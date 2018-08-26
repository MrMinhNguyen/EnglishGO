package sepm.englishgo;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Challenge extends AppCompatActivity {

    public static Word VOCABULARY;
    TextToSpeech t1;
    private static boolean correctPhoto;
    private static boolean correctPron;


    // API
    public static final String FILE_NAME = "temp.jpg";
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = "EnglishGO-TakePhoto";
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private static HashMap<String, Float> resultList = new HashMap();


    // Record pronunciation
    private static String pronunciation;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private ImageView challengeShowImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge);

        FirebaseFirestore.getInstance().collection("word")
                .whereEqualTo("topic",ChooseTopic.TOPIC)
                .whereEqualTo("level",ChooseLevel.LEVEL)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Word> wordList = new ArrayList<>();

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : list) {
                            wordList.add(document.toObject(Word.class));

                        }

                        Random rand = new Random();
                        Word randomWord = wordList.get(rand.nextInt(wordList.size()));

                        VOCABULARY = randomWord;

                        TextView textView = findViewById(R.id.challengeWord);
                        textView.setText(randomWord.getContent());
                    }
                });

        Button takePhoto = findViewById(R.id.challengePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });

        Button recordPron = findViewById(R.id.challengePronunciation);
        recordPron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        challengeShowImage = findViewById(R.id.challengeShowImage);

        correctPhoto = false;
        correctPron = false;

        final Button back = findViewById(R.id.challengeBackButton);
        final Drawable before = getResources().getDrawable(R.drawable.back_before);
        back.setBackground(before);
        final Drawable after = getResources().getDrawable(R.drawable.back_after);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setBackground(after);
                Intent changeView = new Intent( Challenge.this, Rank.class);
                startActivity(changeView);
            }
        });


    }

    protected void onStop() {
        super.onStop();

        final Button back = findViewById(R.id.challengeBackButton);
        final Drawable before = getResources().getDrawable(R.drawable.back_before);
        back.setBackground(before);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
        }

    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Take Photo
        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }


       // Record pronunciation


        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                    pronunciation = result.get(0);
                    System.out.println("Goodbye: "+result.get(0));
                    correctPron = checkPron();

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        check();
                    }
                }, 2000);


                break;
            }

        }

    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        }
        else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        }
        else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);

                detectLabels(bitmap);
                challengeShowImage.setImageBitmap(bitmap);

            }
            catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, "Something is wrong with that image. Pick a different one please.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, "Something is wrong with that image. Pick a different one please.", Toast.LENGTH_LONG).show();
        }
    }

    public void detectLabels(Bitmap bitmap){
        /**Cloud Label Detection (can detect 10,000+ labels, after the first 100 tries will cost)**/
        FirebaseVisionCloudDetectorOptions options =
                new FirebaseVisionCloudDetectorOptions.Builder()
                        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                        .setMaxResults(20)
                        .build();

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionCloudLabelDetector detector = FirebaseVision.getInstance().getVisionCloudLabelDetector(options);
        Task<List<FirebaseVisionCloudLabel>> result =
                detector
                        .detectInImage(image)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionCloudLabel> labels) {
                                        System.out.println("hello");
                                        for (FirebaseVisionCloudLabel label: labels) {
                                            String text = label.getLabel();
                                            String entityId = label.getEntityId();
                                            float confidence = label.getConfidence();
                                            System.out.println("Text: " + text +
                                                                " EntityID: " + entityId +
                                                                " Confidence: "+ confidence);
                                            resultList.put(text, confidence);
                                        }
                                        correctPhoto = checkPhoto();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                check();
                                            }
                                        }, 2000);
                                    }

                                });

        /**On-device Label Detection (can detect 400+ labels, free)**/
//        FirebaseVisionLabelDetectorOptions options =
//                new FirebaseVisionLabelDetectorOptions.Builder()
//                        .setConfidenceThreshold(0.8f)
//                        .build();
//
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
//        FirebaseVisionLabelDetector detector = FirebaseVision.getInstance().getVisionLabelDetector(options);
//        Task<List<FirebaseVisionLabel>> result =
//                detector
//                        .detectInImage(image)
//                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
//                                    @Override
//                                    public void onSuccess(List<FirebaseVisionLabel> labels) {
//                                        System.out.println("hello");
//                                        for (FirebaseVisionLabel label: labels) {
//                                            String text = label.getLabel();
//                                            String entityId = label.getEntityId();
//                                            float confidence = label.getConfidence();
//                                            System.out.println("Text: " + text +
//                                                                " EntityID: " + entityId +
//                                                                " Confidence: "+ confidence);
//                                            resultList.put(text, confidence);
//                                        }
//                                    }
//                                });


    }

    public boolean checkPhoto(){

        for (String s : resultList.keySet()){
            if (s.contains(VOCABULARY.getContent())){
                final ImageView check = findViewById(R.id.challengeCheck);
                check.setImageResource(R.drawable.correct);
                check.setVisibility(View.VISIBLE);
                check.bringToFront();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        check.setVisibility(View.GONE);
                    }
                }, 2000);

                Button takePhoto = findViewById(R.id.challengePhoto);
                Drawable truePhoto = getResources().getDrawable(R.drawable.camera_true);
                takePhoto.setBackground(truePhoto);
                takePhoto.setEnabled(false);
                takePhoto.setClickable(false);

                return true;
            }
        }

        final ImageView check = findViewById(R.id.challengeCheck);
        check.setImageResource(R.drawable.incorrect);
        check.setVisibility(View.VISIBLE);
        check.bringToFront();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                check.setVisibility(View.GONE);
            }
        }, 2000);

        Button takePhoto = findViewById(R.id.challengePhoto);
        Drawable falsePhoto = getResources().getDrawable(R.drawable.camera_false);
        takePhoto.setBackground(falsePhoto);

        return false;
    }

    public boolean checkPron(){

        if (pronunciation.equals(VOCABULARY.getContent())){
            final ImageView check = findViewById(R.id.challengeCheck);
            check.setImageResource(R.drawable.correct);
            check.setVisibility(View.VISIBLE);
            check.bringToFront();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    check.setVisibility(View.GONE);
                }
            }, 2000);

            Button record = findViewById(R.id.challengePronunciation);
            Drawable truePron = getResources().getDrawable(R.drawable.microphone_true);
            record.setBackground(truePron);
            record.setEnabled(false);
            record.setClickable(false);

            return  true;
        }
        else {
            final ImageView check = findViewById(R.id.challengeCheck);
            check.setImageResource(R.drawable.incorrect);
            check.setVisibility(View.VISIBLE);
            check.bringToFront();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    check.setVisibility(View.GONE);
                }
            }, 2000);

            Button record = findViewById(R.id.challengePronunciation);
            Drawable falsePron = getResources().getDrawable(R.drawable.microphone_false);
            record.setBackground(falsePron);

            return false;
        }

    }


    public void check(){
        if (this.correctPhoto && this.correctPron){
            final ImageView check = findViewById(R.id.challengeCheck);
            check.setImageResource(R.drawable.congrat);
            check.setVisibility(View.VISIBLE);
            check.bringToFront();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    check.setVisibility(View.GONE);
                }
            }, 5000);

            MainActivity.POINT++;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent changeView = new Intent( Challenge.this, Result.class);
                    startActivity(changeView);
                }
            }, 2000);

            // Save point
            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.point), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.point), MainActivity.POINT);
            editor.commit();

        }
    }

    public void showHint (View view){
        AlertDialog.Builder hint = new AlertDialog.Builder(this);

        hint
                .setMessage(VOCABULARY.getHint())
                .setTitle("Hint")
                .create();

        hint.show();
    }

    public void textToSpeech(View view){
        try {
            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.UK);
                        t1.speak(VOCABULARY.getContent(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
        }
        catch (NullPointerException e){
            System.out.println("No word error");
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Pronounce the Word");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Sorry device does not support this function",
                    Toast.LENGTH_SHORT).show();
        }
    }

}


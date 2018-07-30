package sepm.englishgo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.googleapis.json.GoogleJsonResponseException;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.vision.v1.Vision;
//import com.google.api.services.vision.v1.VisionRequest;
//import com.google.api.services.vision.v1.VisionRequestInitializer;
//import com.google.api.services.vision.v1.model.AnnotateImageRequest;
//import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
//import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
//import com.google.api.services.vision.v1.model.EntityAnnotation;
//import com.google.api.services.vision.v1.model.Feature;
//import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private static boolean correctPhoto = false;
    private static boolean correctPron = false;

    //API
//    private static final String CLOUD_VISION_API_KEY = BuildConfig.API_KEY;
//
//    public static final String FILE_NAME = "temp.jpg";
//    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
//    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
//    private static final int MAX_LABEL_RESULTS = 20;
//    private static final int MAX_DIMENSION = 1200;
//
//    private static final String TAG = Challenge.class.getSimpleName();
//    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
//    public static final int CAMERA_IMAGE_REQUEST = 3;
//
//    private TextView mImageDetails;
//    private ImageView mMainImage;
//
//    private static HashMap<String, Float> resultList = new HashMap();


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
//        takePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startCamera();
//            }
//        });


    }

    public void check(View v){
        if (this.correctPhoto && this.correctPron){
            Intent changeView = new Intent( Challenge.this, Result.class);
            startActivity(changeView);
        }
    }

    public void getBack(View view){
        Intent changeView = new Intent( Challenge.this, ChooseTopic.class);
        startActivity(changeView);
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

            this.correctPron = true;
            this.correctPhoto = true;

            check(view);
        }
        catch (NullPointerException e){
            System.out.println("No word error");
        }
    }

    //API
//    public void startCamera() {
//        if ( PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST,
//                                                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                                                    Manifest.permission.CAMERA) ) {
//
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//            Uri photoUri = FileProvider.getUriForFile(this,
//                                                    getApplicationContext().getPackageName() + ".provider",
//                                                            getCameraFile());
//
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
//        }
//    }
//
//    public File getCameraFile() {
//        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        return new File(dir, FILE_NAME);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
//            Uri photoUri = FileProvider.getUriForFile(this,
//                                                    getApplicationContext().getPackageName() + ".provider",
//                                                        getCameraFile());
//            uploadImage(photoUri);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
//            startCamera();
//        }
//
//    }
//
//    public void uploadImage(Uri uri) {
//        if (uri != null) {
//            try {
//                // scale the image to save on bandwidth
//                Bitmap bitmap =
//                        scaleBitmapDown(
//                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
//                                MAX_DIMENSION);
//
//                callCloudVision(bitmap);
//                mMainImage.setImageBitmap(bitmap);
//
//            }
//            catch (IOException e) {
//                Log.d(TAG, "Image picking failed because " + e.getMessage());
//            }
//        }
//        else {
//            Log.d(TAG, "Image picker gave us a null image.");
//        }
//    }
//
//    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
//        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
//        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
//
//        VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
//                    /**
//                     * We override this so we can inject important identifying fields into the HTTP
//                     * headers. This enables use of a restricted cloud platform API key.
//                     */
//                    @Override
//                    protected void initializeVisionRequest(VisionRequest<?> visionRequest) throws IOException {
//                        super.initializeVisionRequest(visionRequest);
//
//                        String packageName = getPackageName();
//                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);
//
//                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);
//
//                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
//                    }
//                };
//
//        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
//        builder.setVisionRequestInitializer(requestInitializer);
//
//        Vision vision = builder.build();
//
//        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
//        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
//            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
//
//            // Add the image
//            Image base64EncodedImage = new Image();
//            // Convert the bitmap to a JPEG
//            // Just in case it's a format that Android understands but Cloud Vision
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
//            byte[] imageBytes = byteArrayOutputStream.toByteArray();
//
//            // Base64 encode the JPEG
//            base64EncodedImage.encodeContent(imageBytes);
//            annotateImageRequest.setImage(base64EncodedImage);
//
//            // add the features we want
//            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
//                Feature labelDetection = new Feature();
//                labelDetection.setType("LABEL_DETECTION");
//                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
//                add(labelDetection);
//            }});
//
//            // Add the list of one thing to the request
//            add(annotateImageRequest);
//        }});
//
//        Vision.Images.Annotate annotateRequest =
//                vision.images().annotate(batchAnnotateImagesRequest);
//        // Due to a bug: requests to Vision API containing large images fail when GZipped.
//        annotateRequest.setDisableGZipContent(true);
//        Log.d(TAG, "created Cloud Vision request object, sending request");
//
//        return annotateRequest;
//    }
//
//    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
//        private final WeakReference<Challenge> challengeWeakReference;
//        private Vision.Images.Annotate mRequest;
//
//        LableDetectionTask(Challenge challenge, Vision.Images.Annotate annotate) {
//            challengeWeakReference = new WeakReference<>(challenge);
//            mRequest = annotate;
//        }
//
//        @Override
//        protected String doInBackground(Object... params) {
//            try {
//                Log.d(TAG, "created Cloud Vision request object, sending request");
//                BatchAnnotateImagesResponse response = mRequest.execute();
//                convertResponseToString(response);
//                return "Succeeded doInBackground";
//
//            }
//            catch (GoogleJsonResponseException e) {
//                Log.d(TAG, "failed to make API request because " + e.getContent());
//            }
//            catch (IOException e) {
//                Log.d(TAG, "failed to make API request because of other IOException " +
//                        e.getMessage());
//            }
//            return "Cloud Vision API request failed. Check logs for details.";
//        }
//
//        protected void onPostExecute(String result) {
//            Challenge challenge = challengeWeakReference.get();
//            if (challenge != null && !challenge.isFinishing()) {
//
//                for (String s : resultList.keySet()){
//                    if (resultList.get(s) >= 80 && s.equals(VOCABULARY.getContent())){
//                        Challenge.correctPhoto = true;
//                        break;
//                    }
//                }
//
//            }
//        }
//    }
//
//    private void callCloudVision(final Bitmap bitmap) {
//        // Switch text to loading
////        mImageDetails.setText(R.string.loading_message);
//
//        // Do the real work in an async task, because we need to use the network anyway
//        try {
//            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
//            labelDetectionTask.execute();
//        } catch (IOException e) {
//            Log.d(TAG, "failed to make API request because of other IOException " +
//                    e.getMessage());
//        }
//    }
//
//    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
//
//        int originalWidth = bitmap.getWidth();
//        int originalHeight = bitmap.getHeight();
//        int resizedWidth = maxDimension;
//        int resizedHeight = maxDimension;
//
//        if (originalHeight > originalWidth) {
//            resizedHeight = maxDimension;
//            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
//        }
//        else if (originalWidth > originalHeight) {
//            resizedWidth = maxDimension;
//            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
//        }
//        else if (originalHeight == originalWidth) {
//            resizedHeight = maxDimension;
//            resizedWidth = maxDimension;
//        }
//        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
//    }
//
//    private static void convertResponseToString(BatchAnnotateImagesResponse response) {
//
//        StringBuilder message = new StringBuilder("I found these things:\n\n");
//
//        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
//        if (labels != null) {
//            for (EntityAnnotation label : labels) {
//                message.append(String.format(Locale.US, "%.3f: %s", label.getScore()*100, label.getDescription()));
//                message.append("\n");
//
//                resultList.put(label.getDescription(),label.getScore()*100);
//
//            }
//        }
//        else {
//            message.append("nothing");
//        }
//
//        System.out.println(message.toString());
////        return message.toString();
//    }

}

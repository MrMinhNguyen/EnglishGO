package sepm.englishgo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Challenge extends AppCompatActivity {

    public static Word VOCABULARY;
    TextToSpeech t1;
    private boolean correctPhoto;
    private boolean correctPron;


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

}

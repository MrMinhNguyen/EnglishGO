package sepm.englishgo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Challenge extends AppCompatActivity {

    public static Word VOCABULARY;

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

}

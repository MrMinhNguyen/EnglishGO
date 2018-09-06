package sepm.englishgo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddWord extends AppCompatActivity {

    static String content;
    static String explanation;
    static String hint;
    static int level;
    static String sampleSen;
    static int topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_word);


        final Drawable addWordAfter = getResources().getDrawable(R.drawable.add_word_after);
        final Drawable addWordBefore = getResources().getDrawable(R.drawable.add_word_before);
        final Button addWord = (Button) findViewById(R.id.addWordAdd);
        addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWord.setBackground(addWordAfter);

                System.out.println("Button clicked");

                EditText addContent = findViewById(R.id.addWordContent);
                content = addContent.getText().toString();

                EditText addHint = findViewById(R.id.addWordHint);
                hint = addHint.getText().toString();

                EditText addExplanation = findViewById(R.id.addWordExplanation);
                explanation = addExplanation.getText().toString();

                EditText addSampleSen = findViewById(R.id.addWordSampleSen);
                sampleSen = addSampleSen.getText().toString();



                FirebaseFirestore.getInstance().collection("word")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<Word> wordList = new ArrayList<>();
                                boolean exist = false;

                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot document : list) {
                                    wordList.add(document.toObject(Word.class));
                                }

                                for (Word w : wordList){
                                    if (w.getContent().equals(content)){
                                        exist = true;
                                        break;
                                    }
                                }

                                if (exist){
                                    wordExist(true);
                                }
                                else {
                                    wordExist(false);
                                    Word word = new Word("", content, level, topic, hint, explanation, sampleSen);
                                    CollectionReference docRef = FirebaseFirestore.getInstance().collection("word");
                                    docRef.document().set(word);
                                }
                            }
                        });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addWord.setBackground(addWordBefore);
                    }
                }, 1000);
            }
        });


        final Button back = findViewById(R.id.addWordBack);
        final Drawable backBefore = getResources().getDrawable(R.drawable.back_before);
        back.setBackground(backBefore);
        final Drawable backAfter = getResources().getDrawable(R.drawable.back_after);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setBackground(backAfter);
                Intent changeView = new Intent( AddWord.this, Rank.class);
                startActivity(changeView);
            }
        });

    }

    public void wordExist(boolean exist){
        AlertDialog.Builder hint = new AlertDialog.Builder(this);

        if (exist){
            hint
                    .setMessage("This word has already existed in the database")
                    .create();
        }
        else {
            hint
                    .setMessage("Word added")
                    .create();
        }


        hint.show();
    }

    protected void onStop() {
        super.onStop();

        final Button addWord = findViewById(R.id.addWordAdd);
        final Drawable before = getResources().getDrawable(R.drawable.add_word_before);
        addWord.setBackground(before);
    }

    public void pickLevel(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.easyLevel:
                if (checked)
                    level = 1;
                    break;
            case R.id.mediumLevel:
                if (checked)
                    level = 2;
                    break;
            case R.id.hardLevel:
                if (checked)
                    level = 3;
                    break;
        }
    }

    public void pickTopic(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.topicHome:
                if (checked)
                    topic = 3;
                    break;
            case R.id.topicSchool:
                if (checked)
                    topic = 1;
                    break;
            case R.id.topicZoo:
                if (checked)
                    topic = 2;
                    break;
        }
    }

}

package sepm.englishgo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class Result extends AppCompatActivity {

    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        TextView word = findViewById(R.id.resultWord);
        TextView explanation = findViewById(R.id.explanation);
        TextView sampleSen = findViewById(R.id.sampleSen);

        word.setText((Challenge.VOCABULARY.getContent()));
        explanation.setText("Explanation:\n"+Challenge.VOCABULARY.getExplanation());
        sampleSen.setText("Sample Sentence:\n"+Challenge.VOCABULARY.getSampleSen());

        // Back button
        final Button back = findViewById(R.id.resultBackButton);
        final Drawable beforeBack = getResources().getDrawable(R.drawable.back_before);
        back.setBackground(beforeBack);
        final Drawable afterBack = getResources().getDrawable(R.drawable.back_after);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setBackground(afterBack);
                Intent changeView = new Intent( Result.this, Rank.class);
                startActivity(changeView);
            }
        });

        // Next button
        final Button start = findViewById(R.id.topicNextButton);
        final Drawable beforeStart = getResources().getDrawable(R.drawable.foot_before);
        start.setBackground(beforeStart);
        final Drawable afterStart = getResources().getDrawable(R.drawable.foot_after);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setBackground(afterStart);
                Intent changeView = new Intent( Result.this, Challenge.class);
                startActivity(changeView);
            }
        });
    }

    protected void onStop() {
        super.onStop();

        final Button back = findViewById(R.id.resultBackButton);
        final Drawable beforeBack = getResources().getDrawable(R.drawable.back_before);
        back.setBackground(beforeBack);

        final Button start = findViewById(R.id.topicNextButton);
        final Drawable beforeStart = getResources().getDrawable(R.drawable.foot_before);
        start.setBackground(beforeStart);
    }

    public void textToSpeech(View view){
        try {
            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.UK);
                        t1.speak(Challenge.VOCABULARY.getContent(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
        }
        catch (NullPointerException e){
            System.out.println("No word error");
        }
    }

    public void getBack(View view){
        Intent changeView = new Intent(Result.this, Rank.class);
        startActivity(changeView);
    }


}

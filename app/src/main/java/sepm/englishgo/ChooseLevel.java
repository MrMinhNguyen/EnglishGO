package sepm.englishgo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ChooseLevel extends AppCompatActivity {

    public static int LEVEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_level);

        Button btn1 = (Button)findViewById(R.id.level1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LEVEL = 1;
                openChallenge(v);
            }
        });

        Button btn2 = (Button)findViewById(R.id.level2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LEVEL = 2;
                openChallenge(v);
            }
        });

        Button btn3 = (Button)findViewById(R.id.level3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LEVEL = 3;
                openChallenge(v);
            }
        });


        final Button back = findViewById(R.id.levelBackButton);
        final Drawable before = getResources().getDrawable(R.drawable.back_before);
        back.setBackground(before);
        final Drawable after = getResources().getDrawable(R.drawable.back_after);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setBackground(after);
                Intent changeView = new Intent( ChooseLevel.this, ChooseTopic.class);
                startActivity(changeView);
            }
        });

    }

    protected void onStop() {
        super.onStop();

        final Button back = findViewById(R.id.levelBackButton);
        final Drawable before = getResources().getDrawable(R.drawable.back_before);
        back.setBackground(before);
    }

    public void openChallenge(View view){
        Intent changeView = new Intent( ChooseLevel.this, Challenge.class);
        startActivity(changeView);
    }

}

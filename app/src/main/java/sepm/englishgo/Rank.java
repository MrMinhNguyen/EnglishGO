package sepm.englishgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class Rank extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank);

        TextView point = (TextView) findViewById(R.id.rankPoint);
        point.setText("+"+MainActivity.POINT);

        TextView rankText = (TextView) findViewById(R.id.rankRank);
        rankText.setText(rankText(MainActivity.POINT));

        ImageView rankImage = (ImageView) findViewById(R.id.rankImage);
        rankImage.setImageResource(rankImage(MainActivity.POINT));

        final Button start = findViewById(R.id.rankStartButton);
        final Drawable before = getResources().getDrawable(R.drawable.foot_before);
        start.setBackground(before);
        final Drawable after = getResources().getDrawable(R.drawable.foot_after);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setBackground(after);
                Intent changeView = new Intent( Rank.this, ChooseTopic.class);
                startActivity(changeView);
            }
        });

        // Reset point
        final Button reset = findViewById(R.id.rankReset);
        final Drawable resetAfter = getResources().getDrawable(R.drawable.reset_after);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset.setBackground(resetAfter);
                MainActivity.POINT = 0;
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.point), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.point), MainActivity.POINT);
                editor.commit();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    protected void onStop() {
        super.onStop();

        final Button start = findViewById(R.id.rankStartButton);
        final Drawable before = getResources().getDrawable(R.drawable.foot_before);
        start.setBackground(before);
    }

    private String rankText(int point){
        if (point <= 1){
            return "Normal";
        }
        else if (point > 1 && point <= 2){
            return "Clever";
        }
        else if (point > 2 && point <= 3){
            return "Smart";
        }
        else if (point > 2 && point <= 3){
            return "Gifted";
        }
        else {
            return "Brilliant";
        }
    }

    private int rankImage(int point){
        if (point <= 1){
            return R.drawable.rank_1;
        }
        else if (point > 1 && point <= 2){
            return R.drawable.rank_2;
        }
        else if (point > 2 && point <= 3){
            return R.drawable.rank_3;
        }
        else if (point > 3 && point <= 4){
            return R.drawable.rank_4;
        }
        else {
            return R.drawable.rank_5;
        }
    }
}

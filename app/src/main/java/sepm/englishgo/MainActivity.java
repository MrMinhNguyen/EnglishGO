package sepm.englishgo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    public static int POINT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView appName = findViewById(R.id.appName);
        appName.bringToFront();

        final Button start = findViewById(R.id.homeStartButton);
        final Drawable before = getResources().getDrawable(R.drawable.foot_before);
        start.setBackground(before);
        final Drawable after = getResources().getDrawable(R.drawable.foot_after);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setBackground(after);
                Intent changeView = new Intent( MainActivity.this, Rank.class);
                startActivity(changeView);
            }
        });

        try {
            DataInputStream textFileStream = new DataInputStream(getAssets().open(String.format("point.txt")));
            Scanner sc = new Scanner(textFileStream);
            while (sc.hasNextLine()) {
                String aLine = sc.nextLine();

                POINT = Integer.parseInt(aLine);

                System.out.println(POINT);
            }
            sc.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void onStop() {
        super.onStop();

        final Button start = findViewById(R.id.homeStartButton);
        final Drawable before = getResources().getDrawable(R.drawable.foot_before);
        start.setBackground(before);
    }



}

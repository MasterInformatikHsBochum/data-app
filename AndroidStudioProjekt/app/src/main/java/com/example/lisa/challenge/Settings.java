package com.example.lisa.challenge;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.widget.Button;

public class Settings extends MyNavigation {

    Button buttonZuruecksetzen;
    HighScore highScore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setActivity(Settings.this, R.layout.settings);
        super.onCreate(savedInstanceState);
        highScore = new HighScore(getSharedPreferences("Challange", Context.MODE_PRIVATE));
        buttonZuruecksetzen = findViewById(R.id.buttonZuruecksetzen);
        buttonZuruecksetzen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highScore.clear_all();

                //Alle Tasks müssen zurück gesetzt werden, damit alte Highscore auf der Oberlläche nicht mehr sichtbar ist.
                Intent broadJumpPage = new Intent(activity, BroadJump.class);
                broadJumpPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(broadJumpPage);

                Intent highJumpPage = new Intent(activity, HighJump.class);
                highJumpPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(highJumpPage);

                Intent rotatePage = new Intent(activity, Rotate.class);
                rotatePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(rotatePage);

                Intent main = new Intent(activity, MainActivity.class);
                startActivity(main);
                finish();
            }
                                               }
        );


    }

}

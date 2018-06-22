package com.example.lisa.challenge;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

public class Settings extends MyNavigation {

    Button buttonZuruecksetzen;
    HighScore highScore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setActivity(Settings.this, R.layout.settings);
        super.onCreate(savedInstanceState);
        highScore = new HighScore(this);
        buttonZuruecksetzen = findViewById(R.id.buttonZuruecksetzen);
        buttonZuruecksetzen.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       highScore.clear_all();
                                                   }
                                               }
        );


    }

}

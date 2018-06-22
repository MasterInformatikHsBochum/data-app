package com.example.lisa.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BroadJump extends MyNavigation {

    ChallangeAction challange;
    Button navigateToMainMenu;

    TextView highscoreText;
    HighScore highScore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setActivity(BroadJump.this, R.layout.broadjump);
        super.onCreate(savedInstanceState);
        challange = new ChallangeAction();
        highScore = new HighScore(this);
        setUpIds();
        setUpListenerButtons();
    }

    private void setUpListenerButtons() {

    }

    private void setUpIds() {
        highscoreText = findViewById(R.id.highscoreBroadjump);
        //Lade die existierenden Werte aus der Datenbank. Wurden noch keine Werte definiert, wird standardmäßig 0 eingetragen
        float[] w_score = highScore.get_WS_score();
        highscoreText.setText(challange.createHiscoreString(w_score[0],w_score[1],w_score[2],"cm"));
    }

    public void ueberTrageMessergebnis() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //TODO einkommentieren, wenn implemntiert
                //highScore.new_HS_score(getGemesseneWeiteZentimeter());
                //
                // Lade die existierenden Werte aus der Datenbank. Wurden noch keine Werte definiert, wird standardmäßig 0 eingetragen
                float[] w_score = highScore.get_WS_score();
                highscoreText.setText(challange.createHiscoreString(w_score[0],w_score[1],w_score[2],"cm"));
            }
        });
    }
}

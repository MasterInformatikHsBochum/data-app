package com.example.lisa.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class BroadJump extends MyNavigation {

    Button navigateToMainMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setActivity(BroadJump.this, R.layout.broadjump);
        super.onCreate(savedInstanceState);
        setUpIds();
        setUpListenerButtons();
    }

    private void setUpListenerButtons() {

    }

    private void setUpIds() {
    }
}

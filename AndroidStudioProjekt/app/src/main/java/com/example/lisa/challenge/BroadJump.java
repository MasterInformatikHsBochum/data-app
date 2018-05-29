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
        navigateToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivityPage = new Intent(BroadJump.this, MainActivity.class);
                startActivity(mainActivityPage);
            }
        });
    }

    private void setUpIds() {
        navigateToMainMenu = findViewById(R.id.buttonNavigateFromBroadJumpToMain);
    }
}

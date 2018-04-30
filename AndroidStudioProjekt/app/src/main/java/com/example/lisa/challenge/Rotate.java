package com.example.lisa.challenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

public class Rotate extends Activity {

    Button navigateToMainMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotate);
        setUpIds();
        setUpListenerButtons();
    }

    private void setUpListenerButtons() {
        navigateToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivityPage = new Intent(Rotate.this, MainActivity.class);
                startActivity(mainActivityPage);
            }
        });
    }

    private void setUpIds() {
        navigateToMainMenu = findViewById(R.id.buttonNavigateFromRotateToMain);
    }
}

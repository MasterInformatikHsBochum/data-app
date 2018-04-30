package com.example.lisa.challenge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button navigateToRotate;
    Button navigateToHighJump;
    Button navigateToBroadJump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpIds();
        setUpListenerButtons();
    }

    private void setUpListenerButtons()
    {
        navigateToBroadJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent broadJumpPage = new Intent(MainActivity.this, BroadJump.class);
                startActivity(broadJumpPage);
            }
        });
        navigateToHighJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent highJumpPage = new Intent(MainActivity.this, HighJump.class);
                startActivity(highJumpPage);
            }
        });
        navigateToRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rotatePage = new Intent(MainActivity.this, Rotate.class);
                startActivity(rotatePage);
            }
        });

    }

    private void setUpIds(){
        navigateToRotate = findViewById(R.id.buttonNavigateToRotate);
        navigateToHighJump = findViewById(R.id.buttonNavigateToHighJump);
        navigateToBroadJump = findViewById(R.id.buttonNavigateToBoardJump);

    }

}

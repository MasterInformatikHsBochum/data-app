package com.example.lisa.challenge;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.example.lisa.challenge.GifImageView;

public class MainActivity extends MyNavigation{

    Button navigateToRotate;
    Button navigateToHighJump;
    Button navigateToBroadJump;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Navigation erzeugen. Die aufrufende Aktivitaet muss fuer die Abstracte Klasse gesetzt werden
        setActivity(MainActivity.this);
        super.onCreate(savedInstanceState);

        setUpIds();
        setUpListenerButtons();
        GifImageView gifImageView = (GifImageView) findViewById(R.id.GifImageView);
        gifImageView.setGifImageResource(R.drawable.giphy);
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



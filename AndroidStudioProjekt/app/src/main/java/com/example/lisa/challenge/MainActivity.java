package com.example.lisa.challenge;

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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Button navigateToRotate;
    Button navigateToHighJump;
    Button navigateToBroadJump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpNavigation();
        setUpIds();
        setUpListenerButtons();
        GifImageView gifImageView = (GifImageView) findViewById(R.id.GifImageView);
        gifImageView.setGifImageResource(R.drawable.giphy);
    }

    //------------------------Navigation der Seiten------------------------
   protected void setUpNavigation(){
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);

       DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
               this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
       drawer.addDrawerListener(toggle);
       toggle.syncState();

       NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
       navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_broadJump) {
            Intent broadJumpPage = new Intent(MainActivity.this, BroadJump.class);
            startActivity(broadJumpPage);
        } else if (id == R.id.nav_highJump) {
            Intent highJumpPage = new Intent(MainActivity.this, HighJump.class);
            startActivity(highJumpPage);
        } else if (id == R.id.nav_rotate) {
            Intent rotatePage = new Intent(MainActivity.this, Rotate.class);
            startActivity(rotatePage);
        } else if (id == R.id.nav_impressum) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //----------------------------Navitagtion Ende----------------

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



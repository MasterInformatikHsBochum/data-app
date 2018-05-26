package com.example.lisa.challenge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public abstract class MyNavigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Activity activity;

    protected void setActivity(Activity activity)
    {
        this.activity=activity;
        this.drawer=drawer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        drawer= (DrawerLayout) findViewById(R.id.drawer_layout);

        super.onCreate(savedInstanceState);
        setUpNavigation();
    }

        //------------------------Navigation der Seiten------------------------
        protected void setUpNavigation(){
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }

        @Override
        public void onBackPressed() {
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
//FIXME
            if (id == R.id.nav_broadJump) {
                Intent broadJumpPage = new Intent(activity, BroadJump.class);
                startActivity(broadJumpPage);
            } else if (id == R.id.nav_highJump) {
                Intent highJumpPage = new Intent(activity, HighJump.class);
                startActivity(highJumpPage);
            } else if (id == R.id.nav_rotate) {
                Intent rotatePage = new Intent(activity, Rotate.class);
                startActivity(rotatePage);
            } else if (id == R.id.nav_impressum) {

            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        }



}

package ru.softmine.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class AboutActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = AboutActivity.class.getName();
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        intFab();
        initDrawer();
    }

    private void intFab() {
        FloatingActionButton fab = findViewById(R.id.fab_email);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, R.string.author_contacts);
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.default_subject);

                startActivity(intent);
            }
        });
    }

    private void initDrawer() {
        drawer = findViewById(R.id.drawer_layout_about);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                startActivity(new Intent(AboutActivity.this, MainActivity.class));
                break;

            case R.id.nav_cities:
                startActivity(new Intent(AboutActivity.this, SelectCityActivity.class));
                break;

            case R.id.nav_history:
                startActivity(new Intent(AboutActivity.this, HistoryActivity.class));
                break;

            case R.id.nav_settings:
                startActivity(new Intent(AboutActivity.this, SettingsActivity.class));
                break;

            case R.id.nav_about:
                // Do nothing
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
}
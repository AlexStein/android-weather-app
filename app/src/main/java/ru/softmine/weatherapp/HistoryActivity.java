package ru.softmine.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import ru.softmine.weatherapp.history.HistoryAdapter;
import ru.softmine.weatherapp.history.HistoryDataSource;

public class HistoryActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HistoryActivity.class.getName();
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initDrawer();

        RecyclerView recyclerView = findViewById(R.id.history_recycler_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new HistoryAdapter(HistoryDataSource.historySource));
    }

    private void initDrawer() {
        drawer = findViewById(R.id.drawer_layout_history);
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
                startActivity(new Intent(HistoryActivity.this, MainActivity.class));
                break;

            case R.id.nav_cities:
                startActivity(new Intent(HistoryActivity.this, SelectCityActivity.class));
                break;

            case R.id.nav_history:
                startActivity(new Intent(HistoryActivity.this, HistoryActivity.class));
                break;

            case R.id.nav_settings:
                startActivity(new Intent(HistoryActivity.this, SettingsActivity.class));
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
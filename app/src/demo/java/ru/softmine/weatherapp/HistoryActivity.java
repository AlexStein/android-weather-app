package ru.softmine.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.history.HistoryAdapter;
import ru.softmine.weatherapp.history.HistoryDataSource;

public class HistoryActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HistoryActivity.class.getName();

    private HistoryAdapter adapter;
    private HistoryDataSource historyDataSource;

    private DrawerLayout drawer;

    private Handler.Callback updateCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        historyDataSource = new HistoryDataSource();
        adapter = new HistoryAdapter(historyDataSource);
        updateCallback = new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Logger.DEBUG) {
                            Log.d(TAG, "updateCallback()");
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            }
        };
        WeatherApp.getDatabaseHandler().loadHistory(historyDataSource, updateCallback);

        initDrawer();
        initRecyclerView();
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

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.history_recycler_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Декоратор
        DividerItemDecoration itemDecoration = new DividerItemDecoration(WeatherApp.getAppContext(),
                LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.decorator));
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);

        final SearchView searchViewAndroidActionBar = (SearchView) searchViewItem.getActionView();
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                WeatherApp.getDatabaseHandler().searchHistory(historyDataSource, query, updateCallback);
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    WeatherApp.getDatabaseHandler().loadHistory(historyDataSource, updateCallback);
                    searchViewAndroidActionBar.clearFocus();
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                toggleDrawer();
                return true;

            case R.id.action_search:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.nav_cities:
                startActivity(new Intent(this, CitiesActivity.class));
                break;

            case R.id.nav_history:
                startActivity(new Intent(this, HistoryActivity.class));
                break;

            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
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

    private void toggleDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }
}
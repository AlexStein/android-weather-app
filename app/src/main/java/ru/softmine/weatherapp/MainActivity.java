package ru.softmine.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int CITY_RESULT = 0xAA;
    private static final int SETTING_CODE = 0xBB;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer();

        if (savedInstanceState == null) {
            WeekForecastFragment forecast = new WeekForecastFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, forecast).commit();
        }
    }

    private void initDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Переход на выбор города, по клику на наименовании текущего города
     *
     * @param view TextView с именем города @id/cityName
     */
    public void cityOnClick(View view) {
        Intent intent = new Intent(this, SelectCityActivity.class);
        startActivityForResult(intent, CITY_RESULT);
    }

    /**
     * Переход на страницу с информацией о городе. Например на Wiki.
     *
     * @param view Button
     */
    public void onButtonInfoClick(View view) {
        String cityName = CityModel.getInstance().getCityName();
        String url = String.format(getString(R.string.wiki_url_format), cityName);

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != CITY_RESULT) {
            super.onActivityResult(requestCode, resultCode, data);

            // Вернулись из настроек
            if (requestCode == SETTING_CODE) {
                recreate();
            }
            return;
        }

        if (resultCode == RESULT_OK) {
            if (data == null) {
                if (Logger.DEBUG) {
                    Log.d(TAG, "onActivityResult: data is null");
                }
                return;
            }

            String cityName = data.getStringExtra(BundleKeys.CITY_NAME);
            CurrentWeatherFragment fragment = (CurrentWeatherFragment)
                    getSupportFragmentManager().findFragmentById(R.id.current_weather);
            if (fragment != null) {
                fragment.setCity(cityName);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                return true;

            case R.id.action_change_city:
                Intent intentCity = new Intent(this, SelectCityActivity.class);
                startActivityForResult(intentCity, CITY_RESULT);
                return true;

            case R.id.action_history:
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                return true;

            case R.id.action_settings:
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intentSettings, SETTING_CODE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                // Do nothing
                break;

            case R.id.nav_cities:
                Intent intentCity = new Intent(this, SelectCityActivity.class);
                startActivityForResult(intentCity, CITY_RESULT);
                break;

            case R.id.nav_history:
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                break;

            case R.id.nav_settings:
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intentSettings, SETTING_CODE);
                break;

            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
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
package ru.softmine.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import ru.softmine.weatherapp.cities.CityModel;
import ru.softmine.weatherapp.dialogs.CitySelectDialogFragment;
import ru.softmine.weatherapp.dialogs.ErrorDialog;
import ru.softmine.weatherapp.dialogs.OnDialogListener;
import ru.softmine.weatherapp.interfaces.OnFragmentErrorListener;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int CITY_RESULT = 0xAA;
    private static final int SETTING_CODE = 0xBB;

    private DrawerLayout drawer;
    private CitySelectDialogFragment citySelectDialogFragment;

    private CurrentWeatherFragment currentWeatherFragment;
    private WeekForecastFragment forecastFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentWeatherFragment = (CurrentWeatherFragment) getSupportFragmentManager().findFragmentById(R.id.current_weather);
        if (currentWeatherFragment != null) {
            currentWeatherFragment.setOnErrorListener(getErrorListener());
        }

        initDrawer();

        if (savedInstanceState == null) {
            forecastFragment = new WeekForecastFragment();
            forecastFragment.setOnErrorListener(getErrorListener());

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, forecastFragment).commit();
        }

        if (forecastFragment != null) {
            forecastFragment.setOnErrorListener(getErrorListener());
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
     * Листенер для результата выбора города в диалоге
     * final так как поведение присваивается один раз и не меняется.
     */
    private final OnDialogListener dialogCitySelectListener = new OnDialogListener() {
        @Override
        public void onDialogApply() {
            String cityName = citySelectDialogFragment.getCityName();
            if (currentWeatherFragment != null) {
                currentWeatherFragment.setCity(cityName);
            }
            if (forecastFragment != null) {
                forecastFragment.update();
            }
        }
    };

    private final OnFragmentErrorListener errorListener = new OnFragmentErrorListener() {
        @Override
        public void onFragmentError(String message) {
            showError(message);
        }
    };

    public OnFragmentErrorListener getErrorListener() {
        return errorListener;
    }

    /**
     * Переход на выбор города, по клику на наименовании текущего города
     * Вызывается диалог.
     *
     * @param view TextView с именем города @id/cityName
     */
    public void cityOnClick(View view) {
        citySelectDialogFragment = CitySelectDialogFragment.newInstance();
        citySelectDialogFragment.setOnDialogListener(dialogCitySelectListener);
        citySelectDialogFragment.setOnErrorListener(errorListener);
        citySelectDialogFragment.show(getSupportFragmentManager(), "city_select_dialog_fragment");
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
        super.onActivityResult(requestCode, resultCode, data);

        // Вернулись из настроек, обновим тему
        if (requestCode == SETTING_CODE) {
            recreate();
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
                currentWeatherFragment.update();
                forecastFragment.update();
                return true;

            case R.id.action_change_city:
                startActivity(new Intent(this, CitiesActivity.class));
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
                startActivity(new Intent(this, CitiesActivity.class));
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

    private void showError(String message) {
        ErrorDialog errDlg = ErrorDialog.newInstance();
        errDlg.setMessage(message);
        errDlg.show(getSupportFragmentManager(), "error_dialog_fragment");
    }
}
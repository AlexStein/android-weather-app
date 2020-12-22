package ru.softmine.weatherapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.softmine.weatherapp.broadcastreceivers.BatteryStateReceiver;
import ru.softmine.weatherapp.broadcastreceivers.WifiStateReceiver;
import ru.softmine.weatherapp.constants.BundleKeys;
import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.constants.Notifications;
import ru.softmine.weatherapp.constants.PrefKeys;
import ru.softmine.weatherapp.dialogs.CitySelectDialogFragment;
import ru.softmine.weatherapp.dialogs.ErrorDialog;
import ru.softmine.weatherapp.interfaces.OnDialogListener;
import ru.softmine.weatherapp.interfaces.OnFragmentErrorListener;
import ru.softmine.weatherapp.interfaces.OpenWeatherAPI;
import ru.softmine.weatherapp.openweathermodel.CityParser;
import ru.softmine.weatherapp.openweathermodel.WeatherParser;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int SETTING_CODE = 0xBB;

    private BroadcastReceiver connectivityReceiver = new WifiStateReceiver();
    private BroadcastReceiver batteryReceiver = new BatteryStateReceiver();

    private SharedPreferences sharedPref;

    private DrawerLayout drawer;
    private CitySelectDialogFragment citySelectDialogFragment;

    private WeekForecastFragment forecastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Регистрируем ресивер для действий связанных с соединением
        IntentFilter connectivityFilter = new IntentFilter();
        connectivityFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        connectivityFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        connectivityFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(connectivityReceiver, connectivityFilter);

        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryFilter.addAction(Intent.ACTION_BATTERY_LOW);

        registerReceiver(batteryReceiver, batteryFilter);

        if (Logger.DEBUG) {
            Log.d(TAG, "onCreate()");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sharedPref = getPreferences(MODE_PRIVATE);

        // Горорд по-умолчанию или последний город из настроек
        String defaultCity = sharedPref.getString(PrefKeys.LAST_CITY_NAME,
                getString(R.string.moscow_city));
        WeatherApp.getWeatherParser().setCity(defaultCity);

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

        updateWeather();
        initDrawer();
        initNotificationChannel();
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

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(
                    Notifications.CHANNEL_ID, Notifications.CHANNEL_NAME, importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateWeather() {
        OpenWeatherAPI openWeatherAPI = WeatherApp.getWeatherApiHolder().getOpenWeather();

        String cityName = WeatherApp.getWeatherParser().getCityName();
        String apiKey = BuildConfig.WEATHER_API_KEY;

        openWeatherAPI.loadCity(cityName, apiKey).enqueue(new Callback<CityParser>() {
            @Override
            public void onResponse(Call<CityParser> call, Response<CityParser> response) {
                CityParser cityParser = response.body();

                if (cityParser == null) {
                    return;
                }

                //TODO: Добавим город в БД, так как этот метод будет вызываться,
                //      если ищем какой-то новый город.

                // Город найден, поэтому можно сохранить
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(PrefKeys.LAST_CITY_NAME, cityParser.getName());
                editor.apply();

                WeatherApp.getWeatherParser().setCity(cityParser.getName());
                float lat = cityParser.getLat();
                float lon = cityParser.getLon();

                openWeatherAPI.loadWeather(lat, lon, "minutely,hourly,alerts",
                        apiKey).enqueue(new Callback<WeatherParser>() {
                    @Override
                    public void onResponse(Call<WeatherParser> call, Response<WeatherParser> response) {
                        WeatherParser weatherParser = response.body();
                        if (weatherParser != null) {
                            WeatherApp.getWeatherParser().updateWeather(
                                    weatherParser.getCurrent(), weatherParser.getDaily());
                            WeatherApp.getWeatherParser().notifyObservers();
                            WeatherApp.getDatabaseHandler().updateHistory();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherParser> call, Throwable t) {
                        if (Logger.DEBUG && t.getMessage() != null) {
                            Log.e(TAG, t.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<CityParser> call, Throwable t) {
                if (Logger.DEBUG && t.getMessage() != null) {
                    Log.e(TAG, t.getMessage());
                }
            }
        });

    }

    /**
     * Листенер для результата выбора города в диалоге
     * final так как поведение присваивается один раз и не меняется.
     */
    private final OnDialogListener dialogCitySelectListener = new OnDialogListener() {
        @Override
        public void onDialogApply() {
            WeatherApp.getWeatherParser().setCity(citySelectDialogFragment.getCityName());
            updateWeather();
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
        String cityName = WeatherApp.getWeatherParser().getCityName();
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
            if (data != null && data.getBooleanExtra(BundleKeys.THEME_CHANGED, false)) {
                recreate();
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
            case android.R.id.home:
                toggleDrawer();
                return true;

            case R.id.action_refresh:
                updateWeather();
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
                break;

            case R.id.nav_cities:
                startActivity(new Intent(this, CitiesActivity.class));
                break;

            case R.id.nav_history:
                startActivity(new Intent(this, HistoryActivity.class));
                break;

            case R.id.nav_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivityForResult(intentSettings, SETTING_CODE);
                break;

            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void toggleDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityReceiver);
        unregisterReceiver(batteryReceiver);
    }
}
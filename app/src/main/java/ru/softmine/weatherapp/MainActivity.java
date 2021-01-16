package ru.softmine.weatherapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
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
import ru.softmine.weatherapp.constants.MapDefaults;
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

    private static final int MAPS_CODE = 0xAA;
    private static final int SETTING_CODE = 0xBB;

    private final BroadcastReceiver connectivityReceiver = new WifiStateReceiver();
    private final BroadcastReceiver batteryReceiver = new BatteryStateReceiver();

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
        float lat = sharedPref.getFloat(PrefKeys.LAST_LAT, -1);
        float lon = sharedPref.getFloat(PrefKeys.LAST_LON, -1);

        WeatherApp.getWeatherParser().setCity(defaultCity, lat, lon);

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

        requestLocationPermissions();
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
        String cityName = WeatherApp.getWeatherParser().getCityName();
        float lat = WeatherApp.getWeatherParser().getLat();
        float lon  = WeatherApp.getWeatherParser().getLon();

        verifyCity(cityName, lat, lon);

        lat = WeatherApp.getWeatherParser().getLat();
        lon  = WeatherApp.getWeatherParser().getLon();
        updateWeather(lat, lon);
    }

    private void updateWeather(float lat, float lon) {
        OpenWeatherAPI openWeatherAPI = WeatherApp.getWeatherApiHolder().getOpenWeather();
        String apiKey = BuildConfig.WEATHER_API_KEY;
        String lang = getResources().getString(R.string.locale);

        openWeatherAPI.loadWeather(lat, lon, "minutely,hourly,alerts",
                apiKey, lang).enqueue(new Callback<WeatherParser>() {
            @Override
            public void onResponse(Call<WeatherParser> call, Response<WeatherParser> response) {
                WeatherParser weatherParser = response.body();
                if (Logger.DEBUG) {
                    Log.d(TAG, "BODY: " + response.raw().toString());
                }
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

    private void verifyCity(String cityName, float lat, float lon) {
        OpenWeatherAPI openWeatherAPI = WeatherApp.getWeatherApiHolder().getOpenWeather();
        String apiKey = BuildConfig.WEATHER_API_KEY;
        String lang = getResources().getString(R.string.locale);

        if (lat != -1 && lon != -1) {
            openWeatherAPI.loadCity(lat, lon, apiKey, lang).enqueue(new Callback<CityParser>() {
                @Override
                public void onResponse(Call<CityParser> call, Response<CityParser> response) {
                    updateCityData(response.body());
                }

                @Override
                public void onFailure(Call<CityParser> call, Throwable t) {
                    if (Logger.DEBUG && t.getMessage() != null) {
                        Log.e(TAG, t.getMessage());
                    }
                }
            });
        } else {
            openWeatherAPI.loadCity(cityName, apiKey, lang).enqueue(new Callback<CityParser>() {
                @Override
                public void onResponse(Call<CityParser> call, Response<CityParser> response) {
                    updateCityData(response.body());
                }

                @Override
                public void onFailure(Call<CityParser> call, Throwable t) {
                    if (Logger.DEBUG && t.getMessage() != null) {
                        Log.e(TAG, t.getMessage());
                    }
                }
            });
        }
    }

    /**
     * Обновить данные о текущем расположении, наименование города, координаты.
     *
     * @param cityParser парсер запроса погоды, возвращающий даные о городе
     */
    private void updateCityData(CityParser cityParser) {
        if (cityParser == null) {
            return;
        }

        float lat = cityParser.getLat();
        float lon = cityParser.getLon();

        if (Logger.DEBUG) {
            Log.d(TAG, String.format("name=%s",cityParser.getName()));
            Log.d(TAG, String.format("lat=%f", lat));
            Log.d(TAG, String.format("lon=%f", lon));
        }

        // Город найден, поэтому можно сохранить
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PrefKeys.LAST_CITY_NAME, cityParser.getName());
        editor.putFloat(PrefKeys.LAST_LAT, lat);
        editor.putFloat(PrefKeys.LAST_LON, lon);
        editor.apply();

        WeatherApp.getWeatherParser().setCity(cityParser.getName(), lat, lon);
    }

    /**
     * Листенер для результата выбора города в диалоге
     * final так как поведение присваивается один раз и не меняется.
     */
    private final OnDialogListener dialogCitySelectListener = new OnDialogListener() {
        @Override
        public void onDialogApply() {
            WeatherApp.getWeatherParser().setCity(citySelectDialogFragment.getCityName(), -1, -1);
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

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            float lat = (float) location.getLatitude();// Широта
            float lon = (float)location.getLongitude();// Долгота

            WeatherApp.getWeatherParser().setCity(getString(R.string.location_unknown), lat, lon);
            updateWeather();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

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

        switch (requestCode) {
            case MAPS_CODE:
                // На карте выбрали новый город
                if (resultCode == RESULT_OK) {
                    updateWeather();
                }
                break;

            case SETTING_CODE:
                // Вернулись из настроек, обновим тему
                if (data != null && data.getBooleanExtra(BundleKeys.THEME_CHANGED, false)) {
                    recreate();
                }
                break;
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

            case R.id.action_current_location:
                requestLocation();
                return true;

            case R.id.action_select_on_map:
                float lat = WeatherApp.getWeatherParser().getLat();
                float lon  = WeatherApp.getWeatherParser().getLon();

                Intent intentMaps = new Intent(MainActivity.this, MapsActivity.class);
                intentMaps.putExtra(BundleKeys.LATITUDE, lat);
                intentMaps.putExtra(BundleKeys.LONGITUDE, lon);
                startActivityForResult(intentMaps, MAPS_CODE);
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

    /**
     * Проверить разрешение на получение координат, если их нет - запросить.
     * Будем проверять при каждом запросе координат пользователем.
     */
    private void requestPermissions() {
        // Проверим на пермиссии, и если их нет, запросим у пользователя
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
        }
    }

    /**
     * Запросить разрешение пользователя для определени положения устройства
     */
    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MapDefaults.PERMISSION_REQUEST_CODE);
        }
    }


    /**
     * Запросить текущее положение или получить последнее известное
     */
    private void requestLocation() {
        requestPermissions();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Logger.DEBUG) {
                Log.d(TAG, "No permissions");
            }
            return;
        }

        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            final Looper locationLooper = Looper.myLooper();
            locationManager.requestSingleUpdate(provider, locationListener, locationLooper);
        } else {
            // Получим последнее известное положение, если какие то проблемы с определением
            // текущего положения
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                locationListener.onLocationChanged(location);
                return;
            }
        }
    }

}
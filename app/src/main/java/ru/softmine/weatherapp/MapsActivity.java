package ru.softmine.weatherapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import ru.softmine.weatherapp.constants.BundleKeys;
import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.constants.MapDefaults;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getName();

    private GoogleMap googleMap;
    private TextView textLocation;
    private float lat;
    private float lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textLocation = findViewById(R.id.textLocation);
        Intent intent = getIntent();

        lat = intent.getFloatExtra(BundleKeys.LATITUDE, MapDefaults.MOSCOW_LAT);
        lon = intent.getFloatExtra(BundleKeys.LONGITUDE, MapDefaults.MOSCOW_LON);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        LatLng pos = new LatLng(lat, lon);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pos)
                .zoom(MapDefaults.ZOOM)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);

        googleMap.addMarker(new MarkerOptions().position(pos).title(getString(R.string.im_here)));

        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setMyLocationButtonEnabled(true);

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                googleMap.clear();
                lat = (float)latLng.latitude;
                lon = (float)latLng.longitude;

                getAddress(latLng);
                addMarker(latLng);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent intentResult = new Intent();
                setResult(RESULT_CANCELED, intentResult);
                finish();
                return true;

            case R.id.action_ok:
                if (Logger.DEBUG) {
                    Log.d(TAG, textLocation.getText().toString());
                    Log.d(TAG, String.format("lat=%f", lat));
                    Log.d(TAG, String.format("lon=%f", lon));
                }
                WeatherApp.getWeatherParser().setCity(textLocation.getText().toString(), lat, lon);

                intentResult = new Intent();
                setResult(RESULT_OK, intentResult);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getAddress(final LatLng location){
        final Geocoder geocoder = new Geocoder(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                    textLocation.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Logger.DEBUG) {
                                String message = addresses.get(0).getLocality();
                                if (message != null) {
                                    Log.d(TAG, addresses.get(0).getLocality());
                                } else {
                                    Log.d(TAG, "NULL");
                                }
                            }
                            textLocation.setText(addresses.get(0).getLocality());
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addMarker(LatLng location){
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(getString(R.string.im_here)));
    }
}
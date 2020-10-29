package ru.softmine.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    String currentCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentCity = getResources().getString(R.string.moscow_city);
    }


    public void cityOnClick(View view) {
        Intent intent = new Intent(this, SelectCityActivity.class);
        intent.putExtra("current_city", currentCity);
        startActivity(intent);
    }
}
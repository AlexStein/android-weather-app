package ru.softmine.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import ru.softmine.weatherapp.cities.CityAdapter;

/**
 * Список всех известных нам городов, с возможностью просмотра информации
 */
public class CitiesActivity extends BaseActivity implements CityAdapter.ItemClickListener {

    private static final String TAG = CitiesActivity.class.getName();

    private CityAdapter adapter;
    ArrayList<String> citiesNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        citiesNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cities)));

        RecyclerView recyclerView = findViewById(R.id.city_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CityAdapter(this, citiesNames);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        String cityName = adapter.getItem(position);
        String url = String.format(getString(R.string.wiki_url_format), cityName);

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
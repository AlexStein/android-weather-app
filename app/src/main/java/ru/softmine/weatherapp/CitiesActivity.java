package ru.softmine.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;

import ru.softmine.weatherapp.cities.CityAdapter;
import ru.softmine.weatherapp.cities.CityModel;

/**
 * Список всех известных нам городов, с возможностью просмотра информации
 */
public class CitiesActivity extends BaseActivity implements CityAdapter.ItemClickListener {

    private static final String TAG = CitiesActivity.class.getName();

    private CityAdapter adapter;

    private EditText editText;

    ArrayList<String> citiesNames;

    boolean inputIsCorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city_activity);

        citiesNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cities)));

        RecyclerView recyclerView = findViewById(R.id.city_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CityAdapter(this, citiesNames);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        String currentCityName = CityModel.getInstance().getCityName();
        editText = findViewById(R.id.editTextCityName);
        // Проверяем исходное значение
        inputIsCorrect = validateCity(editText, currentCityName);

        if (inputIsCorrect) {
            editText.setText(currentCityName);
        }

        // Будем докучать пользователю на каждом символе, но начиная с 4го символа
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                inputIsCorrect = (editable.length() > 3) && validateCity(editText, editable.toString());
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        String cityName = adapter.getItem(position);
        editText.setText(cityName);
    }

    /**
     * Переход на википедию
     */
    public void onButtonInfoClick(View view) {
        if (!inputIsCorrect) {
            Snackbar.make(view, R.string.city_activity_wrong_input, BaseTransientBottomBar.LENGTH_LONG).show();
            return;
        }

        String cityName = editText.getText().toString();
        String url = String.format(getString(R.string.wiki_url_format), cityName);

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * Проверка названия города на осмысленность
     *
     * @param view Поле ввода с материальным дизайном
     */
    private boolean validateCity(TextView view, String value) {
        // Проверка на осмысленое имя
        if (CityModel.isValidName(value)) {
            hideError(view);
        } else {
            showError(view, getResources().getString(R.string.city_validation_error));
            return false;
        }

        // Город не в нашем списке
        if (!adapter.hasCity(value)) {
            showError(view, getResources().getString(R.string.city_name_not_supported));
            return false;
        }

        return true;
    }

    // Показать ошибку
    private void showError(TextView view, String message) {
        view.setError(message);
    }

    // Спрятать ошибку
    private void hideError(TextView view) {
        view.setError(null);
    }
}
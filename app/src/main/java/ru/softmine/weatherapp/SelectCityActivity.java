package ru.softmine.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import ru.softmine.weatherapp.cities.CityAdapter;

public class SelectCityActivity extends BaseActivity implements CityAdapter.ItemClickListener {

    private static final String TAG = SelectCityActivity.class.getName();

    private CityAdapter adapter;

    private EditText editText;
    private RadioGroup radioGroupSpeed;
    private RadioGroup radioGroupTemp;

    ArrayList<String> citiesNames;

    boolean inputIsCorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city_activity);

        citiesNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cities)));

        RecyclerView recyclerView = findViewById(R.id.city_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int topItems = Math.min(citiesNames.size(), 5);

        // В списке видим некий топ5.
        adapter = new CityAdapter(this, citiesNames.subList(0, topItems));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        radioGroupSpeed = findViewById(R.id.radioGroupSpeed);
        radioGroupTemp = findViewById(R.id.radioGroupTemp);

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
        CityModel.getInstance().setCityName(cityName);
    }

    public void onButtonApplyClick(View view) {

        if (!inputIsCorrect) {
            Snackbar.make(view, R.string.city_activity_wrong_input, BaseTransientBottomBar.LENGTH_LONG).show();
            return;
        }

        String cityName = editText.getText().toString();
        Intent intentResult = new Intent();
        intentResult.putExtra(BundleKeys.CITY_NAME, cityName);

        if (radioGroupTemp.getCheckedRadioButtonId() == R.id.radioF) {
            intentResult.putExtra(BundleKeys.TEMP_UNITS, getString(R.string.fahrenheit));
        } else {
            intentResult.putExtra(BundleKeys.TEMP_UNITS, getString(R.string.celsius));
        }

        switch (radioGroupSpeed.getCheckedRadioButtonId()) {
            case R.id.radioMPH:
                intentResult.putExtra(BundleKeys.SPEED_UNITS, getString(R.string.mph));
                break;
            case R.id.radioKPH:
                intentResult.putExtra(BundleKeys.SPEED_UNITS, getString(R.string.kph));
                break;
            default:
                intentResult.putExtra(BundleKeys.SPEED_UNITS, getString(R.string.m_s));
        }

        setResult(RESULT_OK, intentResult);

        Snackbar.make(view, R.string.city_activity_apply_message, BaseTransientBottomBar.LENGTH_SHORT).show();

        finish();
    }

    /**
     * Проверка названия города на осмысленность
     *
     * @param view Поле ввода с материальным дизайном
     */
    private boolean validateCity(TextView view, String value) {
        Pattern cityNameRegex = Pattern.compile(getString(R.string.city_regex));

        // Проверка на осмысленое имя
        if (cityNameRegex.matcher(value).matches()) {
            hideError(view);
        } else {
            showError(view, getResources().getString(R.string.city_validation_error));
            return false;
        }

        // Город не в нашем списке
        if (!citiesNames.contains(value)) {
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
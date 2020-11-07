package ru.softmine.weatherapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SelectCityActivity extends AppCompatActivity {

    private static final boolean DEBUG = true;
    private static final String TAG = SelectCityActivity.class.getName();

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city_activity);

        editText = findViewById(R.id.editTextCityName);
        editText.setText(CityPresenter.getInstance().getCityName());
    }

    public void onClick(View view) {

        if (DEBUG) {
            Log.d(TAG, String.format("onClick: view %s", view.getClass().getName()));
            Log.d(TAG, String.format("onClick: id %s", view.getId()));
        }

        switch (view.getId()) {
            case R.id.textViewCity1:
            case R.id.textViewCity2:
            case R.id.textViewCity3:
            case R.id.textViewCity4:
            case R.id.textViewCity5:
                String cityName = ((TextView)view).getText().toString();
                editText.setText(cityName);
                CityPresenter.getInstance().setCityName(cityName);
                return;
            default:
        }
    }
}
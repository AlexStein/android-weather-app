package ru.softmine.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SelectCityActivity extends AppCompatActivity {

    private static final boolean DEBUG = false;
    private static final String TAG = SelectCityActivity.class.getName();

    private EditText editText;
    private RadioGroup radioGroupSpeed;
    private RadioGroup radioGroupTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city_activity);

        radioGroupSpeed = findViewById(R.id.radioGroupSpeed);
        radioGroupTemp = findViewById(R.id.radioGroupTemp);

        editText = findViewById(R.id.editTextCityName);
        editText.setText(CityModel.getInstance().getCityName());
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
                CityModel.getInstance().setCityName(cityName);

                return;
            default:
        }
    }

    public void onButtonApplyClick(View view) {

        String cityName = editText.getText().toString();
        Intent intentResult = new Intent();
        intentResult.putExtra(BundleKeys.CITYNAME, cityName);

        if (radioGroupTemp.getCheckedRadioButtonId() == R.id.radioF) {
            intentResult.putExtra(BundleKeys.TEMPUNITS, getString(R.string.fahrenheit));
        } else {
            intentResult.putExtra(BundleKeys.TEMPUNITS, getString(R.string.celsius));
        }

        switch(radioGroupSpeed.getCheckedRadioButtonId()) {
            case R.id.radioMPH:
                intentResult.putExtra(BundleKeys.SPEEDUNITS, getString(R.string.mph));
                break;
            case R.id.radioKPH:
                intentResult.putExtra(BundleKeys.SPEEDUNITS, getString(R.string.kph));
                break;
            default:
                intentResult.putExtra(BundleKeys.SPEEDUNITS, getString(R.string.m_s));
        }

        setResult(RESULT_OK, intentResult);
        finish();
    }
}
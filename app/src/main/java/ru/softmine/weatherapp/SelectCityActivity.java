package ru.softmine.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SelectCityActivity extends AppCompatActivity {

    private static final boolean DEBUG = true;
    private static final String TAG = SelectCityActivity.class.getName();
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city_activity);

        Intent intent = getIntent();

        editText = findViewById(R.id.editTextCityName);
        editText.setText(intent.getStringExtra("current_city"));
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
                editText.setText(((TextView)view).getText().toString());
                return;
            default:
        }
    }
}
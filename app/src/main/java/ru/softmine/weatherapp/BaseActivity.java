package ru.softmine.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.softmine.weatherapp.constants.PrefKeys;

/**
 * Базовый абстарктный класс для всех активити приложения,
 * для приенения тем.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    protected boolean isDarkTheme() {
        SharedPreferences sharedPref = getSharedPreferences(PrefKeys.NAME_SHARED_PREFERENCE, MODE_PRIVATE);
        return sharedPref.getBoolean(PrefKeys.IS_DARK_THEME, true);
    }
}

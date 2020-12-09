package ru.softmine.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import ru.softmine.weatherapp.constants.BundleKeys;
import ru.softmine.weatherapp.constants.Logger;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = SettingsActivity.class.getName();

    private boolean themeChanged;

    private  SwitchMaterial switchDarkTheme;
    private RadioGroup radioGroupSpeed;
    private RadioGroup radioGroupTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        switchDarkTheme = findViewById(R.id.switch_dark);
        switchDarkTheme.setChecked(isDarkTheme());
        switchDarkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                themeChanged = true;
            }
        });

        radioGroupSpeed = findViewById(R.id.radioGroupSpeed);
        radioGroupTemp = findViewById(R.id.radioGroupTemp);

        themeChanged = false;
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

    /**
     * Нажатие на кнопку сохранения настроек. Настройки сохраняются,
     * в вызывающую активити возвращем ОК и признак того что тема приложения
     * изменилась.
     */
    public void onButtonApplyClick(View view) {
        if (Logger.DEBUG) {
            Log.d(TAG, "onButtonApplyClick");
        }

        if (themeChanged) {
            Snackbar.make(switchDarkTheme.getRootView(), R.string.app_theme_apply_message,
                    BaseTransientBottomBar.LENGTH_SHORT).show();

            setDarkTheme(switchDarkTheme.isChecked());
            recreate();
        }

        // TODO: Сохранение настроек
        int tempUnits = radioGroupTemp.getCheckedRadioButtonId();
        int speedUnits = radioGroupSpeed.getCheckedRadioButtonId();

        Intent intentResult = new Intent();
        intentResult.putExtra(BundleKeys.THEME_CHANGED, themeChanged);

        setResult(RESULT_OK, intentResult);
        finish();
    }

}
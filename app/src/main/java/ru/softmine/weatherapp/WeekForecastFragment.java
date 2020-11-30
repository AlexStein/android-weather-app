package ru.softmine.weatherapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeekForecastFragment extends Fragment {

    private static final String TAG = WeekForecastFragment.class.getName();

    public static WeekForecastFragment createFragment() {
        return new WeekForecastFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_week_forecast_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initList(view);
    }

    /** Создаем список прогноза на неделю, данные из ресурса, без использования
     * адаптеров.
     *
     * @param view LinearLayout фрагмента
     */
    private void initList(View view) {
        LinearLayout layoutView = (LinearLayout) view;
        String[] temps = getResources().getStringArray(R.array.temps);
        String[] forecasts = getResources().getStringArray(R.array.forecasts);

        Calendar calendar = Calendar.getInstance();
        Date dt = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.day_fmt), Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dt = calendar.getTime();
            layoutView.addView(listItem(sdf.format(dt), temps[i], forecasts[i]));

        }
    }

    private View listItem(String weekDay, String temperatures, String forecast) {

        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        itemLayout.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tvParams.weight = 1.0f;

        TextView tvDay = new TextView(getContext());
        tvDay.setText(weekDay);
        tvDay.setTextSize(30);
        tvDay.setLayoutParams(tvParams);

        TextView tvTemp = new TextView(getContext());
        tvTemp.setText(temperatures);
        tvTemp.setTextSize(16);
        tvTemp.setLayoutParams(tvParams);

        TextView tvForecast= new TextView(getContext());
        tvForecast.setText(forecast);
        tvForecast.setTextSize(14);
        tvForecast.setLayoutParams(tvParams);

        itemLayout.addView(tvDay);
        itemLayout.addView(tvTemp);
        itemLayout.addView(tvForecast);

        return itemLayout;
    }
}

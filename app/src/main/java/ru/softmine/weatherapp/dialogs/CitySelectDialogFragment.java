package ru.softmine.weatherapp.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.cities.CityAdapter;
import ru.softmine.weatherapp.constants.Logger;
import ru.softmine.weatherapp.interfaces.OnDialogListener;
import ru.softmine.weatherapp.interfaces.OnFragmentErrorListener;

public class CitySelectDialogFragment extends BottomSheetDialogFragment {

    private static final String TAG = CitySelectDialogFragment.class.getName();

    private final String cityNameRegex = getResources().getString(R.string.cityNameRegEx);

    /* Список известных городов */
    ArrayList<String> citiesNames;

    /* Адаптер для списка городов */
    private CityAdapter adapter;

    /* Обработчик события выбора города */
    private OnDialogListener dialogListener;

    private OnFragmentErrorListener errorListener;

    /* Поле ввода наименования города */
    private EditText editTextCityName;

    /**
     * Создание экземпляра фрагмента коснтруктором по умолчанию.
     *
     * @return CitySelectDialogFragment
     */
    public static CitySelectDialogFragment newInstance() {
        return new CitySelectDialogFragment();
    }

    /**
     * Установить обработчик-наблюдатель выбора города
     *
     * @param listener класс с интерфейсом OnDialogListener
     */
    public void setOnDialogListener(OnDialogListener listener) {
        this.dialogListener = listener;
    }

    public void setOnErrorListener(OnFragmentErrorListener listener) {
        this.errorListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.city_select_fragment_dialog, container, false);

        citiesNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cities)));

        editTextCityName = view.findViewById(R.id.editTextCityName);
        editTextCityName.setSingleLine();
        editTextCityName.setImeOptions(EditorInfo.IME_ACTION_DONE);

        RecyclerView recyclerView = view.findViewById(R.id.city_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        int topItems = Math.min(citiesNames.size(), 3);

        // В списке видим некий топ5.
        adapter = new CityAdapter(getContext(), citiesNames.subList(0, topItems));
        adapter.setClickListener(new CityAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String cityName = adapter.getItem(position);
                editTextCityName.setText(cityName);
            }
        });
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.buttonApply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (dialogListener != null) {

                    //TODO: Проверить что город корректный.
                    String cityName = editTextCityName.getText().toString();
                    boolean inputIsCorrect = validateCity(editTextCityName, cityName);

                    if (inputIsCorrect) {
                        if (Logger.VERBOSE) {
                            Log.v(TAG, "inputIsCorrect!");
                        }
                        dialogListener.onDialogApply();
                    }
                }
            }
        });

        return view;
    }

    /** Получить значение из поля ввода наименовани города
     *
     * @return наименование города
     */
    public String getCityName() {
        return editTextCityName.getText().toString();
    }

    /**
     * Проверка названия города на осмысленность
     *
     * @param view Поле ввода с материальным дизайном
     */
    private boolean validateCity(TextView view, String value) {
        // Проверка на осмысленое имя
        if (isValidName(value)) {
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

    /**
     * Показать ошибку ввода в поле наименования города
     *
     * @param view поле ввода
     * @param message текст сообщения
     */
    private void showError(TextView view, String message) {
        view.setError(message);
        errorListener.onFragmentError(message);
    }

    /** Спрятать ошибку
     *
     * @param view Поле ввода
     */
    private void hideError(TextView view) {
        view.setError(null);
    }

    private boolean isValidName(String cityName) {
        Pattern cityNamePattern = Pattern.compile(cityNameRegex);
        return cityNamePattern.matcher(cityName).matches();
    }
}

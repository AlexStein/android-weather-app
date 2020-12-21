package ru.softmine.weatherapp.history;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.softmine.weatherapp.R;
import ru.softmine.weatherapp.WeatherApp;
import ru.softmine.weatherapp.constants.Database;
import ru.softmine.weatherapp.database.WeatherDao;

public class HistoryContentProvider extends ContentProvider {

    // Типы URI для определения запроса
    private static final int URI_ALL = 1;   //URI для всех записей
    private static final int URI_ID = 2;    //URI для конкретрной записи

    // Часть пути (будем определять ее до таблицы student)
    private static final String HISTORY_PATH = Database.HISTORY_ITEM_TABLE;

    // Адрес URI
    private String authorities;

    // Помогает определить тип URI адреса
    private UriMatcher uriMatcher;

    // Типы данных
    // набор строк
    private String history_items_content_type;
    // одна строка
    private String history_item_content_item_type;

    // адрес URI провайдера
    private Uri content_uri;

    @Override
    public boolean onCreate() {
        // Прочитаем часть пути из ресурсов
        authorities = getContext().getResources().getString(R.string.authorities);

        // Вспомогательный класс, для определения типа запроса
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Интересуют объекты
        uriMatcher.addURI(authorities, HISTORY_PATH, URI_ALL);
        // Интересует только один объект
        uriMatcher.addURI(authorities, HISTORY_PATH + "/#", URI_ID);

        // тип содержимого - все объекты
        history_items_content_type = "vnd.android.cursor.dir/vnd." + authorities + "." + HISTORY_PATH;
        // тип содержимого - один объект
        history_item_content_item_type = "vnd.android.cursor.item/vnd." + authorities + "." + HISTORY_PATH;

        // Строка для доступа к провайдеру
        content_uri = Uri.parse("content://" + authorities + "/" + HISTORY_PATH);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        WeatherDao weatherDao = WeatherApp.getWeatherDao();
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case URI_ALL:
                // Запрос в базу данных для всех элементов
                cursor = weatherDao.getHistoryCursor();
                break;
            case URI_ID:
                // Определяем id из uri адреса.
                // Класс ContentUris помогает это сделать
                long id = ContentUris.parseId(uri);
                // Запрос в базу данных для одного элемента
                cursor = weatherDao.getHistoryCursor(id);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        // Установим нотификацию при изменении данных в content_uri
        cursor.setNotificationUri(getContext().getContentResolver(), content_uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case URI_ALL:
                return history_items_content_type;
            case URI_ID:
                return history_item_content_item_type;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

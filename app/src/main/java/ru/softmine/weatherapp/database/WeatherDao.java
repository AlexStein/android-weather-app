package ru.softmine.weatherapp.database;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.softmine.weatherapp.constants.Database;
import ru.softmine.weatherapp.history.HistoryItem;

@Dao
public interface WeatherDao {

    String historyFields = HistoryItem.getFields();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHistoryItem(HistoryItem historyItem);

    @Query("SELECT * FROM " + Database.HISTORY_ITEM_TABLE)
    List<HistoryItem> getHistoryItems();

    @Query("SELECT * FROM " + Database.HISTORY_ITEM_TABLE +
            " WHERE " + HistoryItem.CITY_NAME + " LIKE :name")
    List<HistoryItem> searchHistoryItems(String name);

    @Query("SELECT COUNT(*) FROM " + Database.HISTORY_ITEM_TABLE)
    int getHistoryItemsCount();

    @Query("SELECT * FROM " + Database.HISTORY_ITEM_TABLE + " ORDER BY " + HistoryItem.ID + " DESC LIMIT 1")
    HistoryItem lastHistoryItem();

    @Update
    void updateHistoryItem(HistoryItem historyItem);

    @Query("SELECT * FROM " + Database.HISTORY_ITEM_TABLE)
    Cursor getHistoryCursor();

    @Query("SELECT * FROM " + Database.HISTORY_ITEM_TABLE + " WHERE id = :id")
    Cursor getHistoryCursor(long id);
}

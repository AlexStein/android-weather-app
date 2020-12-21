package ru.softmine.weatherapp.history;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.softmine.weatherapp.constants.Database;
import ru.softmine.weatherapp.openweathermodel.WeatherParser;

@Entity(tableName = Database.HISTORY_ITEM_TABLE)
public class HistoryItem {

    public final static String ID = "id";
    public final static String DT = "dt";
    public final static String CITY_NAME = "city_name";
    public final static String TEMPERATURE = "temperature";
    public final static String CONDITIONS = "conditions";
    public final static String WIND = "wind";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    public long id;

    @ColumnInfo(name = CITY_NAME)
    private String cityName;

    @ColumnInfo(name = DT)
    private Date dt;

    @ColumnInfo(name = TEMPERATURE)
    private String temperature;

    @ColumnInfo(name = CONDITIONS)
    private String conditions;

    @ColumnInfo(name = WIND)
    private String wind;

    public HistoryItem() {
    }

    @Ignore
    public HistoryItem(WeatherParser weatherParser) {
        this.dt = Calendar.getInstance().getTime();
        this.cityName = weatherParser.getCityName();
        this.temperature =  weatherParser.getTemperatureString();
        this.conditions = weatherParser.getWeatherString();
        this.wind = weatherParser.getWindString();
    }

    public static String getFields() {
        List<String> fields = new ArrayList<String>() {};
        fields.add(ID);
        fields.add(DT);
        fields.add(CITY_NAME);
        fields.add(TEMPERATURE);
        fields.add(CONDITIONS);
        fields.add(WIND);

        return String.join(",", fields);
    }

    public Date getDt() {
        return dt;
    }

    public String getCityName() {
        return cityName;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getConditions() {
        return conditions;
    }

    public String getWind() {
        return wind;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getDateString(SimpleDateFormat sdf) {
        return sdf.format(dt);
    }
}

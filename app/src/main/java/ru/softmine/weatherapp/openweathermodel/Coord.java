package ru.softmine.weatherapp.openweathermodel;

/**
 * Класс описывает географические координаты, получаемы в запросе погоды
 */
public class Coord {
    private float lat;
    private float lon;

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }
}

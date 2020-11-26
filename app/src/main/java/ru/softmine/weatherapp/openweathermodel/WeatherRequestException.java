package ru.softmine.weatherapp.openweathermodel;

public class WeatherRequestException extends Exception {
    public WeatherRequestException(String message) {
        super(message);
    }
}

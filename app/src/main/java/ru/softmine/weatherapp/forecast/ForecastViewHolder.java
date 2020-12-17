package ru.softmine.weatherapp.forecast;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import ru.softmine.weatherapp.R;

public class ForecastViewHolder extends RecyclerView.ViewHolder {

    private final TextView textViewDate;
    private final ImageView imageViewIcon;
    private final TextView textViewTemp;
    private final TextView textViewForecast;

    public ForecastViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewDate = itemView.findViewById(R.id.textViewDate);
        imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
        textViewTemp = itemView.findViewById(R.id.textViewTemp);
        textViewForecast = itemView.findViewById(R.id.textViewForecast);
    }

    public void setData(String date, String icon, String temps, String forecast) {
        Picasso.get().load(icon).placeholder(R.drawable.unknown).into(imageViewIcon);
        getTextViewDate().setText(date);
        getTextViewTemp().setText(temps);
        getTextViewForecast().setText(forecast);
    }

    public TextView getTextViewDate() {
        return textViewDate;
    }

    public ImageView getImageViewIcon() {
        return imageViewIcon;
    }

    public TextView getTextViewTemp() {
        return textViewTemp;
    }

    public TextView getTextViewForecast() {
        return textViewForecast;
    }
}

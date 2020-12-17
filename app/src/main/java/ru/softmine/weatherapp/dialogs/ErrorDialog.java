package ru.softmine.weatherapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ru.softmine.weatherapp.R;

public class ErrorDialog extends DialogFragment {

    AlertDialog.Builder builder;

    private String message;

    public static ErrorDialog newInstance() {
        return new ErrorDialog();
    }

    public void setMessage(String message){
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_dialog_error)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setCancelable(false)
                .setMessage(message);
        return builder.create();
    }
}

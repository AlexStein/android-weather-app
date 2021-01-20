package ru.softmine.weatherapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ru.softmine.weatherapp.R;

public class SignOutDialog extends DialogFragment {

    private final static String TAG = SignOutDialog.class.getSimpleName();

    private AlertDialog.Builder builder;

    private String message;

    public static SignOutDialog newInstance() {
        return new SignOutDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog");

        builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_dialog_sign_out)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    dismiss();

                })
                .setCancelable(false);
        return builder.create();
    }
}

package com.example.micontrol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConfirmationDialog {

    public interface ConfirmationDialogListener {
        void onPositiveButtonClicked();
        void onNegativeButtonClicked();
    }

    public static void show(Context context, String title, String message, String positiveButtonText, String negativeButtonText, ConfirmationDialogListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listener != null) {
                    listener.onPositiveButtonClicked();
                }
            }
        });
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (listener != null) {
                    listener.onNegativeButtonClicked();
                }
            }
        });
        builder.show();
    }
}

package com.example.micontrol;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private static final String PREF_KEY_COLOR = "background_color";
    private static final String PREF_NAME = "app_preferences";

    public static void saveSelectedColor(Context context, String color) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_COLOR, color);
        editor.apply();
    }

    public static String getSelectedColor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_KEY_COLOR, "cafe"); // Color por defecto si no se ha seleccionado ninguno
    }
}

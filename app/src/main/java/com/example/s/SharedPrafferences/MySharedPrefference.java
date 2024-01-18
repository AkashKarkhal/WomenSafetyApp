package com.example.s.SharedPrafferences;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPrefference {
    private static final String PREFERENCES_NAME = "MyPreferences";

    // Write a string value to SharedPreferences
    public static void writeString(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Read a string value from SharedPreferences
    public static String readString(Context context, String key, String defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, defaultValue);
    }

    public static boolean isSharedPreferencesEmpty(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getAll().isEmpty();
    }
}

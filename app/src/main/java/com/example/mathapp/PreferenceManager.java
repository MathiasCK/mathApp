package com.example.mathapp;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private final String LANG_PREF_KEY = "selected_language";
    private final String DIFFICULTY_PREF_KEY = "selected_difficulty";
    
    private SharedPreferences sharedPreferences;
    
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
    }

    public void setSelectedLanguage(String languageCode) {
        sharedPreferences.edit().putString(LANG_PREF_KEY, languageCode).apply();
    }
    
    public void setDifficulty(String difficulty) {
        sharedPreferences.edit().putString(DIFFICULTY_PREF_KEY, difficulty).apply();
    }
    
    public String getDifficulty() {
        return sharedPreferences.getString(DIFFICULTY_PREF_KEY, "15");
    }
    
    
}


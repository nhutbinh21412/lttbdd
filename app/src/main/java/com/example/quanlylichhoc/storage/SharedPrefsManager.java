package com.example.quanlylichhoc.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_ROLE = "role";
    private static final String KEY_MSSV = "mssv";
    
    private static SharedPrefsManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPrefsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUser(int id, String username, String name, String role, String mssv) {
        sharedPreferences.edit()
                .putInt(KEY_USER_ID, id)
                .putString(KEY_USERNAME, username)
                .putString(KEY_FULL_NAME, name)
                .putString(KEY_ROLE, role)
                .putString(KEY_MSSV, mssv)
                .apply();
    }

    public int getUserId() { return sharedPreferences.getInt(KEY_USER_ID, -1); }
    public String getFullName() { return sharedPreferences.getString(KEY_FULL_NAME, "User"); }
    public String getUserRole() { return sharedPreferences.getString(KEY_ROLE, "Student"); }


    public void saveSearchDraft(String query) {
        sharedPreferences.edit().putString("search_draft", query).apply();
    }

    public String getSearchDraft() {
        return sharedPreferences.getString("search_draft", "");
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}

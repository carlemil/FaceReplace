package se.kjellstrand.facereplace.db;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.Gson;

public class BaseStorage {

    protected static void putContentValuesIfNotNull(String tag, ContentValues contentValues, String key, String value) {
        if (key != null) {
            if (value != null) {            
                contentValues.put(key, value);
            }
            else {
                contentValues.put(key, 0);
                Log.d(tag, "putContentValuesIfNotNull, value: null");
            }
        }
        else {
            Log.d(tag, "putContentValuesIfNotNull, key: null");
        }
    }

    protected static void putContentValuesIfNotNull(String tag, ContentValues contentValues, String key, Long value) {
        if (key != null) {
            if (value != null) {
                contentValues.put(key, value);
            }
            else {
                contentValues.put(key, 0);
                Log.d(tag, "putContentValuesIfNotNull, value: null");
            }
        }
        else {
            Log.d(tag, "putContentValuesIfNotNull, key: null");
        }
    }

    protected static void putContentValuesIfNotNull(String tag, ContentValues contentValues, String key, Integer value) {
        if (key != null) {
            if (value != null) {
                contentValues.put(key, value);
            }
            else {
                contentValues.put(key, 0);
                Log.d(tag, "putContentValuesIfNotNull, value: null");
            }
        }
        else {
            Log.d(tag, "putContentValuesIfNotNull, key: null");
        }
    }

    protected static void putContentValuesIfNotNull(String tag, ContentValues contentValues, String key, byte[] value) {
        if (key != null) {
            if (value != null) {
                contentValues.put(key, value);
            }
            else {
                contentValues.put(key, 0);
                Log.d(tag, "putContentValuesIfNotNull, value: null");
            }
        }
        else {
            Log.d(tag, "putContentValuesIfNotNull, key: null");
        }
    }

}

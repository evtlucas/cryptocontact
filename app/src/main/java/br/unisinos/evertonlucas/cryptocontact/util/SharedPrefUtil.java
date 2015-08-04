package br.unisinos.evertonlucas.cryptocontact.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by everton on 02/08/15.
 */
public class SharedPrefUtil {

    public static String readFrom(Activity activity, String name, String key) {
        SharedPreferences pref = activity.getSharedPreferences(name, Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public static void writeTo(Activity activity, String name, String key, String value) {
        SharedPreferences pref = activity.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }
}

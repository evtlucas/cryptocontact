package br.unisinos.evertonlucas.cryptocontact.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * Created by everton on 02/08/15.
 */
public class SharedPrefUtil {

    public static final String KEYCHAIN_PREF = "keychain";
    public static final String KEYCHAIN_PREF_ALIAS = "alias";
    public static final String KEYCHAIN_PREF_KEY = "key";

    public static String readFrom(Context context, String name, String key) {
        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public static byte[] readByteFrom(Context context, String name, String key) {
        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        String value = pref.getString(key, "");
        if (value.trim().length() == 0)
            return new byte[0];
        byte[] result = Base64.decode(value, Base64.DEFAULT);
        return result;
    }

    public static void writeTo(Context context, String name, String key, String value) {
        SharedPreferences.Editor editor = getEditor(context, name);
        editor.putString(key, value);
        editor.commit();
    }

    private static SharedPreferences.Editor getEditor(Context context, String name) {
        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return pref.edit();
    }

    public static void writeByteTo(Context context, String name, String key, byte[] value) {
        String stringValue = Base64.encodeToString(value, Base64.DEFAULT);
        SharedPreferences.Editor editor = getEditor(context, name);
        editor.putString(key, stringValue);
        editor.commit();
    }
}

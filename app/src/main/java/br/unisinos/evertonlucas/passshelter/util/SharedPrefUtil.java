/*
Copyright 2015 Everton Luiz de Resende Lucas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package br.unisinos.evertonlucas.passshelter.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * Class used to read for and write to Shared Preferences
 * Created by everton on 02/08/15.
 */
public class SharedPrefUtil {

    public static final String KEYCHAIN_PREF = "keychain";
    public static final String KEYCHAIN_PREF_ALIAS = "alias";
    public static final String KEYCHAIN_PREF_KEY = "key";
    public static final String KEYCHAIN_PREF_STATE = "state";
    public static final String KEYCHAIN_PREF_USER = "user";
    public static final String KEYCHAIN_PREF_PWD = "pwd";
    public static final String RESOURCE = "resource";
    public static final String RESOURCE_NAME = "name";

    public static String readFrom(Context context, String name, String key) {
        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public static byte[] readByteFrom(Context context, String name, String key) {
        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        String value = pref.getString(key, "");
        if (value.trim().length() == 0)
            return new byte[0];
        return Base64.decode(value, Base64.DEFAULT);
    }

    public static void writeTo(Context context, String name, String key, String value) {
        SharedPreferences.Editor editor = getEditor(context, name);
        editor.putString(key, value);
        editor.commit();
    }

    public static void delete(Context context, String name, String key) {
        SharedPreferences.Editor editor = getEditor(context, name);
        editor.remove(key);
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

    public static int readIntFrom(Context context, String name, String key) {
        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return pref.getInt(key, -1);
    }

    public static void writeIntTo(Context context, String name, String key, int value) {
        SharedPreferences.Editor editor = getEditor(context, name);
        editor.putInt(key, value);
        editor.commit();
    }
}

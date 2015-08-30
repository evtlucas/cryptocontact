package br.unisinos.evertonlucas.passshelter.util;

import android.content.Context;

import com.parse.Parse;

import br.unisinos.evertonlucas.passshelter.app.AppConfig;

/**
 * Created by everton on 30/08/15.
 */
public class ParseUtil {

    public static void registerParse(Context context) {
        Parse.enableLocalDatastore(context);
        Parse.initialize(context, AppConfig.APPLICATION_ID, AppConfig.CLIENT_KEY);
    }
}

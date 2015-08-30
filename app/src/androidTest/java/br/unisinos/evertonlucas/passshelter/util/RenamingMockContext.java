package br.unisinos.evertonlucas.passshelter.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.RenamingDelegatingContext;

/**
 * From book "Learning Android Application Testing", item "Mocking applications and preferences"
 * Created by everton on 08/08/15.
 */
public class RenamingMockContext extends RenamingDelegatingContext {

    public static final String PREFIX = "test.";

    public RenamingMockContext(Context context) {
        super(context, PREFIX);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return super.getSharedPreferences(PREFIX + name, mode);
    }
}

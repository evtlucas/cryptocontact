package br.unisinos.evertonlucas.passshelter.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Class responsible for Toast Messages
 * Created by everton on 08/10/15.
 */
public class ToastUtil {

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}

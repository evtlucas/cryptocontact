package br.unisinos.evertonlucas.passshelter.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Class created for show ProgressDialog
 * Created by everton on 08/09/15.
 */
public class ProgressDialogUtil {
    public static ProgressDialog createProgressDialog(Context context, String msg) {
        return ProgressDialog.show(context, "Pass Shelter", msg, true);
    }
}

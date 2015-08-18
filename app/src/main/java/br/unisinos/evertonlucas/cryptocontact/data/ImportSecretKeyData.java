package br.unisinos.evertonlucas.cryptocontact.data;

import android.app.Activity;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import br.unisinos.evertonlucas.cryptocontact.R;
import br.unisinos.evertonlucas.cryptocontact.encryption.Algorithms;
import br.unisinos.evertonlucas.cryptocontact.util.ConfirmationDialog;
import br.unisinos.evertonlucas.cryptocontact.util.SharedPrefUtil;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

/**
 * Class responsible for read data.crypto file.
 * Created by everton on 17/08/15.
 */
public class ImportSecretKeyData implements ConfirmationDialog.ConfirmationDialogListener {
    private UpdateSecretKey updateSecretKey;
    private Activity activity;

    public ImportSecretKeyData(UpdateSecretKey updateSecretKey, Activity activity) {
        this.updateSecretKey = updateSecretKey;
        this.activity = activity;
    }

    public void importData() {
        ConfirmationDialog dialog = ConfirmationDialog.newInstance(this);
        dialog.setTitle(R.string.import_key)
                .setMessage(R.string.import_message)
                .show(this.activity.getFragmentManager(), "");
    }

    @Override
    public void onConfirmationPositive() {
        File importFile = DataUtil.getFile();
        if (!importFile.exists()) {
            makeText(this.activity, this.activity.getResources().getString(R.string.file_not_found), LENGTH_LONG).show();
            return;
        }
        try {
            FileInputStream fis = new FileInputStream(importFile);
            byte[] data = new byte[(int)importFile.length()];
            fis.read(data);
            updateSecretKey.update(data);
        } catch (Exception e) {
            throwException(e, activity);
        }
    }

    public static void throwException(Exception e, Activity activity) {
        makeText(activity, activity.getResources().getString(R.string.import_key_exception) + e.getMessage(),
                LENGTH_LONG).show();
        Log.e(activity.getResources().getString(R.string.app_name),
                activity.getResources().getString(R.string.import_key_exception) +
                        e.getMessage() + "\n\r" + e.getStackTrace().toString());
    }
}

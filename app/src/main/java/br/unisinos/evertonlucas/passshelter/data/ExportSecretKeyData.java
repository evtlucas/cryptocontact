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

package br.unisinos.evertonlucas.passshelter.data;

import android.app.Activity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.util.ConfirmationDialog;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

/**
 * Class responsible for export cryptographic key
 * Created by everton on 09/08/15.
 */
public class ExportSecretKeyData implements ConfirmationDialog.ConfirmationDialogListener {

    private Activity activity;
    private byte[] key = null;

    public ExportSecretKeyData(Activity context) {
        this.activity = context;
    }

    @Override
    public void onConfirmationPositive() {
        try {
            File exportFile = DataUtil.getFile();
            if (exportFile.exists())
                exportFile.delete();
            FileOutputStream file = new FileOutputStream(exportFile);
            file.write(this.key);
            file.flush();
            file.close();
            makeText(this.activity, this.activity.getResources().getString(R.string.export_key_success), LENGTH_LONG).show();
        } catch (Exception e) {
            throwException(e, activity);
        }
    }

    public static void throwException(Exception e, Activity activity) {
        makeText(activity, activity.getResources().getString(R.string.export_key_exception) + e.getMessage(),
                LENGTH_LONG).show();
        Log.e(activity.getResources().getString(R.string.app_name), activity.getResources().getString(R.string.export_key_exception), e);
    }

    public void export(byte[] key) {
        this.key = key;
        ConfirmationDialog confirmationDialog = ConfirmationDialog.newInstance(this);
        confirmationDialog.setTitle(R.string.backup)
                .setMessage(R.string.backup_message)
                .show(this.activity.getFragmentManager(), "");
    }
}

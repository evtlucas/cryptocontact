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
import java.io.FileInputStream;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.util.ConfirmationDialog;

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
            if(fis.read(data) > 0)
                updateSecretKey.update(data);
        } catch (Exception e) {
            throwException(e, activity);
        }
    }

    public static void throwException(Exception e, Activity activity) {
        makeText(activity, activity.getResources().getString(R.string.import_key_exception) + e.getMessage(),
                LENGTH_LONG).show();
        Log.e(activity.getResources().getString(R.string.app_name), activity.getResources().getString(R.string.import_key_exception), e);
    }
}

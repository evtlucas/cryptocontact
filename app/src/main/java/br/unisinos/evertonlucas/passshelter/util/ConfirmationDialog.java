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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import br.unisinos.evertonlucas.passshelter.R;

/**
 * Class responsible for show a confirmation dialog
 * Created by everton on 08/08/15.
 */
public class ConfirmationDialog extends DialogFragment {

    private ConfirmationDialogListener listener;
    private int title;
    private int message;

    public static ConfirmationDialog newInstance(ConfirmationDialogListener  listener) {
        ConfirmationDialog d = new ConfirmationDialog();
        d.listener = listener;
        return d;
    }

    public ConfirmationDialog setTitle(int title) {
        this.title = title;
        return this;
    }

    public ConfirmationDialog setMessage(int message) {
        this.message = message;
        return this;
    }

    public interface ConfirmationDialogListener {
        void onConfirmationPositive();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle(this.title)
                .setMessage(this.message)
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onConfirmationPositive();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return alertBuilder.create();
    }
}

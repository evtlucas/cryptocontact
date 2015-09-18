package br.unisinos.evertonlucas.passshelter.util;

import android.app.Activity;
import android.os.Environment;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutionException;

import br.unisinos.evertonlucas.passshelter.async.CertificateReadAsyncTask;
import br.unisinos.evertonlucas.passshelter.async.UpdateCertificate;

/**
 * Class responsible for communicate with KeyChain
 * Created by everton on 20/07/15.
 */
public class KeyStoreUtil {

    private final Activity activity;

    public KeyStoreUtil(Activity activity) {
        this.activity = activity;
    }

    public void readCertificate(UpdateCertificate update, String alias)
            throws ExecutionException, InterruptedException {
        CertificateReadAsyncTask asyncTask = new CertificateReadAsyncTask(activity, update, alias);
        asyncTask.execute();
    }

    public void choosePrivateKeyAlias(KeyChainAliasCallback callback) {
        KeyChain.choosePrivateKeyAlias(this.activity, callback, new String[]{"RSA"}, null, null, -1, null);
    }
}

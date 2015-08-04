package br.unisinos.evertonlucas.cryptocontact.util;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.unisinos.evertonlucas.cryptocontact.Main;
import br.unisinos.evertonlucas.cryptocontact.async.CertificateReadAsyncTask;
import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificate;
import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificateStatus;

/**
 * Class responsible for communicate with KeyChain
 * Created by everton on 20/07/15.
 */
public class KeyStoreUtil {

    private final Activity activity;

    public KeyStoreUtil(Activity activity) {
        this.activity = activity;
    }

    private byte[] readFile(String filename) throws Exception {
        File f = new File(Environment.getExternalStorageDirectory(), filename);
        byte[] result = new byte[(int) f.length()];
        FileInputStream in = new FileInputStream(f);
        in.read(result);
        in.close();
        return result;
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

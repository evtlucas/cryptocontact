package br.unisinos.evertonlucas.cryptocontact.util;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.security.KeyChain;
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

/**
 * Class responsible for communicate with KeyChain
 * Created by everton on 20/07/15.
 */
public class KeyStoreUtil {

    private final Activity activity;
    private KeyStore keyStore;
    private X509Certificate cert;

    public KeyStoreUtil(Activity activity) {
        this.activity = activity;
        try {
            // TODO review the need about KeyStore
            keyStore = KeyStore.getInstance("AndroidCAStore");
            keyStore.load(null, null);
        } catch (Exception e) {
            Log.e("CryptoContact", "Erro ao obter KeyStore: " + e.getStackTrace());
        }
    }

    public void installCertificate(String certFile) {
        try {
            Intent intent = KeyChain.createInstallIntent();
            byte[] p12 = readFile(certFile);
            intent.putExtra(KeyChain.EXTRA_PKCS12, p12);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e("CryptoContact", "Erro ao ler PKCS12: " + e.getStackTrace());
        }
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

    public List<String> getKeys() throws KeyStoreException {
        List<String> keyAliases = new ArrayList<String>();
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = (String) aliases.nextElement();
            if ("Everton".equalsIgnoreCase(alias))
                Log.d("CryptoContact", "Encontrou");
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
        }
        return keyAliases;
    }
}

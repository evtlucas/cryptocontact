package br.unisinos.evertonlucas.cryptocontact.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.security.KeyChain;
import android.security.KeyChainException;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by everton on 20/07/15.
 */
public class KeyStoreService {

    private final Activity activity;
    private KeyStore keyStore;

    public KeyStoreService(Activity activity) {
        this.activity = activity;
        try {
            keyStore = KeyStore.getInstance("AndroidCAStore");
            keyStore.load(null, null);
        } catch (Exception e) {
            Log.e("CryptoContact", "Erro ao obter KeyStore: " + e.getStackTrace());
        }
    }

    public void instalarCertificado() {
        try {
            Intent intent = KeyChain.createInstallIntent();
            byte[] p12 = readFile("Download/Everton.p12");
            intent.putExtra(KeyChain.EXTRA_PKCS12, p12);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e("CryptoContact", "Erro ao ler PKCS12: " + e.getStackTrace());
        }
    }

    public void instalarCertificadoCA() {
        try {
            Intent intent = KeyChain.createInstallIntent();
            byte[] p12 = readFile("Download/getacert.cer");
            intent.putExtra(KeyChain.EXTRA_CERTIFICATE, p12);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e("CryptoContact", "Erro ao ler Certificado CA: " + e.getStackTrace());
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

    public X509Certificate getCertificado() {
        X509Certificate chain = null;

        new AsyncTask<Object, Object, X509Certificate>() {
            @Override
            protected X509Certificate doInBackground(Object... params) {
                X509Certificate[] canal = null;
                try {
                    Context context = (Context) params[0];
                    String alias = (String) params[1];
                    canal = KeyChain.getCertificateChain(context, alias);
                } catch (Exception e) {
                    Log.e("CryptoContact", "Erro ao ler Certificado: " + e.getStackTrace());
                }
                return canal[0];
            }
        }.execute(activity, "Everton", chain);

        return chain;
    }

    public List<String> getChaves() throws KeyStoreException {
        List<String> keyAliases = new ArrayList<String>();
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = (String) aliases.nextElement();
            if ("Everton".equalsIgnoreCase(alias))
                Log.d("CryptoContact", "Encontrou");
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
            //if (cert.getIssuerDN().getName().contains("user"))
                keyAliases.add(cert.getIssuerDN().getName());
        }
        return keyAliases;
    }
}

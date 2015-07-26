package br.unisinos.evertonlucas.cryptocontact.async;

import android.content.Context;
import android.os.AsyncTask;
import android.security.KeyChain;
import android.util.Log;

import java.security.cert.X509Certificate;

/**
 * Class responsible for read a certificate chain based on an alias.
 * These class should be implemented as AsyncTask because getCertificateChain will block UI Thread
 * Created by everton on 24/07/15.
 */
public class CertificateReadAsyncTask extends AsyncTask<Void, Void, X509Certificate> {

    private final UpdateCertificate updateCertificate;
    private final String alias;
    private final Context ctx;

    public CertificateReadAsyncTask(Context ctx, UpdateCertificate updateCertificate, String alias) {
        this.ctx = ctx;
        this.updateCertificate = updateCertificate;
        this.alias = alias;
    }

    @Override
    protected X509Certificate doInBackground(Void... params) {
        X509Certificate[] chain = null;
        try {
            chain = KeyChain.getCertificateChain(ctx, alias);
        } catch (Exception e) {
            Log.e("CryptoContact", "Error reading certificate: " + e.getStackTrace());
        }
        return chain[0];
    }

    @Override
    protected void onPostExecute(X509Certificate x509Certificate) {
        updateCertificate.updateCertificateInfo(x509Certificate);
    }
}

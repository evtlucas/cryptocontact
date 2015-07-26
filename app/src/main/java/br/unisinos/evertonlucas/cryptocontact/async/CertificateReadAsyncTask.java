package br.unisinos.evertonlucas.cryptocontact.async;

import android.content.Context;
import android.os.AsyncTask;
import android.security.KeyChain;
import android.util.Log;

import java.security.cert.X509Certificate;

/**
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
        X509Certificate[] canal = null;
        try {
            canal = KeyChain.getCertificateChain(ctx, alias);
        } catch (Exception e) {
            Log.e("CryptoContact", "Erro ao ler Certificado: " + e.getStackTrace());
        }
        return canal[0];
    }

    @Override
    protected void onPostExecute(X509Certificate x509Certificate) {
        updateCertificate.updateCertificateInfo(x509Certificate);
        super.onPostExecute(x509Certificate);
    }
}

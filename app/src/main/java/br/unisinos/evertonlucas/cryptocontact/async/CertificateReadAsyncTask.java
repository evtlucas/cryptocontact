package br.unisinos.evertonlucas.cryptocontact.async;

import android.content.Context;
import android.os.AsyncTask;
import android.security.KeyChain;
import android.util.Log;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import br.unisinos.evertonlucas.cryptocontact.model.CertificateBag;

/**
 * Class responsible for read a certificate chain based on an alias.
 * These class should be implemented as AsyncTask because getCertificateChain will block UI Thread
 * Created by everton on 24/07/15.
 */
public class CertificateReadAsyncTask extends AsyncTask<Void, Void, CertificateBag> {

    private final UpdateCertificate updateCertificate;
    private final String alias;
    private final Context ctx;

    public CertificateReadAsyncTask(Context ctx, UpdateCertificate updateCertificate, String alias) {
        this.ctx = ctx;
        this.updateCertificate = updateCertificate;
        this.alias = alias;
    }

    @Override
    protected CertificateBag doInBackground(Void... params) {
        X509Certificate[] chain = null;
        PrivateKey privateKey = null;
        CertificateBag bag;
        try {
            chain = KeyChain.getCertificateChain(ctx, alias);
            privateKey = KeyChain.getPrivateKey(ctx, alias);
            if ((chain == null) || (privateKey == null))
                return new CertificateBag(null, null);
            bag = new CertificateBag(chain[0], privateKey);
        } catch (Exception e) {
            Log.e("CryptoContact", "Error reading certificate bag: " + e.getStackTrace());
            return new CertificateBag(null, null);
        }
        return bag;
    }

    @Override
    protected void onPostExecute(CertificateBag certificateBag) {
        updateCertificate.updateCertificateInfo(certificateBag);
    }
}

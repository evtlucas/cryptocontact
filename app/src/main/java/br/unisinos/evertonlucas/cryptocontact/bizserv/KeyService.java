package br.unisinos.evertonlucas.cryptocontact.bizserv;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;

import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificate;
import br.unisinos.evertonlucas.cryptocontact.util.KeyStoreUtil;

/**
 * Class responsible for manage keys
 * Created by everton on 26/07/15.
 */
public class KeyService implements UpdateCertificate {

    private static final String KEYCHAIN_PREF = "keychain";
    private static final String KEYCHAIN_PREF_ALIAS = "alias";
    private final Activity activity;
    private final KeyStoreUtil util;
    private X509Certificate cert = null;

    public KeyService(Activity activity) {
        this.activity = activity;
        util = new KeyStoreUtil(activity);
        try {
            util.readCertificate(this, "Everton");
        } catch (Exception e) {
            cert = null;
            Log.e("CryptoContact", "Error during KeyService creation: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }
    }

    private String getAlias() {
        SharedPreferences pref = activity.getSharedPreferences(KEYCHAIN_PREF, Context.MODE_PRIVATE);
        return pref.getString(KEYCHAIN_PREF_ALIAS, "");
    }

    public boolean isCertificateAvailable() {
        return cert != null;
    }

    @Override
    public void updateCertificateInfo(X509Certificate cert) {
        this.cert = cert;
    }
}

package br.unisinos.evertonlucas.cryptocontact.bizserv;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.util.Log;

import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;

import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificate;
import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificateStatus;
import br.unisinos.evertonlucas.cryptocontact.util.KeyStoreUtil;

/**
 * Class responsible for manage keys
 * Created by everton on 26/07/15.
 */
public class KeyService implements UpdateCertificate, KeyChainAliasCallback {

    private static final String KEYCHAIN_PREF = "keychain";
    private static final String KEYCHAIN_PREF_ALIAS = "alias";
    private final UpdateCertificateStatus certStatus;
    private Activity activity;
    private final KeyStoreUtil util;
    private X509Certificate cert = null;

    public KeyService(UpdateCertificateStatus certStatus, Activity activity) {
        this.certStatus = certStatus;
        this.activity = activity;
        util = new KeyStoreUtil(activity);
        try {
            util.readCertificate(this, getAlias());
        } catch (Exception e) {
            cert = null;
            Log.e("CryptoContact", "Error during KeyService creation: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }
    }

    private String getAlias() {
        // TODO Create class for reading from and write to SharedPreferences.
        SharedPreferences pref = activity.getSharedPreferences(KEYCHAIN_PREF, Context.MODE_PRIVATE);
        return pref.getString(KEYCHAIN_PREF_ALIAS, "");
    }

    public boolean isCertificateAvailable() {
        return cert != null;
    }

    public void choosePrivateKeyAlias() {
        KeyChain.choosePrivateKeyAlias(this.activity, this, new String[]{"RSA"}, null, null, -1, null);
    }

    @Override
    public void updateCertificateInfo(X509Certificate cert) {
        this.cert = cert;
        certStatus.update(isCertificateAvailable());
    }

    @Override
    public void alias(String alias) {
        SharedPreferences pref = activity.getSharedPreferences(KEYCHAIN_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEYCHAIN_PREF_ALIAS, alias);
        editor.commit();
        try {
            util.readCertificate(this, alias);
        } catch (Exception e) {
            cert = null;
            Log.e("CryptoContact", "Error during KeyService definition: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }
    }
}

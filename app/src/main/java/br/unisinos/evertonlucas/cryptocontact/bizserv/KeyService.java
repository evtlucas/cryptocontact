package br.unisinos.evertonlucas.cryptocontact.bizserv;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.util.Log;

import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificate;
import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificateStatus;
import br.unisinos.evertonlucas.cryptocontact.model.CertificateBag;
import br.unisinos.evertonlucas.cryptocontact.util.KeyStoreUtil;
import br.unisinos.evertonlucas.cryptocontact.util.SharedPrefUtil;

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
    private CertificateBag cert = null;
    private boolean generatePrivateKey = false;

    public KeyService(UpdateCertificateStatus certStatus, Activity activity) {
        this.certStatus = certStatus;
        this.activity = activity;
        util = new KeyStoreUtil(activity);
        try {
            readCertificate(getAlias());
        } catch (Exception e) {
            cert = null;
            Log.e("CryptoContact", "Error during KeyService creation: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }
    }

    private void readCertificate(String alias) throws ExecutionException, InterruptedException {
        util.readCertificate(this, alias);
    }

    private String getAlias() {
        return SharedPrefUtil.readFrom(this.activity, KEYCHAIN_PREF, KEYCHAIN_PREF_ALIAS);
    }

    public boolean isCertificateAvailable() {
        return cert != null;
    }

    public void installCertificate() {
        this.util.choosePrivateKeyAlias(this);
    }

    @Override
    public void updateCertificateInfo(CertificateBag cert) {
        this.cert = cert;
        certStatus.update(isCertificateAvailable());
        // TODO generate and save private key
        if (generatePrivateKey) {
            generatePrivateKey = false;
        }
    }

    @Override
    public void alias(String alias) {
        // This method is called as a callback for KeyChain.choosePrivateKeyAlias
        SharedPrefUtil.writeTo(this.activity, KEYCHAIN_PREF, KEYCHAIN_PREF_ALIAS, alias);
        try {
            readCertificate(alias);
            generatePrivateKey = true;
        } catch (Exception e) {
            cert = null;
            Log.e("CryptoContact", "Error during KeyService definition: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }
    }

    /*private SecretKeySpec readSecretKeySharedPref() {
        String key = SharedPrefUtil.readFrom(this.activity, KEYCHAIN_PREF, "secret");
        if (key.trim().length() == 0)
            return null;
        byte[] bytes = key.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
        return secretKeySpec;
    }*/

    public void reReadCertificate() {
        try {
            readCertificate(getAlias());
        } catch (Exception e) {
            cert = null;
            Log.e("CryptoContact", "Error during KeyService new read: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }
    }
}

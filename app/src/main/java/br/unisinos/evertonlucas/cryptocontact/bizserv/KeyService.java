package br.unisinos.evertonlucas.cryptocontact.bizserv;

import android.app.Activity;
import android.security.KeyChainAliasCallback;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import br.unisinos.evertonlucas.cryptocontact.R;
import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificate;
import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificateStatus;
import br.unisinos.evertonlucas.cryptocontact.data.ExportCryptoKeyData;
import br.unisinos.evertonlucas.cryptocontact.encryption.AssymetricEncryption;
import br.unisinos.evertonlucas.cryptocontact.model.CertificateBag;
import br.unisinos.evertonlucas.cryptocontact.util.KeyGenerationUtil;
import br.unisinos.evertonlucas.cryptocontact.util.KeyStoreUtil;
import br.unisinos.evertonlucas.cryptocontact.util.SharedPrefUtil;

import static android.widget.Toast.*;

/**
 * Class responsible for manage keys
 * Created by everton on 26/07/15.
 */
public class KeyService implements UpdateCertificate, KeyChainAliasCallback {

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
            Log.e(this.activity.getResources().getString(R.string.app_name),
                    "Error during KeyService creation: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }
    }

    private void readCertificate(String alias) throws ExecutionException, InterruptedException {
        util.readCertificate(this, alias);
    }

    private String getAlias() {
        return SharedPrefUtil.readFrom(this.activity, SharedPrefUtil.KEYCHAIN_PREF, SharedPrefUtil.KEYCHAIN_PREF_ALIAS);
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
        if (generatePrivateKey) {
            generatePrivateKey = false;
            try {
                AssymetricEncryption enc = new AssymetricEncryption(this.cert);
                byte[] key = KeyGenerationUtil.generate().getEncoded();
                byte[] encodedKey = enc.encrypt(key);
                SharedPrefUtil.writeByteTo(this.activity, SharedPrefUtil.KEYCHAIN_PREF,
                        SharedPrefUtil.KEYCHAIN_PREF_KEY, encodedKey);
            } catch (Exception e) {
                makeText(this.activity, "Erro ao atualizar certificado", LENGTH_LONG).show();
                Log.e(this.activity.getResources().getString(R.string.app_name),
                        "Error during KeyService definition: " +
                                e.getMessage() + "\n\r" + e.getStackTrace().toString());
            }
        }
    }

    @Override
    public void alias(String alias) {
        // This method is called as a callback for KeyChain.choosePrivateKeyAlias
        SharedPrefUtil.writeTo(this.activity, SharedPrefUtil.KEYCHAIN_PREF, SharedPrefUtil.KEYCHAIN_PREF_ALIAS, alias);
        try {
            readCertificate(alias);
            generatePrivateKey = true;
        } catch (Exception e) {
            cert = null;
            Log.e(this.activity.getResources().getString(R.string.app_name),
                    "Error during KeyService definition: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }
    }

    public void reReadCertificate() {
        try {
            readCertificate(getAlias());
        } catch (Exception e) {
            cert = null;
            Log.e(this.activity.getResources().getString(R.string.app_name),
                    "Error during KeyService new read: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }
    }

    public void saveCryptographicKey() {
        ExportCryptoKeyData data = new ExportCryptoKeyData(this.activity);
        data.export();
    }
}

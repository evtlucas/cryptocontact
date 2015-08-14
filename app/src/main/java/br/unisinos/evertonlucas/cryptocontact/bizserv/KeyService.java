package br.unisinos.evertonlucas.cryptocontact.bizserv;

import android.app.Activity;
import android.security.KeyChainAliasCallback;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import javax.crypto.SecretKey;

import br.unisinos.evertonlucas.cryptocontact.R;
import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificate;
import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificateStatus;
import br.unisinos.evertonlucas.cryptocontact.data.ExportCryptoKeyData;
import br.unisinos.evertonlucas.cryptocontact.data.PersistCriptoData;
import br.unisinos.evertonlucas.cryptocontact.model.CertificateBag;
import br.unisinos.evertonlucas.cryptocontact.util.KeyStoreUtil;
import br.unisinos.evertonlucas.cryptocontact.util.SharedPrefUtil;

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
    private SecretKey key = null;
    private PersistCriptoData persist;

    public KeyService(UpdateCertificateStatus certStatus, Activity activity) {
        this.certStatus = certStatus;
        this.activity = activity;
        util = new KeyStoreUtil(activity);
        this.persist = new PersistCriptoData();
        /*try {
            // TODO revisar necessidade dessa leitura, uma vez que ela ocorre durante o onResume
            readCertificate(getAlias());
        } catch (Exception e) {
            cert = null;
            Log.e(this.activity.getResources().getString(R.string.app_name),
                    "Error during KeyService creation: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }*/
    }

    public boolean isCertificateAvailable() {
        return cert.isValid();
    }

    public void installCertificate() {
        this.util.choosePrivateKeyAlias(this);
    }

    @Override
    public void updateCertificateInfo(CertificateBag cert) {
        this.cert = cert;
        certStatus.update(isCertificateAvailable());
        if (!this.cert.isValid()) {
            cleanPrivateKey();
            return;
        }
        if (generatePrivateKey) {
            generatePrivateKey = false;
            createSavePrivateKey();
        } else {
            readSavedPrivateKey();
        }
    }

    private void cleanPrivateKey() {
        this.persist.cleanPrivateKey(this.activity);
    }

    private void readSavedPrivateKey() {
        this.key = persist.readPrivateKey(this.cert, this.activity);
    }

    private void createSavePrivateKey() {
        this.key = persist.savePrivateKey(this.cert, this.activity);
    }

    private String getAlias() {
        return persist.readAlias(this.activity, SharedPrefUtil.KEYCHAIN_PREF, SharedPrefUtil.KEYCHAIN_PREF_ALIAS);
    }

    private void readCertificate(String alias) throws ExecutionException, InterruptedException {
        util.readCertificate(this, alias);
    }

    @Override
    public void alias(String alias) {
        // This method is called as a callback for KeyChain.choosePrivateKeyAlias
        persist.writeAlias(this.activity, SharedPrefUtil.KEYCHAIN_PREF, SharedPrefUtil.KEYCHAIN_PREF_ALIAS, alias);
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

    public void exportCryptographicKey() {
        ExportCryptoKeyData data = new ExportCryptoKeyData(this.activity);
        data.export();
    }

    public void importCryptographicKey() {

    }
}

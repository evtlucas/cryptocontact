package br.unisinos.evertonlucas.passshelter.bizserv;

import android.app.Activity;
import android.security.KeyChainAliasCallback;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.UpdateCertificate;
import br.unisinos.evertonlucas.passshelter.async.UpdateCertificateStatus;
import br.unisinos.evertonlucas.passshelter.data.ExportSecretKeyData;
import br.unisinos.evertonlucas.passshelter.data.ImportSecretKeyData;
import br.unisinos.evertonlucas.passshelter.data.PersistSecretData;
import br.unisinos.evertonlucas.passshelter.data.UpdateSecretKey;
import br.unisinos.evertonlucas.passshelter.encryption.Algorithms;
import br.unisinos.evertonlucas.passshelter.encryption.AssymetricEncryption;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.util.KeyStoreUtil;
import br.unisinos.evertonlucas.passshelter.util.SharedPrefUtil;

/**
 * Class responsible for manage keys
 * Created by everton on 26/07/15.
 */
public class KeyService implements UpdateCertificate, KeyChainAliasCallback, UpdateSecretKey {

    private final UpdateCertificateStatus certStatus;
    private Activity activity;
    private boolean generatePrivateKey = false;
    private final KeyStoreUtil util;
    private CertificateBag cert = null;
    private SecretKey key = null;
    private PersistSecretData persist;

    public KeyService(UpdateCertificateStatus certStatus, Activity activity) {
        this.certStatus = certStatus;
        this.activity = activity;
        util = new KeyStoreUtil(activity);
        this.persist = new PersistSecretData();
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
            cert = new CertificateBag(null, null);
            Log.e(this.activity.getResources().getString(R.string.app_name),
                    "Error during KeyService definition: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }
    }

    public void reReadCertificate() {
        try {
            readCertificate(getAlias());
        } catch (Exception e) {
            cert = new CertificateBag(null, null);
            Log.e(this.activity.getResources().getString(R.string.app_name),
                    "Error during KeyService new read: " +
                    e.getMessage() + "\n\r" + e.getStackTrace().toString());
        }
    }

    public void exportCryptographicKey() throws NoSuchProviderException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        ExportSecretKeyData data = new ExportSecretKeyData(this.activity);
        byte[] encoded = new AssymetricEncryption(this.cert).encrypt(this.key.getEncoded());
        data.export(encoded);
    }

    public void importCryptographicKey() {
        ImportSecretKeyData data = new ImportSecretKeyData(this, this.activity);
        data.importData();
    }

    @Override
    public void update(byte[] key) {
        try {
            byte[] decoded = new AssymetricEncryption(this.cert).decrypt(key);
            SecretKey skey = new SecretKeySpec(decoded, Algorithms.SYMMETRIC);
            this.key = skey;
        } catch (Exception e) {
            ImportSecretKeyData.throwException(e, this.activity);
        }
    }
}

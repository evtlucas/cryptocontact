/*
Copyright 2015 Everton Luiz de Resende Lucas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package br.unisinos.evertonlucas.passshelter.service;

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
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.data.ExportSecretKeyData;
import br.unisinos.evertonlucas.passshelter.data.ImportSecretKeyData;
import br.unisinos.evertonlucas.passshelter.data.PersistSecretData;
import br.unisinos.evertonlucas.passshelter.data.UpdateSecretKey;
import br.unisinos.evertonlucas.passshelter.encryption.Algorithms;
import br.unisinos.evertonlucas.passshelter.encryption.AssymetricCryptography;
import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.util.ConfirmationDialog;
import br.unisinos.evertonlucas.passshelter.util.KeyStoreUtil;
import br.unisinos.evertonlucas.passshelter.util.SharedPrefUtil;

/**
 * Class responsible for manage keys
 * Created by everton on 26/07/15.
 */
public class KeyService implements UpdateCertificate, KeyChainAliasCallback, UpdateSecretKey {

    private UpdateStatus certStatus;
    private Activity activity;
    private boolean generatePrivateKey = false;
    private final KeyStoreUtil util;
    private CertificateBag cert = null;
    private SecretKey key = null;
    private PersistSecretData persist;

    public KeyService(UpdateStatus certStatus, Activity activity) {
        this.certStatus = certStatus;
        this.activity = activity;
        this.util = new KeyStoreUtil(activity);
        this.persist = new PersistSecretData();
    }

    public void setUpdateCertificateStatus(UpdateStatus certStatus) {
        this.certStatus = certStatus;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public SymmetricEncryption getSymmetricEncryption() {
        readSavedPrivateKey();
        if (this.key == null)
            return null;
        return new SymmetricEncryption(key);
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
            Log.e(this.activity.getResources().getString(R.string.app_name), "Error during KeyService definition", e);
        }
    }

    public void reReadCertificate() {
        try {
            readCertificate(getAlias());
        } catch (Exception e) {
            cert = new CertificateBag(null, null);
            Log.e(this.activity.getResources().getString(R.string.app_name), "Error during KeyService new read", e);
        }
    }

    public void exportCryptographicKey() throws NoSuchProviderException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        ExportSecretKeyData data = new ExportSecretKeyData(this.activity);
        byte[] encoded = new AssymetricCryptography(this.cert).encrypt(this.key.getEncoded());
        data.export(encoded);
    }

    public void importCryptographicKey() {
        ImportSecretKeyData data = new ImportSecretKeyData(this, this.activity);
        ConfirmationDialog dialog = ConfirmationDialog.newInstance(data);
        dialog.setTitle(R.string.import_key)
                .setMessage(R.string.import_message)
                .show(this.activity.getFragmentManager(), "");
    }

    @Override
    public void update(byte[] key) {
        try {
            byte[] decoded = new AssymetricCryptography(this.cert).decrypt(key);
            this.key = new SecretKeySpec(decoded, Algorithms.SYMMETRIC);
        } catch (Exception e) {
            ImportSecretKeyData.throwException(e, this.activity);
        }
    }

    public void loadCertificate() throws ExecutionException, InterruptedException {
        readCertificate(getAlias());
        //certStatus.update(isCertificateAvailable());
    }

    public CertificateBag getCertificateBag() {
        return this.cert;
    }
}

package br.unisinos.evertonlucas.passshelter.data;

import android.content.Context;
import android.util.Log;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.encryption.AssymetricEncryption;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.util.KeyGenerationUtil;
import br.unisinos.evertonlucas.passshelter.util.SharedPrefUtil;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

/**
 * Class responsible for persist private key and its alias
 * Created by everton on 11/08/15.
 */
public class PersistSecretData {

    public SecretKey readPrivateKey(CertificateBag cert, Context context) {
        SecretKey pKey = null;
        try {
            AssymetricEncryption enc = new AssymetricEncryption(cert);
            byte[] encodedKey = SharedPrefUtil.readByteFrom(context, SharedPrefUtil.KEYCHAIN_PREF,
                    SharedPrefUtil.KEYCHAIN_PREF_KEY);
            if (encodedKey.length > 0) {
                byte[] key = enc.decrypt(encodedKey);
                pKey = new SecretKeySpec(key, 0, key.length, "AES");
            }
        } catch (Exception e) {
            makeText(context, "Erro ao ler dados criptográficos", LENGTH_LONG).show();
            Log.e(context.getResources().getString(R.string.app_name), "Error during private key reading: ", e);
        }
        return pKey;
    }

    public SecretKey savePrivateKey(CertificateBag cert, Context context) {
        SecretKey key = null;
        try {
            AssymetricEncryption enc = new AssymetricEncryption(cert);
            key = KeyGenerationUtil.generate();
            byte[] pkey = key.getEncoded();
            byte[] encodedKey = enc.encrypt(pkey);
            SharedPrefUtil.writeByteTo(context, SharedPrefUtil.KEYCHAIN_PREF,
                    SharedPrefUtil.KEYCHAIN_PREF_KEY, encodedKey);
        } catch (Exception e) {
            makeText(context, "Erro ao atualizar certificado", LENGTH_LONG).show();
            Log.e(context.getResources().getString(R.string.app_name), "Error during KeyService definition: ", e);
        }
        return key;
    }

    public String readAlias(Context context, String name, String key) {
        return SharedPrefUtil.readFrom(context, name, key);
    }

    public void writeAlias(Context context, String name, String key, String value) {
        SharedPrefUtil.writeTo(context, name, key, value);
    }

    public void cleanPrivateKey(Context context) {
        SharedPrefUtil.writeTo(context, SharedPrefUtil.KEYCHAIN_PREF, SharedPrefUtil.KEYCHAIN_PREF_KEY, "");
    }
}

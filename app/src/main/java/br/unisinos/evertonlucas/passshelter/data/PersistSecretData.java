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

package br.unisinos.evertonlucas.passshelter.data;

import android.content.Context;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import br.unisinos.evertonlucas.passshelter.encryption.AssymetricCryptography;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.util.KeyGenerationUtil;
import br.unisinos.evertonlucas.passshelter.util.SharedPrefUtil;
import br.unisinos.evertonlucas.passshelter.util.ShowLogExceptionUtil;

/**
 * Class responsible for persist private key and its alias
 * Created by everton on 11/08/15.
 */
public class PersistSecretData {

    public SecretKey readPrivateKey(CertificateBag cert, Context context) {
        SecretKey pKey = null;
        try {
            AssymetricCryptography enc = new AssymetricCryptography(cert);
            byte[] encodedKey = SharedPrefUtil.readByteFrom(context, SharedPrefUtil.KEYCHAIN_PREF,
                    SharedPrefUtil.KEYCHAIN_PREF_KEY);
            if (encodedKey.length > 0) {
                byte[] key = enc.decrypt(encodedKey);
                pKey = new SecretKeySpec(key, 0, key.length, "AES");
            }
        } catch (Exception e) {
            ShowLogExceptionUtil.logException(context, "Erro ao ler dados criptográficos", e);
        }
        return pKey;
    }

    public SecretKey savePrivateKey(CertificateBag cert, Context context) {
        SecretKey key = null;
        try {
            AssymetricCryptography enc = new AssymetricCryptography(cert);
            key = KeyGenerationUtil.generate();
            byte[] pkey = key.getEncoded();
            byte[] encodedKey = enc.encrypt(pkey);
            SharedPrefUtil.writeByteTo(context, SharedPrefUtil.KEYCHAIN_PREF,
                    SharedPrefUtil.KEYCHAIN_PREF_KEY, encodedKey);
        } catch (Exception e) {
            ShowLogExceptionUtil.logException(context, "Erro ao ler atualizar certificado", e);
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

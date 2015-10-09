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

package br.unisinos.evertonlucas.passshelter.encryption;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.passshelter.model.CertificateBag;

/**
 * Class responsible for encrypt and decrypt methods which implement Assymetric Encryption
 * Created by everton on 07/08/15.
 */
public class AssymetricCryptography {

    private Cipher encCipher;
    private Cipher decCipher;

    public AssymetricCryptography(CertificateBag bag) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        initEncCipher(bag);
        initDecCipher(bag);
    }

    private void initDecCipher(CertificateBag bag) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
        this.decCipher = Cipher.getInstance(Algorithms.ASSYMETRIC, Algorithms.PROVIDER);
        this.decCipher.init(Cipher.DECRYPT_MODE, bag.getCert().getPublicKey());
    }

    private void initEncCipher(CertificateBag bag) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
        this.encCipher = Cipher.getInstance(Algorithms.ASSYMETRIC, Algorithms.PROVIDER);
        this.encCipher.init(Cipher.ENCRYPT_MODE, bag.getPrivateKey());
    }

    public byte[] encrypt(byte[] bytes) throws BadPaddingException, IllegalBlockSizeException {
        return new AssymetricEncryption(this.encCipher).encrypt(bytes);
    }

    public byte[] decrypt(byte[] encrypted) throws BadPaddingException, IllegalBlockSizeException {
        return new AssymetricDecryption(this.decCipher).decrypt(encrypted);
    }
}

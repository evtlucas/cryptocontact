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

package br.unisinos.evertonlucas.passshelter;

import android.test.AndroidTestCase;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.passshelter.encryption.AssymetricCryptography;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.util.TestKeyGenerationUtil;


/**
 * Class responsible for test assymetric cryptography routines
 * Created by everton on 07/08/15.
 */

public class AssymetricCryptographyTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", "/data/data/" + BuildConfig.APPLICATION_ID + ".test/cache");
    }

    public void testEncryptionDecryption() throws CertificateException, NoSuchAlgorithmException,
            InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {
        String phrase = "CryptoContact";
        CertificateBag bag = TestKeyGenerationUtil.generate(getContext());
        AssymetricCryptography encryption = new AssymetricCryptography(bag);
        byte[] encrypted = encryption.encrypt(phrase.getBytes());
        byte[] decrypted = encryption.decrypt(encrypted);
        String finalPhrase = new String(decrypted);
        assertTrue(finalPhrase.equals(phrase));
    }
}

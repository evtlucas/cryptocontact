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

import junit.framework.TestCase;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.util.KeyGenerationUtil;

/**
 * Class designed fot test Symmetric Encryption
 * Created by everton on 04/08/15.
 */
public class SymmetricEncryptionTest extends TestCase {

    public void testEncryption() throws NoSuchAlgorithmException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        SecretKey key = KeyGenerationUtil.generate();
        byte[] clearText = "CryptoContact".getBytes();
        SymmetricEncryption encryption = new SymmetricEncryption(key);
        byte[] cipherText = encryption.encrypt(clearText);
        assertFalse(Arrays.equals(clearText, cipherText));
        byte[] clearText2 = encryption.decrypt(cipherText);
        assertTrue(Arrays.equals(clearText, clearText2));
    }
}

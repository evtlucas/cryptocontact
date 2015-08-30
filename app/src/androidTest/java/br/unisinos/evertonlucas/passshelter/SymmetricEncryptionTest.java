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

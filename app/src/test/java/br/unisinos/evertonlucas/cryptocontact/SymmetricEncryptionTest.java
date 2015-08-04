package br.unisinos.evertonlucas.cryptocontact;

import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static org.junit.Assert.*;

/**
 * Created by everton on 04/08/15.
 */
public class SymmetricEncryptionTest {

    @Test
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

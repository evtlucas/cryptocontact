package br.unisinos.evertonlucas.passshelter;

import junit.framework.TestCase;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.SecretKey;

import br.unisinos.evertonlucas.passshelter.util.KeyGenerationUtil;


/**
 * Created by everton on 03/08/15.
 */
public class KeyGenerationTest extends TestCase {

    public void testKeyGeneration() throws NoSuchAlgorithmException {
        SecretKey key = KeyGenerationUtil.generate();
        byte[] key1 = key.getEncoded();
        key = KeyGenerationUtil.generate();
        byte[] key2 = key.getEncoded();
        assertFalse(Arrays.equals(key1, key2));
    }

    public void testGenerateEqualSecretKeys() throws NoSuchAlgorithmException {
        SecretKey key = KeyGenerationUtil.generate();
        SecretKey key2 = KeyGenerationUtil.getSecretKey(key.getEncoded());
        final byte[] keyTest = key.getEncoded();
        assertTrue(Arrays.equals(key.getEncoded(), key2.getEncoded()));
    }

}
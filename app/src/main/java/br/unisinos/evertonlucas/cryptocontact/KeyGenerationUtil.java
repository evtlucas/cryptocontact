package br.unisinos.evertonlucas.cryptocontact;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class responsible for generate random symmetric keys
 * Created by everton on 03/08/15.
 */
public class KeyGenerationUtil {
    public static SecretKey generate() throws NoSuchAlgorithmException {
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(256);
        SecretKey key = gen.generateKey();
        return key;
    }

    public static SecretKey getSecretKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }
}

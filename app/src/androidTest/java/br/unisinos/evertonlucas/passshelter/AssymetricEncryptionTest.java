package br.unisinos.evertonlucas.passshelter;

import android.test.AndroidTestCase;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.passshelter.encryption.AssymetricEncryption;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.util.TestKeyGenerationUtil;


/**
 * Created by everton on 07/08/15.
 */

public class AssymetricEncryptionTest extends AndroidTestCase {

    public void testEncryptionDecryption() throws CertificateException, NoSuchAlgorithmException,
            InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {
        String phrase = "CryptoContact";
        CertificateBag bag = TestKeyGenerationUtil.generate(getContext());
        AssymetricEncryption encryption = new AssymetricEncryption(bag);
        byte[] encrypted = encryption.encrypt(phrase.getBytes());
        byte[] decrypted = encryption.decrypt(encrypted);
        String finalPhrase = new String(decrypted);
        assertTrue(finalPhrase.equals(phrase));
    }
}

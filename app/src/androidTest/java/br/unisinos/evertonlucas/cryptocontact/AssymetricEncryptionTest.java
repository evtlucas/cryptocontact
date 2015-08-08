package br.unisinos.evertonlucas.cryptocontact;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.cryptocontact.encryption.AssymetricEncryption;
import br.unisinos.evertonlucas.cryptocontact.model.CertificateBag;
import br.unisinos.evertonlucas.cryptocontact.util.TestKeyGenerationUtil;


/**
 * Created by everton on 07/08/15.
 */

public class AssymetricEncryptionTest extends AndroidTestCase {

    public void testEncryptionDecryption() throws CertificateException, NoSuchAlgorithmException,
            InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        String phrase = "CryptoContact";
        CertificateBag bag = TestKeyGenerationUtil.generate(getContext());
        AssymetricEncryption encryption = new AssymetricEncryption(bag);
        byte[] encrypted = encryption.encrypt(phrase.getBytes());
        byte[] decrypted = encryption.decrypt(encrypted);
        String finalPhrase = new String(decrypted);
        assertTrue(finalPhrase.equals(phrase));
    }
}

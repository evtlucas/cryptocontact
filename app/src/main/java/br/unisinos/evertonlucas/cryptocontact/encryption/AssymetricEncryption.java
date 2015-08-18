package br.unisinos.evertonlucas.cryptocontact.encryption;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.cryptocontact.model.CertificateBag;

/**
 * Class responsible for encrypt and decrypt methods which implement Assymetric Encryption
 * Created by everton on 07/08/15.
 */
public class AssymetricEncryption {
    private Cipher encCipher;
    private Cipher decCipher;

    public AssymetricEncryption(CertificateBag bag) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        initEncCipher(bag);
        initDecCipher(bag);
    }

    private void initDecCipher(CertificateBag bag) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
        this.decCipher = Cipher.getInstance(Algorithms.ASSYMETRIC, "AndroidOpenSSL");
        this.decCipher.init(Cipher.DECRYPT_MODE, bag.getCert().getPublicKey());
    }

    private void initEncCipher(CertificateBag bag) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {
        this.encCipher = Cipher.getInstance(Algorithms.ASSYMETRIC, "AndroidOpenSSL");
        this.encCipher.init(Cipher.ENCRYPT_MODE, bag.getPrivateKey());
    }

    public byte[] encrypt(byte[] bytes) throws BadPaddingException, IllegalBlockSizeException {
        return this.encCipher.doFinal(bytes);
    }

    public byte[] decrypt(byte[] encrypted) throws BadPaddingException, IllegalBlockSizeException {
        return this.decCipher.doFinal(encrypted);
    }
}

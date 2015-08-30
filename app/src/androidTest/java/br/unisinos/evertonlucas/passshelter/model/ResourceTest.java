package br.unisinos.evertonlucas.passshelter.model;

import android.util.Base64;

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
 * Created by everton on 30/08/15.
 */
public class ResourceTest extends TestCase {

    private String facebook = "Facebook";
    private String user = "User";
    private String password = "123456";
    private SymmetricEncryption encryption;

    public void setUp() throws NoSuchAlgorithmException {
        final SecretKey key = KeyGenerationUtil.generate();
        this.encryption = new SymmetricEncryption(key);
    }

    public void testOpenAttributes() {
        Resource resource = getResource(facebook, user, password);
        assertEquals(facebook, resource.getName());
        assertEquals(user, resource.getUser());
        assertEquals(password, resource.getPassword());
    }

    private Resource getResource(String facebook, String user, String password) {
        Resource resource = new Resource(encryption);
        resource.setName(facebook);
        resource.setUser(user);
        resource.setPassword(password);
        return resource;
    }

    public void testCryptoUser() throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Resource resource = getResource(facebook, this.user, password);
        byte[] cryptoUser = resource.getCryptoUser();
        byte[] decryptedUser = this.encryption.decrypt(cryptoUser);
        assertTrue(Arrays.equals(this.user.getBytes(), decryptedUser));
    }

    public void testCryptoPassword() throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Resource resource = getResource(facebook, this.user, password);
        byte[] cryptoPassword = resource.getCryptoPassword();
        byte[] decryptedPassword = this.encryption.decrypt(cryptoPassword);
        assertTrue(Arrays.equals(this.password.getBytes(), decryptedPassword));
    }
}
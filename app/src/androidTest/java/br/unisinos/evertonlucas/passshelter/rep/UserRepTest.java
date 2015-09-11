package br.unisinos.evertonlucas.passshelter.rep;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import junit.framework.TestCase;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.util.KeyGenerationUtil;

/**
 * Class designed for test User Repository
 * Created by everton on 06/09/15.
 */
public class UserRepTest extends AndroidTestCase {

    private static final String TEST_PREFIX = "test_";
    private SymmetricEncryption encryption;
    private UserRep userRep;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final SecretKey key = KeyGenerationUtil.generate();
        this.encryption = new SymmetricEncryption(key);

        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), TEST_PREFIX);
        userRep = new UserRep(context, encryption);
    }

    public void testUser() throws InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        String user = "userTest";
        userRep.saveUser(user);
        assertEquals(user, userRep.getUser());
    }

    public void testPassword() throws NoSuchAlgorithmException {
        String pwd = "abcxyz123";
        userRep.savePassword(pwd);
        assertTrue(userRep.validatePassword(pwd));
    }
}
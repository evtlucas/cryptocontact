package br.unisinos.evertonlucas.passshelter;

import android.test.AndroidTestCase;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import br.unisinos.evertonlucas.passshelter.util.KeyGenerationUtil;
import br.unisinos.evertonlucas.passshelter.util.RenamingMockContext;
import br.unisinos.evertonlucas.passshelter.util.SharedPrefUtil;

/**
 * Tests about SharedPrefUtil class
 * Created by everton on 08/08/15.
 */
public class SharedPrefDataUtilTest extends AndroidTestCase {

    private static final String KEYCHAIN_PREF = "keychain";
    private static final String KEYCHAIN_PREF_ALIAS = "alias";

    public void testReadWriteByteValues() throws NoSuchAlgorithmException {
        RenamingMockContext mockContext = getRenamingMockContext();
        byte[] value = KeyGenerationUtil.generate().getEncoded();
        SharedPrefUtil.writeByteTo(mockContext, KEYCHAIN_PREF, KEYCHAIN_PREF_ALIAS, value);
        byte[] returnedValue = SharedPrefUtil.readByteFrom(mockContext, KEYCHAIN_PREF, KEYCHAIN_PREF_ALIAS);
        assertTrue(Arrays.equals(value, returnedValue));
    }

    private RenamingMockContext getRenamingMockContext() {
        RenamingMockContext mockContext = new RenamingMockContext(getContext());
        setContext(mockContext);
        return mockContext;
    }

    public void testReadWriteStringValues() {
        RenamingMockContext mockContext = getRenamingMockContext();
        String value = "CryptoContact";
        SharedPrefUtil.writeTo(mockContext, KEYCHAIN_PREF, KEYCHAIN_PREF_ALIAS, value);
        String returnedValue = SharedPrefUtil.readFrom(mockContext, KEYCHAIN_PREF, KEYCHAIN_PREF_ALIAS);
        assertEquals(value, returnedValue);
    }
}

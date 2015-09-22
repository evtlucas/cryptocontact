/*
Copyright 2015 Everton Luiz de Resende Lucas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package br.unisinos.evertonlucas.passshelter.rep;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

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
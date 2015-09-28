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

import android.database.sqlite.SQLiteConstraintException;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import br.unisinos.evertonlucas.passshelter.model.User;
import br.unisinos.evertonlucas.passshelter.util.UserFactory;

/**
 * Class responsible for test UserRep
 * Created by everton on 27/09/15.
 */
public class UserRepTest extends AndroidTestCase {

    private static final String TEST_PREFIX = "test_";
    private User user;
    private UserRep userRep;

    public void setUp() throws Exception {
        super.setUp();

        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), TEST_PREFIX);
        userRep = new UserRep(context);
        user = UserFactory.generateUser("abc@xyz.com");
        initializeUser(user);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        userRep = null;
        user = null;
    }

    private void initializeUser(User user) {
        this.userRep.insert(user);
    }

    public void testInsertedUser() throws InvalidKeySpecException, NoSuchAlgorithmException {
        User userQuery = this.userRep.getUserByEmail(user.getEmail());
        assertEquals(user.getEmail(), userQuery.getEmail());
        assertTrue(Arrays.equals(user.getPublicKey().getEncoded(), userQuery.getPublicKey().getEncoded()));
    }

    public void testDeleteUser() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String email = "xyz@xyz.com";
        User user = UserFactory.generateUser(email);
        user.setEmail(email);
        this.userRep.insert(user);
        this.userRep.deleteUser(user);
        assertNull(this.userRep.getUserByEmail(email));
    }

    public void testSaveExistentUser() throws InvalidKeySpecException, NoSuchAlgorithmException {
        try {
            User userQuery = this.userRep.getUserByEmail(user.getEmail());
            userRep.save(userQuery);
            assertTrue(true);
        } catch (SQLiteConstraintException e) {
            fail("Could not reach here");
        }
    }
}
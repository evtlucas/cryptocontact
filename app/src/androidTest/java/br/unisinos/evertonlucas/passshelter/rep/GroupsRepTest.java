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
import android.support.annotation.NonNull;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import br.unisinos.evertonlucas.passshelter.model.Group;
import br.unisinos.evertonlucas.passshelter.model.User;
import br.unisinos.evertonlucas.passshelter.util.UserFactory;

/**
 * Class designed for test groups persistence
 * Created by everton on 25/09/15.
 */
public class GroupsRepTest extends AndroidTestCase {

    private static final String TEST_PREFIX = "test_";
    private GroupsRep groupsRep;
    private String name = "Group";
    private String admin = "someone@domain.com";

    public void setUp() throws Exception {
        super.setUp();

        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), TEST_PREFIX);
        groupsRep = new GroupsRep(context);
        initializeGroup();
    }

    private void initializeGroup() throws InvalidKeySpecException, NoSuchAlgorithmException {
        insertGroup(name, admin);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        groupsRep = null;
    }

    public void testInsertGroup() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String name = "Group2";
        String admin = "someone2@domain.com";
        Group group = insertGroup(name, admin);
        Group groupQuery = groupsRep.getGroupByName(name);
        testGroup(group, groupQuery);
    }

    private void testGroup(Group group, Group groupQuery) {
        assertEquals(group.getId(), groupQuery.getId());
        assertEquals(group.getName(), groupQuery.getName());
        assertEquals(group.getAdmin(), groupQuery.getAdmin());
    }

    public void testAvoidGroupDuplication() throws InvalidKeySpecException, NoSuchAlgorithmException {
        try {
            initializeGroup();
            fail("Could not reach here");
        } catch (SQLiteConstraintException e) {
            assertTrue(true);
        }
    }

    public void testUpdateGroup() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String name = "Group2";
        String admin = "someone2@domain.com";
        Group group = groupsRep.getGroupByName(this.name);
        group.setAdmin(admin);
        group.setName(name);
        groupsRep.update(group);
        Group groupQuery = groupsRep.getGroupByName(name);
        testGroup(group, groupQuery);
    }

    public void testAvoidDuplicateUpdate() throws InvalidKeySpecException, NoSuchAlgorithmException {
        try {
            String name = "Group3";
            String admin = "someone3@domain.com";
            insertGroup(name, admin);
            Group group = groupsRep.getGroupByName(name);
            group.setName(this.name);
            groupsRep.update(group);
            fail("Could not reach here");
        } catch (SQLiteConstraintException e) {
            assertTrue(true);
        }
    }

    public void testDeleteGroup() throws InvalidKeySpecException, NoSuchAlgorithmException {
        Group group = groupsRep.getGroupByName(this.name);
        groupsRep.delete(group);
        assertNull(groupsRep.getGroupByName(this.name));
        initializeGroup();
    }

    public void testDeleteNotFoundGroup() throws InvalidKeySpecException, NoSuchAlgorithmException {
        Group group = new Group();
        group.setId(999999L);
        group.setName("Abc");
        groupsRep.delete(group);
        assertNull(groupsRep.getGroupByName("Abc"));
    }

    @NonNull
    private Group insertGroup(String name, String admin) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Group group = instantiateGroup(name, admin);
        groupsRep.insert(group);
        return group;
    }

    @NonNull
    private Group instantiateGroup(String name, String admin) {
        Group group = new Group();
        group.setName(name);
        group.setAdmin(admin);
        return group;
    }

    public void testSaveUsersGroup() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String groupUsers = "GroupUsers";
        Group group = instantiateGroup(groupUsers, "abc@xyz.com");
        group.addUser(UserFactory.generateUser("def@xyz.com"));
        group.addUser(UserFactory.generateUser("ghi@xyz.com"));
        group.addUser(UserFactory.generateUser("jkl@xyz.com"));
        groupsRep.insert(group);
        Group groupQuery = groupsRep.getGroupByName(groupUsers);
        assertEquals(3, groupQuery.getUsers().size());
        int i = 0;
        for (User user : groupQuery.getUsers())
            if (user.getEmail().contains("def") ||
                    user.getEmail().contains("ghi") ||
                    user.getEmail().contains("jkl"))
                i++;
        assertEquals(3, i);
    }

    public void testUpdateExistentGroup() throws InvalidKeySpecException, NoSuchAlgorithmException {
        Group group = groupsRep.getGroupByName(this.name);
        group.addUser(UserFactory.generateUser("def@xyz.com"));
        groupsRep.update(group);
        Group groupQuery = groupsRep.getGroupByName(this.name);
        User user = groupQuery.getUsers().get(0);
        assertEquals("def@xyz.com", user.getEmail());
    }
}
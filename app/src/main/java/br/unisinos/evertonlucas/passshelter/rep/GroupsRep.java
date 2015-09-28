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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import br.unisinos.evertonlucas.passshelter.data.DbHelper;
import br.unisinos.evertonlucas.passshelter.model.Group;
import br.unisinos.evertonlucas.passshelter.model.User;

/**
 * Class responsible for Groups Persistence
 * Created by everton on 25/09/15.
 */
public class GroupsRep {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private UserRep userRep;

    public GroupsRep(Context context) {
        dbHelper = new DbHelper(context);
        userRep = new UserRep(context);
    }

    public void insert(Group group) throws InvalidKeySpecException, NoSuchAlgorithmException {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = transformGroupCv(group);

        long id = db.insertOrThrow(DbHelper.GROUPS, null, cv);
        if (id > -1) {
            group.setId(id);
            saveListUsers(group);
            close();
        } else {
            close();
            throw new RuntimeException("Db Insertion Error");
        }
    }

    private void saveListUsers(Group group) throws InvalidKeySpecException, NoSuchAlgorithmException {
        for(User user : group.getUsers()) {
            userRep.save(user);
            ContentValues cv = new ContentValues();
            cv.put("idgroup", group.getId());
            cv.put("iduser", user.getId());
            db.insert(DbHelper.USERS_GROUPS, null, cv);
        }
    }

    @NonNull
    private ContentValues transformGroupCv(Group group) {
        ContentValues cv = new ContentValues();
        cv.put("name", group.getName());
        cv.put("admin", group.getAdmin());
        return cv;
    }

    private void close() {
        db.close();
        dbHelper.close();
    }

    public Group getGroupByName(String name) throws InvalidKeySpecException, NoSuchAlgorithmException {
        Group group = null;
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.GROUPS, DbHelper.GROUPS_COLUMNS,
                "name = ?", new String[]{name}, null, null, "name");
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            group = new Group();
            group.setId(cursor.getLong(0));
            group.setName(cursor.getString(1));
            group.setAdmin(cursor.getString(2));
            List<User> users = getListUsers(group);
            group.addAllUsers(users);
        }
        cursor.close();
        return group;
    }

    private List<User> getListUsers(Group group) throws InvalidKeySpecException, NoSuchAlgorithmException {
        List<User> lstUsers = new ArrayList<>();
        Cursor cursor = db.query(DbHelper.USERS_GROUPS, DbHelper.USERS_GROUPS_COLUMNS,
                "idgroup = ?", new String[]{group.getId().toString()}, null, null, "idgroup");
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {
            User user = userRep.getUserById(cursor.getInt(1));
            lstUsers.add(user);
            cursor.moveToNext();
        }
        return lstUsers;
    }

    public List<User> getUsersGroup(Group group) throws InvalidKeySpecException, NoSuchAlgorithmException {
        db = dbHelper.getReadableDatabase();
        List<User> lstUsers = getListUsers(group);
        close();
        return lstUsers;
    }

    public List<String> getGroupsName() {
        List<String> lstNames = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.GROUPS, new String[]{"name"}, null, null, null, null, "name");
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {
            lstNames.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return lstNames;
    }

    public void update(Group group) throws InvalidKeySpecException, NoSuchAlgorithmException {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = transformGroupCv(group);
        long result = db.update(DbHelper.GROUPS, cv, "_id = ?", new String[]{group.getId().toString()});
        if (result > 0) {
            deleteListUsers(group);
            saveListUsers(group);
        }
        close();
        if (result < 0)
            throw new RuntimeException("Db Update Error");
    }

    private void deleteListUsers(Group group) {
        db.delete(DbHelper.USERS_GROUPS, "idgroup = ?", new String[]{group.getId().toString()});
    }

    public void delete(Group group) {
        db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.GROUPS, "_id = ?", new String[] { group.getId().toString() });
        db.delete(DbHelper.USERS_GROUPS, "idgroup = ?", new String[] { group.getId().toString() });
        db.close();
    }

    public void save(Group group) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (group.getId() != null)
            update(group);
        else
            insert(group);
    }
}

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
import android.support.annotation.Nullable;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import br.unisinos.evertonlucas.passshelter.data.DbHelper;
import br.unisinos.evertonlucas.passshelter.encryption.KeyFactory;
import br.unisinos.evertonlucas.passshelter.model.User;

/**
 * Class responsible for persist Users
 * Created by everton on 27/09/15.
 */
public class UserRep {

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public UserRep(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void insert(User user) {
        db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("email", user.getEmail());
        cv.put("public_key", user.getPublicKey().getEncoded());

        long id = db.insertOrThrow(DbHelper.USERS, null, cv);
        if (id > -1) {
            user.setId(id);
            close();
        } else {
            close();
            throw new RuntimeException("Db Insertion Error");
        }
    }

    private void close() {
        db.close();
        dbHelper.close();
    }

    public User getUserByEmail(String email) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return searchUser("email = ?", new String[] { email });
    }

    public User getUserById(int id) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        return searchUser("_id = ?", new String[] { Integer.toString(id) });
    }

    @Nullable
    private User searchUser(String selection, String[] selectionArgs) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        User user = null;
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.USERS, DbHelper.USERS_COLUMNS, selection, selectionArgs, null, null, "email");
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user = new User();
            user.setId(cursor.getLong(0));
            user.setEmail(cursor.getString(1));
            user.setRemoteId(cursor.getString(2));
            user.setPublicKey(KeyFactory.generatePublicKey(cursor.getBlob(3)));
        }
        return user;
    }

    public void deleteUser(User user) {
        db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.USERS, "_id = ?", new String[]{user.getId().toString()});
        db.close();
    }

    public void save(User user) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (user.getId() == null)
            insert(user);
    }

    public void deleteAll() {
        db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.USERS, null, null);
        db.close();
    }
}

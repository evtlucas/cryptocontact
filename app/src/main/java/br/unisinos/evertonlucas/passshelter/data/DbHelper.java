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

package br.unisinos.evertonlucas.passshelter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class responsible for manage database
 * Created by everton on 31/08/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE = "db";
    private static final int VERSION = 1;
    public static final String RESOURCE = "resource";
    public static final String GROUPS = "groups";
    public static final String USERS = "users";
    public static final String USERS_GROUPS = "users_groups";
    public static final String[] RESOURCE_COLUMNS = new String[] { "_id", "name", "user", "password"};
    public static final String[] GROUPS_COLUMNS = new String[] { "_id", "name", "admin"};
    public static final String[] USERS_COLUMNS = new String[] { "_id", "email", "public_key"};
    public static final String[] USERS_GROUPS_COLUMNS = new String[] { "idgroup", "iduser" };

    public DbHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE resource (_id INTEGER PRIMARY KEY, name TEXT unique, user BLOB, password BLOB)");
        db.execSQL("CREATE TABLE groups (_id INTEGER PRIMARY KEY, name TEXT unique, admin TEXT)");
        db.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY, email TEXT unique, public_key BLOB)");
        db.execSQL("CREATE TABLE users_groups (idgroup INTEGER, iduser INTEGER, PRIMARY KEY (idgroup, iduser))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}

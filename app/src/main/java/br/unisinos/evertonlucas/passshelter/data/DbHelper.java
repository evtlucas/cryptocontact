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
    public static final String[] RESOURCE_COLUMNS = new String[] { "_id", "name", "user", "password"};

    public DbHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE resource (_id INTEGER PRIMARY KEY, name TEXT unique, user BLOB, password BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}

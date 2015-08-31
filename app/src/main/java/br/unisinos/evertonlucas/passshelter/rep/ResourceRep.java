package br.unisinos.evertonlucas.passshelter.rep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.passshelter.data.DbHelper;
import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.model.Resource;

/**
 * Class responsible for Resource persistence.
 * Created by everton on 31/08/15.
 */
public class ResourceRep {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private SymmetricEncryption encryption;

    public ResourceRep(Context context, SymmetricEncryption encryption) {
        this.encryption = encryption;
        dbHelper = new DbHelper(context);
    }

    public void insertResource(Resource resource) throws Exception {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = transformResourceDb(resource);

        long result = db.insert(DbHelper.RESOURCE, null, cv);
        if (result > -1) {
            resource.setId(result);
            close();
        }
        else {
            close();
            throw new RuntimeException("Db Insertion Error");
        }
    }

    private ContentValues transformResourceDb(Resource resource) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        ContentValues cv = new ContentValues();
        cv.put("name", resource.getName());
        cv.put("user", resource.getCryptoUser());
        cv.put("password", resource.getCryptoPassword());
        return cv;
    }

    public Resource getResourceByName(String name) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        Resource resource = null;
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.RESOURCE,
                new String[] { "_id", "name", "user", "password"}, "name=?", new String[]{name},
                null, null, null);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {
            resource = new Resource(encryption);
            resource.setId(cursor.getLong(0));
            resource.setName(cursor.getString(1));
            resource.setCryptoUser(cursor.getBlob(2));
            resource.setCryptoPassword(cursor.getBlob(3));
            cursor.moveToNext();
        }
        cursor.close();
        return resource;
    }

    private void close() {
        db.close();
        dbHelper.close();
    }
}

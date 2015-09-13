package br.unisinos.evertonlucas.passshelter.rep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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

        long result = db.insertOrThrow(DbHelper.RESOURCE, null, cv);
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

    public Resource getResourceByName(String name) throws InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException,
            NoSuchPaddingException {
        Resource resource = null;
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.RESOURCE, DbHelper.RESOURCE_COLUMNS, "name = ?", new String[]{name},
                null, null, null);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {
            resource = getResourceFromCursor(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        return resource;
    }

    private Resource getResourceFromCursor(Cursor cursor) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Resource resource;
        resource = new Resource(encryption);
        resource.setId(cursor.getLong(0));
        resource.setName(cursor.getString(1));
        resource.setCryptoUser(cursor.getBlob(2));
        resource.setCryptoPassword(cursor.getBlob(3));
        return resource;
    }

    private void close() {
        db.close();
        dbHelper.close();
    }

    public void updateResource(Resource resource) throws InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException,
            NoSuchPaddingException {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = transformResourceDb(resource);
        long result = db.update(DbHelper.RESOURCE, cv, "_id=?", new String[]{ resource.getId().toString() });
        close();
        if (result < 0)
            throw new RuntimeException("Db Update Error");
    }

    public void saveResource(Resource resource) throws Exception {
        if (resource.getId() != null)
            updateResource(resource);
        else
            insertResource(resource);
    }

    public List<Resource> getAllResources() throws InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        List<Resource> resources = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.RESOURCE, DbHelper.RESOURCE_COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {
            Resource resource = getResourceFromCursor(cursor);
            resources.add(resource);
            cursor.moveToNext();
        }
        cursor.close();
        return resources;
    }

    public void delete(Resource resource) {
        db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.RESOURCE, "_id=?", new String[] {resource.getId().toString()});
    }
}

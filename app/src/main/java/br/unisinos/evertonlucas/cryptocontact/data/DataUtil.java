package br.unisinos.evertonlucas.cryptocontact.data;

import android.os.Environment;

import java.io.File;

/**
 * Util class
 * Created by everton on 17/08/15.
 */
public class DataUtil {
    public static File getFile() {
        File dowloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String name = dowloadDir.getAbsolutePath() + "/data.crypto";
        return new File(name);
    }
}

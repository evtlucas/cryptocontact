package br.unisinos.evertonlucas.passshelter.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class used to verify if the device is connected
 * Created by everton on 09/09/15.
 */
public class NetworkUtil {
    public static void verifyNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnectedOrConnecting())
            throw new RuntimeException("Não existe conectividade");
    }
}

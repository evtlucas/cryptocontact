package br.unisinos.evertonlucas.cryptocontact.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Class responsible for detect when the Android has started
 * Created by everton on 23/07/15.
 */
public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO: Start the service responsible for cryptograph routines
        Log.i("CryptoContact", "AutoStart inicializado");
    }
}

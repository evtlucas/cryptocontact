package br.unisinos.evertonlucas.passshelter.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Class used for dummy initialization
 * Created by everton on 05/09/15.
 */
public class InitActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }
}

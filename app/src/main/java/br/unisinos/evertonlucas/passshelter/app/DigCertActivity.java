package br.unisinos.evertonlucas.passshelter.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.UpdateCertificateStatus;
import br.unisinos.evertonlucas.passshelter.bizserv.InstallService;
import br.unisinos.evertonlucas.passshelter.bizserv.KeyService;

public class DigCertActivity extends AppCompatActivity implements UpdateCertificateStatus {

    private InstallService installService;
    private KeyService keyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dig_cert);

        this.installService = PassShelterApp.getInstance().getInstallService();
        try {
            this.keyService = PassShelterApp.createKeyService(this, this);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar tela de certificado digital", Toast.LENGTH_LONG).show();
            Log.e(this.getResources().getString(R.string.app_name), "Exceção ao iniciar tela de certificado digital", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        installService.setContext(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dig_cert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);*/
        return false;
    }


    public void btnInstallCertificate(View view) {
        keyService.installCertificate();
    }

    public void btnCancel(View view) {
        finish();
        installService.cancel();
    }

    @Override
    public void update(boolean status) {
        InstallState state = status ? InstallState.CERTIFICATE_INSTALLED : InstallState.INITIAL;
        installService.persistState(state);
        if (!status) {
            finish();
        }
        installService.finished(state);
    }
}

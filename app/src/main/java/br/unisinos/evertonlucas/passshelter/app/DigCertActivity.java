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

package br.unisinos.evertonlucas.passshelter.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.service.InstallService;
import br.unisinos.evertonlucas.passshelter.service.KeyService;

public class DigCertActivity extends AppCompatActivity implements UpdateStatus {

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

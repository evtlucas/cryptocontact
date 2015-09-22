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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.rep.UserRep;
import br.unisinos.evertonlucas.passshelter.service.InstallService;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.util.ProgressDialogUtil;

public class DefUserActivity extends AppCompatActivity implements UpdateStatus {

    private InstallService installService;
    private KeyService keyService;
    private UserRep userRep;
    private EditText edtEmail;
    private EditText edtPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_def_user);

        this.edtEmail = (EditText) findViewById(R.id.edtEmail);
        this.edtPassword = (EditText) findViewById(R.id.edtPassword);

        this.installService = PassShelterApp.getInstance().getInstallService();
        try {
            this.keyService = PassShelterApp.createKeyService(this, this);
            this.keyService.loadCertificate();
            progressDialog = ProgressDialogUtil.createProgressDialog(this, "Aguarde a leitura do Certificado Digital");
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar tela de cadastro do usuário", Toast.LENGTH_LONG).show();
            Log.e("Pass Shelter", "Exceção ao iniciar tela do cadastro do usuário", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_def_user, menu);
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

    public void btnDefineUser(View view) {
        if (this.edtEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Campo email não pode ficar em branco", Toast.LENGTH_LONG).show();
            return;
        }
        if (this.edtPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Campo senha não pode ficar em branco", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            this.userRep.saveUser(this.edtEmail.getText().toString());
            this.userRep.savePassword(this.edtPassword.getText().toString());
            this.installService.persistState(InstallState.USER_DEFINED);
            this.installService.finished(InstallState.USER_DEFINED);
        } catch (Exception e) {
            this.installService.persistState(InstallState.CERTIFICATE_INSTALLED);
            Toast.makeText(this, "Exceção ao salvar usuário " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Pass Shelter", "Exceção ao salvar usuário", e);
            finish();
        }
    }

    @Override
    public void update(boolean status) {
        this.installService.setContext(this);
        this.userRep = PassShelterApp.createUserRep(this, this.keyService.getSymmetricEncryption());
        progressDialog.dismiss();
    }
}

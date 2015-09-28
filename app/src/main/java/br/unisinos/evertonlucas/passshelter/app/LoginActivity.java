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

import java.security.NoSuchAlgorithmException;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;
import br.unisinos.evertonlucas.passshelter.service.InstallService;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.service.LoginService;
import br.unisinos.evertonlucas.passshelter.util.ProgressDialogUtil;

import static br.unisinos.evertonlucas.passshelter.app.PassShelterApp.getInstance;

public class LoginActivity extends AppCompatActivity implements UpdateStatus {

    private LocalUserRep localUserRep;
    private EditText txtLoginPassword;
    private InstallService installService;
    private ProgressDialog progressDialog;
    private KeyService keyService;
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle extras = getIntent().getExtras();
        boolean init = extras.getBoolean("init");
        this.txtLoginPassword = (EditText) findViewById(R.id.txtLoginPassword);

        this.installService = getInstance().getInstallService();
        this.keyService = PassShelterApp.createKeyService(this, this);
        try {
            this.progressDialog = ProgressDialogUtil.createProgressDialog(this, "Aguarde a leitura do Certificado Digital");
            this.keyService.loadCertificate();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar tela de cadastro do usuário", Toast.LENGTH_LONG).show();
            Log.e("Pass Shelter", "Exceção ao iniciar tela do cadastro do usuário", e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void btnLogin(View view) {
        try {
            if (!this.loginService.login(this.txtLoginPassword.getText().toString())) {
                Toast.makeText(this, "Senha errada", Toast.LENGTH_LONG).show();
            }
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(this, "Exceção ao salvar usuário " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Pass Shelter", "Exceção ao salvar usuário", e);
        }
    }

    @Override
    public void update(boolean status) {
        progressDialog.dismiss();
        this.installService.setContext(this);
        this.localUserRep = PassShelterApp.createUserRep(this, keyService.getSymmetricEncryption());
        this.loginService = new LoginService(this, this.localUserRep, this.installService);
    }
}

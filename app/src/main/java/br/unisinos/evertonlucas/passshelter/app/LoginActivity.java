package br.unisinos.evertonlucas.passshelter.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.UpdateCertificateStatus;
import br.unisinos.evertonlucas.passshelter.bizserv.InstallService;
import br.unisinos.evertonlucas.passshelter.bizserv.KeyService;
import br.unisinos.evertonlucas.passshelter.rep.UserRep;
import br.unisinos.evertonlucas.passshelter.util.ProgressDialogUtil;

import static br.unisinos.evertonlucas.passshelter.app.PassShelterApp.*;

public class LoginActivity extends AppCompatActivity implements UpdateCertificateStatus {

    private UserRep userRep;
    private EditText txtLoginPassword;
    private InstallService installService;
    private ProgressDialog progressDialog;
    private KeyService keyService;

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

    }

    @Override
    public void update(boolean status) {
        progressDialog.dismiss();
        this.installService.setContext(this);
        this.userRep = PassShelterApp.createUserRep(this, keyService.getSymmetricEncryption());
    }
}

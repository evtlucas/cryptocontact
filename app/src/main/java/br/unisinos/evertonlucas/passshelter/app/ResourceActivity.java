package br.unisinos.evertonlucas.passshelter.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.UpdateCertificateStatus;
import br.unisinos.evertonlucas.passshelter.bizserv.KeyService;
import br.unisinos.evertonlucas.passshelter.model.Resource;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;
import br.unisinos.evertonlucas.passshelter.util.SharedPrefUtil;

public class ResourceActivity extends AppCompatActivity implements UpdateCertificateStatus,
        DialogInterface.OnClickListener{

    private KeyService keyService;
    private ResourceRep resourceRep;
    private EditText txtResource;
    private EditText txtUser;
    private EditText txtPassword;
    private Resource resource = null;
    private boolean insercao = false;
    private boolean isFinalized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);

        setFields();

        if (this.getActionBar() != null)
            this.getActionBar().setDisplayHomeAsUpEnabled(true);

        this.keyService = PassShelterApp.createKeyService(this, this);
        this.resourceRep = new ResourceRep(this, this.keyService.getSymmetricEncryption());

        try {
            Bundle extras = getIntent().getExtras();
            String name = SharedPrefUtil.readFrom(this, SharedPrefUtil.RESOURCE, SharedPrefUtil.RESOURCE_NAME);
            if (name.trim().isEmpty() && (extras != null))
                name = extras.getString("name") != null ? extras.getString("name") : "";
            if (!name.trim().isEmpty()) {
                this.resource = this.resourceRep.getResourceByName(name);
                fillFieldsFromResource(this.resource);
            } else
                insercao = true;
            SharedPrefUtil.delete(this, SharedPrefUtil.RESOURCE, SharedPrefUtil.RESOURCE_NAME);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar tela de recursos", Toast.LENGTH_LONG).show();
            Log.e("Pass Shelter", "Exceção ao iniciar tela de recursos", e);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isFinalized) {
            menu.getItem(0).setEnabled(!insercao);
            menu.getItem(1).setEnabled(!insercao);
        }
        return true;
    }

    private void fillFieldsFromResource(Resource resource) {
        this.txtResource.setText(resource.getName());
        this.txtUser.setText(resource.getUser());
        this.txtPassword.setText(resource.getPassword());
    }

    private void setFields() {
        this.txtResource = (EditText) findViewById(R.id.txtActivityResource);
        this.txtUser = (EditText) findViewById(R.id.txtActivityUser);
        this.txtPassword = (EditText) findViewById(R.id.txtActivityPassword);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_resource, menu);
        isFinalized = true;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                AlertDialog dialog = createAlertDialog();
                dialog.show();
                return true;
            case R.id.action_send:
                SharedPrefUtil.writeTo(this, SharedPrefUtil.RESOURCE, SharedPrefUtil.RESOURCE_NAME, this.resource.getName());
                startActivity(new Intent(this, SendResourceActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteResource() {
        this.resourceRep.delete(this.resource);
        NavUtils.navigateUpFromSameTask(this);
    }

    private AlertDialog createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirmation));
        builder.setPositiveButton(getString(R.string.button_yes), this);
        builder.setNegativeButton(getString(R.string.button_no), this);
        return builder.create();
    }

    public void btnSaveResource(View view) {
        try {
            verifyResourceName();
            verifyUserName();
            verifyPasswordName();

            this.resource = fillResourceFromFields(this.resource);

            this.resourceRep.saveResource(resource);
            NavUtils.navigateUpFromSameTask(this);
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "Recurso já salvo anteriormente", Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar recurso", Toast.LENGTH_LONG).show();
            Log.e("Pass Shelter", "Exceção ao salvar recurso", e);
        }
    }

    private Resource fillResourceFromFields(Resource oldResource) {
        Resource resource;
        if (oldResource == null)
            resource = new Resource(this.keyService.getSymmetricEncryption());
        else
            resource = oldResource;
        resource.setName(this.txtResource.getText().toString());
        resource.setUser(this.txtUser.getText().toString());
        resource.setPassword(this.txtPassword.getText().toString());
        return resource;
    }

    private void verifyPasswordName() {
        if (this.txtPassword.getText().toString().trim().isEmpty())
            throw new IllegalArgumentException("Senha preenchida incorretamente");
    }

    private void verifyUserName() {
        if (this.txtUser.getText().toString().trim().isEmpty())
            throw new IllegalArgumentException("Nome do usuário preenchido incorretamente");
    }

    private void verifyResourceName() {
        if (this.txtResource.getText().toString().trim().isEmpty())
            throw new IllegalArgumentException("Nome do recurso preenchido incorretamente");
    }

    @Override
    public void update(boolean status) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                deleteResource();
        }
    }
}

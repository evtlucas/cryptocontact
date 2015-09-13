package br.unisinos.evertonlucas.passshelter.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.UpdateCertificateStatus;
import br.unisinos.evertonlucas.passshelter.bizserv.KeyService;
import br.unisinos.evertonlucas.passshelter.data.ExportSecretKeyData;
import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.model.Resource;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;
import br.unisinos.evertonlucas.passshelter.util.ConfirmationDialog;


public class Main extends AppCompatActivity implements UpdateCertificateStatus,
        ConfirmationDialog.ConfirmationDialogListener{

    private Context myContext;
    private Button button;
    private KeyService service;
    private ResourceRep resourceRep;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = this;

        this.listView = (ListView) findViewById(R.id.list_view_main);
        this.listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        this.listView.setLongClickable(true);
        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> resMap = (Map<String, String>) parent.getItemAtPosition(position);
                try {
                    Resource res = resourceRep.getResourceByName(resMap.get("name"));
                    Intent intent = new Intent(myContext, ResourceActivity.class);
                    intent.putExtra("name", res.getName());
                    myContext.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro ao editar recurso", Toast.LENGTH_LONG).show();
                    Log.e("Pass Shelter", "Exceção ao editar recurso", e);
                }
                return false;
            }
        });

        try {
            this.service = PassShelterApp.createKeyService(this, this);
            this.resourceRep = new ResourceRep(this, this.service.getSymmetricEncryption());

            /*for (int i = 0; i < 5; i++)
                insertTestResource(this.service.getSymmetricEncryption(), i);*/

            loadListAdapter(listView, this.resourceRep.getAllResources(), R.layout.list_resources);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar tela principal", Toast.LENGTH_LONG).show();
            Log.e("Pass Shelter", "Exceção ao iniciar tela principal", e);
        }
    }

    public void loadListAdapter(ListView listView, List<Resource> resources, int resource) {
        String[] from = { "name" };
        int[] to = { R.id.txtResLabel };
        SimpleAdapter adapter = new SimpleAdapter(this, listResources(resources), resource, from, to);
        listView.setAdapter(adapter);
    }

    private List<? extends Map<String, String>> listResources(List<Resource> resources) {
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        for (Resource resource : resources) {
            Map<String, String> map = new HashMap<>();
            map.put("name", resource.getName());
            mapList.add(map);
        }
        return mapList;
    }

    public void insertTestResource(SymmetricEncryption encryption, int i) throws Exception {
        String resource = "resource " + Integer.toString(i);
        String usr = "usr";
        String pwd = "pwd";
        insertResource(encryption, resource, usr, pwd);
    }

    private void insertResource(SymmetricEncryption encryption, String resName, String user, String password) throws Exception {
        Resource resource = new Resource(encryption);
        resource.setName(resName);
        resource.setUser(user);
        resource.setPassword(password);
        resourceRep.insertResource(resource);
    }

    @Override
    protected void onResume() {
        this.service.reReadCertificate();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        switch (id) {
            case R.id.action_backup:
                try {
                    service.exportCryptographicKey();
                } catch (Exception e) {
                    ExportSecretKeyData.throwException(e, this);
                }
                return true;
            case R.id.action_restore:
                service.importCryptographicKey();
                return true;
            case R.id.action_add:
                startActivity(new Intent(this, ResourceActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnInstallCertificate(View v) {
        service.installCertificate();
    }

    @Override
    public void update(boolean status) {
        /*ImageView imgView = (ImageView) findViewById(R.id.imgStatusDigCert);
        if (status) {
            imgView.setImageResource(R.drawable.ic_thumb_up);
        } else {
            imgView.setImageResource(R.drawable.ic_thumb_down);
        }*/
    }

    @Override
    public void onConfirmationPositive() {
        Toast.makeText(this, "Clicado", Toast.LENGTH_LONG).show();
    }
}

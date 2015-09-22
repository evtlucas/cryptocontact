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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.async.VerifyProcessAsyncTask;
import br.unisinos.evertonlucas.passshelter.data.ExportSecretKeyData;
import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.model.Resource;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;
import br.unisinos.evertonlucas.passshelter.rep.UserRep;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.util.ProgressDialogUtil;


public class Main extends AppCompatActivity implements UpdateStatus{

    private Context myContext;
    private Button button;
    private KeyService service;
    private ResourceRep resourceRep;
    private ListView listView;
    private ProgressDialog progressDialog;
    private UserRep userRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = this;

        createListView();

        try {
            this.service = PassShelterApp.createKeyService(this, this);
            this.resourceRep = new ResourceRep(this, this.service.getSymmetricEncryption());
            this.userRep = PassShelterApp.createUserRep(this, this.service.getSymmetricEncryption());

            loadListView();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar tela principal", Toast.LENGTH_LONG).show();
            Log.e("Pass Shelter", "Exceção ao iniciar tela principal", e);
        }
    }

    private void loadListView() throws InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        loadListAdapter(listView, this.resourceRep.getAllResources(), R.layout.list_resources);
    }

    private void createListView() {
        this.listView = (ListView) findViewById(R.id.list_view_main);
        this.listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        this.listView.setLongClickable(true);
        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Map<String, String> resMap = (Map<String, String>) parent.getItemAtPosition(position);
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
    }

    public void loadListAdapter(ListView listView, List<Resource> resources, int resource) {
        String[] from = { "name" };
        int[] to = { R.id.txtResLabel };
        SimpleAdapter adapter = new SimpleAdapter(this, listResources(resources), resource, from, to);
        listView.setAdapter(adapter);
    }

    private List<? extends Map<String, String>> listResources(List<Resource> resources) {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (Resource resource : resources) {
            Map<String, String> map = new HashMap<>();
            map.put("name", resource.getName());
            mapList.add(map);
        }
        return mapList;
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
            case R.id.action_refresh:
                verifyResources();
                return true;
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

    private void verifyResources() {
        this.progressDialog = ProgressDialogUtil.createProgressDialog(this, getString(R.string.process_verify_resources));
        try {
            new VerifyProcessAsyncTask(this, service, resourceRep, this, new ParseData(), userRep)
                    .execute();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao verificar recursos", Toast.LENGTH_LONG).show();
            Log.e(this.getResources().getString(R.string.app_name), "Exceção ao verificar recursos", e);
            this.progressDialog.dismiss();
        }
    }

    @Override
    public void update(boolean status) {
        if (status) {
            try {
                loadListView();
                if (this.progressDialog != null)
                    this.progressDialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, "Erro ao recarregar recursos", Toast.LENGTH_LONG).show();
                Log.e(this.getResources().getString(R.string.app_name), "Exceção ao recarregar recursos", e);
            }
        }
    }
}

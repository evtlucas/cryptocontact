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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.net.ConnectException;
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
import br.unisinos.evertonlucas.passshelter.analytics.AnalyticsMessage;
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.async.VerifyProcessAsyncTask;
import br.unisinos.evertonlucas.passshelter.data.ExportSecretKeyData;
import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.model.Resource;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.util.NetworkUtil;
import br.unisinos.evertonlucas.passshelter.util.ProgressDialogUtil;
import br.unisinos.evertonlucas.passshelter.util.ShowLogExceptionUtil;
import br.unisinos.evertonlucas.passshelter.util.ToastUtil;


public class Main extends AppCompatActivity implements UpdateStatus{

    private Context myContext;
    private KeyService service;
    private ResourceRep resourceRep;
    private ListView listView;
    private ProgressDialog progressDialog;
    private LocalUserRep localUserRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = this;

        createListView();

        try {
            this.service = PassShelterApp.createKeyService(this, this);
            this.resourceRep = new ResourceRep(this, this.service.getSymmetricEncryption());
            this.localUserRep = PassShelterApp.createUserRep(this, this.service.getSymmetricEncryption());

            loadListView();
        } catch (Exception e) {
            ShowLogExceptionUtil.showAndLogException(this, "Erro ao iniciar tela principal", e);
        }
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
                exportCryptographicKey();
                return true;
            case R.id.action_restore:
                service.importCryptographicKey();
                return true;
            case R.id.action_add:
                startResourceActivity();
                return true;
            case R.id.action_group:
                startGroupsActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadListView() throws InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        loadListAdapter(listView, this.resourceRep.getAllResources(), R.layout.list_resources);
    }

    private void createListView() {
        this.listView = (ListView) findViewById(R.id.list_view_main);
        this.listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        this.listView.setLongClickable(true);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Map<String, String> resMap = (Map<String, String>) parent.getItemAtPosition(position);
                    Resource res = resourceRep.getResourceByName(resMap.get("name"));
                    Intent intent = new Intent(myContext, ResourceActivity.class);
                    intent.putExtra("name", res.getName());
                    myContext.startActivity(intent);
                } catch (Exception e) {
                    ShowLogExceptionUtil.showAndLogException(myContext, "Erro ao abrir recurso para edição", e);
                }
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

    private void startResourceActivity() {
        AnalyticsMessage.sendMessageToAnalytics("Main", "UX", "startResourceActivity");
        startActivity(new Intent(this, ResourceActivity.class));
    }

    private void startGroupsActivity() {
        try {
            AnalyticsMessage.sendMessageToAnalytics("Main", "UX", "startGroupsActivity");
            Intent intent = new Intent(this, GroupsActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            ShowLogExceptionUtil.showAndLogException(this, "Erro ao abrir tela de grupos", e);
        }
    }

    private void exportCryptographicKey() {
        try {
            service.exportCryptographicKey();
        } catch (Exception e) {
            ExportSecretKeyData.throwException(e, this);
        }
    }

    private void verifyResources() {
        this.progressDialog = ProgressDialogUtil.createProgressDialog(this, getString(R.string.process_verify_resources));
        try {
            AnalyticsMessage.sendMessageToAnalytics("Main", "UX", "verifyResources");
            NetworkUtil.verifyNetwork(this);
            new VerifyProcessAsyncTask(this, service, resourceRep, this, new ParseData(), localUserRep)
                    .execute();
        } catch (ConnectException e) {
            ToastUtil.showToastMessage(this, getResources().getString(R.string.msg_no_connection));
            this.progressDialog.dismiss();
        } catch (Exception e) {
            ShowLogExceptionUtil.showAndLogException(this, "Erro ao verificar recursos", e);
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
                ShowLogExceptionUtil.showAndLogException(this, "Erro ao recarregar recursos", e);
            }
        } else {
            Toast.makeText(this, "Erro ao recarregar recursos", Toast.LENGTH_LONG).show();
            if (this.progressDialog != null)
                this.progressDialog.dismiss();
        }
    }
}

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
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.SendProcessAsyncTask;
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;
import br.unisinos.evertonlucas.passshelter.rep.UserRep;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.util.NetworkUtil;
import br.unisinos.evertonlucas.passshelter.util.ProgressDialogUtil;

public class SendResourceActivity extends AppCompatActivity implements FinishedFind, UpdateStatus {

    private ListView listViewEmails;
    private TextView txtDestinationEmail;
    private ParseData parseData;
    private ProgressDialog progressDialog;
    private KeyService service;
    private ResourceRep resourceRep;
    private UserRep userRep;
    private String resourceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_resource);

        this.listViewEmails = (ListView) findViewById(R.id.list_view_emails);
        this.txtDestinationEmail = (EditText) findViewById(R.id.txtDestinationEmail);
        this.parseData = new ParseData();
        this.service = PassShelterApp.createKeyService(this, this);
        this.resourceRep = new ResourceRep(this, this.service.getSymmetricEncryption());
        this.userRep = PassShelterApp.createUserRep(this, this.service.getSymmetricEncryption());

        if (this.getActionBar() != null)
            this.getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        this.resourceName = intent.getExtras().getString("resourceName");
    }

    public void loadListAdapter(ListView listView, List<String> resources, int resource) {
        String[] from = { "name" };
        int[] to = { R.id.txtSendResource };
        SimpleAdapter adapter = new SimpleAdapter(this, listResources(resources), resource, from, to);
        listView.setAdapter(adapter);
    }

    private List<? extends Map<String, String>> listResources(List<String> resources) {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (String resource : resources) {
            Map<String, String> map = new HashMap<>();
            if (resource.length() > 25)
                resource = resource.substring(0, 25).concat("...");
            map.put("name", resource);
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    public void btnSearch(View view) {
        this.progressDialog = ProgressDialogUtil.createProgressDialog(this, getString(R.string.wait_emails));
        try {
            NetworkUtil.verifyNetwork(this);
            parseData.getEMailUsers(this.txtDestinationEmail.getText().toString(), this);
        } catch (Exception e) {
            this.progressDialog.dismiss();
            Toast.makeText(this, "Erro ao pesquisar usuários", Toast.LENGTH_LONG).show();
            Log.e(this.getResources().getString(R.string.app_name), "Exceção ao pesquisar usuários", e);
        }
    }

    @Override
    public void notifyFinished(List<String> emailList) {
        this.progressDialog.dismiss();
        loadListAdapter(this.listViewEmails, emailList, R.layout.list_send_resources);
    }

    @Override
    public void notifyError() {
        this.progressDialog.dismiss();
    }

    public void btnSendResource(View v) {
        try {
            this.progressDialog = ProgressDialogUtil.createProgressDialog(this, getString(R.string.process_send_resource));
            GridLayout parent = (GridLayout) v.getParent();
            TextView txtSendResource = (TextView) parent.getChildAt(0);
            new SendProcessAsyncTask(this, service, resourceRep, this, parseData, userRep)
                    .execute(txtSendResource.getText().toString(), resourceName);
        } catch (Exception e) {
            this.progressDialog.dismiss();
            Toast.makeText(this, "Erro ao enviar recurso", Toast.LENGTH_LONG).show();
            Log.e(this.getResources().getString(R.string.app_name), "Exceção ao enviar recurso", e);
        }
    }

    @Override
    public void update(boolean status) {
        this.progressDialog.dismiss();
        if (status)
            Toast.makeText(this, "Recurso enviado com sucesso", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Erro ao enviar recurso", Toast.LENGTH_LONG).show();
    }
}

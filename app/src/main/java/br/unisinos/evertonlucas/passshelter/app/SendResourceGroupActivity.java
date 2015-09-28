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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.SendProcessGroupAsyncTask;
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.model.Group;
import br.unisinos.evertonlucas.passshelter.rep.GroupsRep;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.util.ProgressDialogUtil;
import br.unisinos.evertonlucas.passshelter.util.SharedPrefUtil;

public class SendResourceGroupActivity extends AppCompatActivity implements UpdateStatus {

    private GroupsRep groupsRep;
    private ProgressDialog progressDialog;
    private ParseData parseData;
    private KeyService service;
    private ResourceRep resourceRep;
    private LocalUserRep localUserRep;
    private String resourceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_resource_group);
        ListView lstGroups = (ListView) findViewById(R.id.list_view_groups_send);

        this.parseData = new ParseData();
        this.service = PassShelterApp.createKeyService(this, this);
        this.resourceRep = new ResourceRep(this, this.service.getSymmetricEncryption());
        this.localUserRep = PassShelterApp.createUserRep(this, this.service.getSymmetricEncryption());
        this.resourceName = getIntent().getExtras().getString("resourceName");

        groupsRep = new GroupsRep(this);
        loadListAdapter(lstGroups, groupsRep.getGroupsName(), R.layout.list_send_resources_group);
    }

    public void loadListAdapter(ListView listView, List<String> names, int resource) {
        String[] from = { "name" };
        int[] to = { R.id.txt_send_resource_group };
        SimpleAdapter adapter = new SimpleAdapter(this, listResources(names), resource, from, to);
        listView.setAdapter(adapter);
    }

    private List<? extends Map<String, String>> listResources(List<String> names) {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (String name : names) {
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_resource_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, ResourceActivity.class);
            SharedPrefUtil.writeTo(this, SharedPrefUtil.RESOURCE, SharedPrefUtil.RESOURCE_NAME, this.resourceName);
            NavUtils.navigateUpTo(this, intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnSendResource(View view) {
        try {
            this.progressDialog = ProgressDialogUtil.createProgressDialog(this, getString(R.string.process_send_resource));
            RelativeLayout parent = (RelativeLayout) view.getParent();
            TextView txtSendResource = (TextView) parent.getChildAt(0);
            Group group = this.groupsRep.getGroupByName(txtSendResource.getText().toString());
            new SendProcessGroupAsyncTask(this, service, resourceRep, this, parseData, localUserRep, group)
                    .execute(resourceName);
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

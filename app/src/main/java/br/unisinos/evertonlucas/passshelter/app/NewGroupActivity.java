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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.rep.GroupsRep;

public class NewGroupActivity extends AppCompatActivity {

    private EditText txtNameNewGroup;
    private GroupsRep groupsRep;
    private String localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        txtNameNewGroup = (EditText) findViewById(R.id.txtNameNewGroup);
        groupsRep = new GroupsRep(this);

        Bundle extras = getIntent().getExtras();
        localUser = extras.getString("local_user");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void btnVerifyNameNewGroup(View view) {
        try {
            if (txtNameNewGroup.getText().length() == 0) {
                Toast.makeText(this, getString(R.string.name_new_group_blank), Toast.LENGTH_LONG).show();
                return;
            }
            if (groupsRep.getGroupByName(txtNameNewGroup.getText().toString()) != null) {
                Toast.makeText(this, getString(R.string.name_new_group_found), Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(this, UsersNewGroupActivity.class);
            intent.putExtra("group_name", txtNameNewGroup.getText().toString());
            intent.putExtra("local_user", localUser);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao verificar nome do grupo", Toast.LENGTH_LONG).show();
            Log.e(this.getResources().getString(R.string.app_name), "Exceção ao verificar nome do grupo", e);
        }
    }
}

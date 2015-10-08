
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.rep.GroupsRep;

public class GroupsActivity extends AppCompatActivity {

    private GroupsRep groupsRep;
    private ListView lstGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        lstGroups = (ListView) findViewById(R.id.list_view_groups);

        groupsRep = new GroupsRep(this);
        loadListAdapter(lstGroups, groupsRep.getGroupsName(), R.layout.list_groups);
    }

    public void loadListAdapter(ListView listView, List<String> names, int resource) {
        String[] from = { "name" };
        int[] to = { R.id.txtGroup };
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
        getMenuInflater().inflate(R.menu.menu_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_group) {
            startNewGroupActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startNewGroupActivity() {
        Intent intent = new Intent(this, NewGroupActivity.class);
        startActivity(intent);
    }

    public void btnViewGroup(View view) {
        RelativeLayout layout = (RelativeLayout) view.getParent();
        TextView txtGroup = (TextView) layout.getChildAt(0);
        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra("group_name", txtGroup.getText().toString());
        startActivity(intent);
    }
}

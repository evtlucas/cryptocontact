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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.model.Group;
import br.unisinos.evertonlucas.passshelter.model.ParseUser;
import br.unisinos.evertonlucas.passshelter.model.User;
import br.unisinos.evertonlucas.passshelter.service.GroupsService;
import br.unisinos.evertonlucas.passshelter.util.ConfirmationDialog;
import br.unisinos.evertonlucas.passshelter.util.ProgressDialogUtil;
import br.unisinos.evertonlucas.passshelter.view.DelayAutoCompleteTextView;
import br.unisinos.evertonlucas.passshelter.view.UsersAutoCompleteAdapter;

public class UsersNewGroupActivity extends AppCompatActivity implements ConfirmationDialog.ConfirmationDialogListener {

    private DelayAutoCompleteTextView tv;
    private ListView lstUsers;
    private List<ParseUser> emailUsersGroup = new ArrayList<>();
    private ParseUser parseUser;
    private String groupName = "";
    private String localUser = "";
    private GroupsService groupsService;
    private ProgressDialog progressDialog;
    private boolean foundGroup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_new_group);
        lstUsers = (ListView) findViewById(R.id.lst_users_new_group);

        this.groupsService = new GroupsService(this);

        initializeAutoCompleteTextView();

        defineInitialParameters();
    }

    private void defineInitialParameters() {
        Bundle extras = getIntent().getExtras();
        String groupName = extras.getString("group_name");
        this.groupName = groupName;
        setTitle(groupName);
        localUser = extras.getString("local_user");
        try {
            Group group = this.groupsService.getGroupByName(groupName);
            if (group != null) {
                foundGroup = true;
                for(User user : group.getUsers()) {
                    ParseUser parseUser = new ParseUser(user.getEmail(), user.getPublicKey());
                    emailUsersGroup.add(parseUser);
                }
                refreshListAdapter();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeAutoCompleteTextView() {
        tv = (DelayAutoCompleteTextView) findViewById(R.id.txt_search_user_new_group);
        tv.setThreshold(4);
        tv.setAdapter(new UsersAutoCompleteAdapter(this));
        tv.setLoadingIndicator((ProgressBar) findViewById(R.id.pb_loading_indicator));
        tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseUser user = (ParseUser) parent.getItemAtPosition(position);
                tv.setText("");
                insertUserIntoList(user);
            }
        });
    }

    private void insertUserIntoList(ParseUser parseUser) {
        ConfirmationDialog dialog = ConfirmationDialog.newInstance(this);
        dialog.setTitle(R.string.confirmation_new_user)
                .setMessage(R.string.confirmation_new_user_message)
                .show(this.getFragmentManager(), "");
        this.parseUser = parseUser;
    }

    public void loadListAdapter(ListView listView, List<ParseUser> emailList, int resource) {
        String[] from = { "email" };
        int[] to = { R.id.txt_user_new_group};
        SimpleAdapter adapter = new SimpleAdapter(this, listResources(emailList), resource, from, to);
        listView.setAdapter(adapter);
    }

    private List<? extends Map<String, String>> listResources(List<ParseUser> parseUsers) {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (ParseUser parseUser : parseUsers) {
            Map<String, String> map = new HashMap<>();
            map.put("email", parseUser.getEmail());
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_users_new_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_create_group) {
            if (emailUsersGroup.size() > 0)
                saveGroupAndUsers();
            else
                Toast.makeText(this, getString(R.string.save_group_not_allowed), Toast.LENGTH_LONG).show();
            return true;
        } else if (id == android.R.id.home) {
            redirectToCorrectActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void redirectToCorrectActivity() {
        if (!foundGroup) {
            Intent upIntent = new Intent(this, NewGroupActivity.class);
            defineExtras(upIntent);
            NavUtils.navigateUpTo(this, upIntent);
        }
        else {
            Intent upIntent = new Intent(this, GroupActivity.class);
            defineExtras(upIntent);
            NavUtils.navigateUpTo(this, upIntent);
        }
    }

    private void saveGroupAndUsers() {
        this.progressDialog = ProgressDialogUtil.createProgressDialog(this, getString(R.string.saving_group));
        try {
            Group group = foundGroup ? this.groupsService.getGroupByName(this.groupName) : new Group();
            group.setName(groupName);
            group.setAdmin(localUser);
            this.groupsService.saveGroup(group, emailUsersGroup);
            redirectAfterSaveToCorrectActivity();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir tela de grupos", Toast.LENGTH_LONG).show();
            Log.e(this.getResources().getString(R.string.app_name), "Exceção ao abrir tela de grupos", e);
        } finally {
            this.progressDialog.dismiss();
        }
    }

    private void redirectAfterSaveToCorrectActivity() {
        if (!foundGroup)
            openGroupsActivity();
        else
            openGroupActivity();
    }

    private void openGroupsActivity() {
        Intent intent = new Intent(this, GroupsActivity.class);
        intent.putExtra("local_user", localUser);
        startActivity(intent);
    }

    private void openGroupActivity() {
        Intent intent = new Intent(this, GroupActivity.class);
        defineExtras(intent);
        startActivity(intent);
    }

    private void defineExtras(Intent intent) {
        intent.putExtra("local_user", localUser);
        intent.putExtra("group_name", groupName);
    }

    public void btnClearUserGroup(View view) {
        RelativeLayout layout = (RelativeLayout) view.getParent();
        TextView txtUserGroup = (TextView) layout.getChildAt(0);
        ParseUser user = null;
        for(ParseUser userList : emailUsersGroup) {
            if (userList.getEmail().trim().equals(txtUserGroup.getText().toString()))
                user = userList;
        }
        if (user != null) {
            emailUsersGroup.remove(user);
            refreshListAdapter();
        }
    }

    @Override
    public void onConfirmationPositive() {
        emailUsersGroup.add(parseUser);
        refreshListAdapter();
    }

    private void refreshListAdapter() {
        loadListAdapter(lstUsers, emailUsersGroup, R.layout.list_users_new_group);
    }
}

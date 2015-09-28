package br.unisinos.evertonlucas.passshelter.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.model.Group;
import br.unisinos.evertonlucas.passshelter.model.User;
import br.unisinos.evertonlucas.passshelter.rep.GroupsRep;

public class GroupActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    private GroupsRep groupsRep;
    private String localUser;
    private ListView lstUsersGroup;
    private EditText txtGroupName;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        lstUsersGroup = (ListView) findViewById(R.id.lst_users_group);
        txtGroupName = (EditText) findViewById(R.id.txt_group_name);
        groupsRep = new GroupsRep(this);

        Bundle extras = getIntent().getExtras();
        localUser = extras.getString("local_user");
        String groupName = extras.getString("group_name");
        setTitle(groupName);
        try {
            group = groupsRep.getGroupByName(groupName);
            txtGroupName.setText(groupName);
            loadListAdapter(lstUsersGroup, groupsRep.getUsersGroup(group), R.layout.list_users_group);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao inicializar tela de grupo", Toast.LENGTH_LONG).show();
            Log.e(this.getResources().getString(R.string.app_name), "Exceção ao inicializar tela de grupo", e);
        }
    }

    public void loadListAdapter(ListView listView, List<User> users, int resource) {
        String[] from = { "email" };
        int[] to = { R.id.txt_user_group };
        SimpleAdapter adapter = new SimpleAdapter(this, listResources(users), resource, from, to);
        listView.setAdapter(adapter);
    }

    private List<? extends Map<String, String>> listResources(List<User> users) {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (User user : users) {
            Map<String, String> map = new HashMap<>();
            map.put("email", user.getEmail());
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_edit_users_group:
                Intent intent = new Intent(this, UsersNewGroupActivity.class);
                intent.putExtra("group_name", group.getName());
                startActivity(intent);
                return true;
            case android.R.id.home:
                navigateBack();
                return true;
            case R.id.action_save_group:
                saveGroup();
                return true;
            case R.id.action_delete_group:
                AlertDialog dialog = createDeleteDialog();
                dialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog createDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_group_confirmation));
        builder.setPositiveButton(getString(R.string.button_yes), this);
        builder.setNegativeButton(getString(R.string.button_no), this);
        return builder.create();
    }

    private void navigateBack() {
        Intent intent;
        intent = new Intent(this, GroupsActivity.class);
        intent.putExtra("group_name", group.getName());
        NavUtils.navigateUpTo(this, intent);
    }

    private void saveGroup() {
        try {
            group.setName(txtGroupName.getText().toString());
            groupsRep.save(group);
            navigateBack();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "Grupo já existe", Toast.LENGTH_LONG).show();
            Log.e(this.getResources().getString(R.string.app_name), "Grupo já existe", e);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar grupo", Toast.LENGTH_LONG).show();
            Log.e(this.getResources().getString(R.string.app_name), "Exceção ao salvar grupo", e);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                deleteGroup();
        }
    }

    private void deleteGroup() {
        groupsRep.delete(group);
        navigateBack();
    }
}

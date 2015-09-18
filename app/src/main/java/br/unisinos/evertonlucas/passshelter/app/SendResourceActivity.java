package br.unisinos.evertonlucas.passshelter.app;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.util.ProgressDialogUtil;

public class SendResourceActivity extends AppCompatActivity implements FinishedFind {

    private ListView listViewEmails;
    private TextView txtDestinationEmail;
    private ParseData parseData;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_resource);

        this.listViewEmails = (ListView) findViewById(R.id.list_view_emails);
        this.txtDestinationEmail = (EditText) findViewById(R.id.txtDestinationEmail);
        this.parseData = new ParseData();

        if (this.getActionBar() != null)
            this.getActionBar().setDisplayHomeAsUpEnabled(true);
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
        parseData.getEMailUsers(this.txtDestinationEmail.getText().toString(), this);
    }

    @Override
    public void notifyFinished(List<String> emailList) {
        this.progressDialog.dismiss();
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emailList);
        this.listViewEmails.setAdapter(adapter);
    }

    @Override
    public void notifyError() {
        this.progressDialog.dismiss();
    }
}

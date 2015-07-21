package br.unisinos.evertonlucas.cryptocontact;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.security.KeyStoreException;
import java.util.List;

import br.unisinos.evertonlucas.cryptocontact.service.KeyStoreService;


public class Main extends ActionBarActivity {

    private TextView txtAliases;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAliases = (TextView) findViewById(R.id.txtAliases);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnAtualizaAliases(View v) {
        KeyStoreService service = new KeyStoreService();
        try {
            List<String> aliases = service.getChaves();
            txtAliases.setText(aliases.toString());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

    }
}

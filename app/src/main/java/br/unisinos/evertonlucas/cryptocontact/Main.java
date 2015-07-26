package br.unisinos.evertonlucas.cryptocontact;

import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.cert.X509Certificate;
import java.util.List;

import br.unisinos.evertonlucas.cryptocontact.async.UpdateCertificate;
import br.unisinos.evertonlucas.cryptocontact.bizserv.KeyService;
import br.unisinos.evertonlucas.cryptocontact.util.KeyStoreUtil;


public class Main extends ActionBarActivity implements KeyChainAliasCallback, UpdateCertificate {

    private TextView txtAliases;
    private Button button;
    private KeyService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAliases = (TextView) findViewById(R.id.txtAliases);
        this.service = new KeyService(this);
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
        /*KeyChain.choosePrivateKeyAlias(this, this,
                new String[]{"RSA"}, null, null, -1, null);*/
        try {
            String status = service.isCertificateAvailable() ?
                    "Certificado Presente" : "Certificado Ausente";
            txtAliases.setText(status);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void alias(String alias) {
        System.out.println("Alias: " + alias);
    }

    @Override
    public void updateCertificateInfo(X509Certificate certificate) {
        txtAliases.setText(certificate.getSubjectDN().getName());
    }
}

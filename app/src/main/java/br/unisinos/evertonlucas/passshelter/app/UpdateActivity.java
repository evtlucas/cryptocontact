package br.unisinos.evertonlucas.passshelter.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.service.InitService;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.service.UpdateService;

public class UpdateActivity extends AppCompatActivity implements UpdateStatus {

    private InitService initService;
    private KeyService keyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        int versionCode = getIntent().getExtras().getInt("version_code");
        boolean firstInstallation = getIntent().getExtras().getBoolean("first_installation");

        this.initService = PassShelterApp.getInstance().getInitService();

        try {
            keyService = PassShelterApp.createKeyService(this, this);
            keyService.loadCertificate();
            new UpdateService(this, versionCode, firstInstallation).update(this, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(boolean status) {
        if (status) {
            try {
                initService.finished(InstallState.READY);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

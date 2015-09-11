package br.unisinos.evertonlucas.passshelter.bizserv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.app.DefUserActivity;
import br.unisinos.evertonlucas.passshelter.app.DigCertActivity;
import br.unisinos.evertonlucas.passshelter.app.InstallState;
import br.unisinos.evertonlucas.passshelter.app.InstallStepFinished;
import br.unisinos.evertonlucas.passshelter.app.LoginActivity;
import br.unisinos.evertonlucas.passshelter.app.PassShelterApp;
import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.rep.UserRep;
import br.unisinos.evertonlucas.passshelter.util.NetworkUtil;
import br.unisinos.evertonlucas.passshelter.util.ParseUtil;
import br.unisinos.evertonlucas.passshelter.util.ProgressDialogUtil;
import br.unisinos.evertonlucas.passshelter.util.SharedPrefUtil;

/**
 * Class designed for manage initialization process
 * Created by everton on 06/09/15.
 */
public class InstallService implements InstallStepFinished, Serializable {

    private Context context;

    public InstallService(Context context) {
        this.context = context;
        ParseUtil.registerParse(this.context);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private InstallState getInstallState() {
        int state = SharedPrefUtil.readIntFrom(context, SharedPrefUtil.KEYCHAIN_PREF, SharedPrefUtil.KEYCHAIN_PREF_STATE);
        if (state == -1) state = 0;
        return InstallState.values()[state];
    }

    private void startActivity(Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void initialize() {
        InstallState installState = getInstallState();
        startActivityFromState(installState);
    }

    private void sendUserToCloud() {
        KeyService keyService = PassShelterApp.getInstance().getKeyService();
        UserRep userRep = PassShelterApp.createUserRep(this.context, keyService.getSymmetricEncryption());
        try {
            NetworkUtil.verifyNetwork(this.context);
            ProgressDialog dialog = ProgressDialogUtil.createProgressDialog(this.context, "Aguarde enquanto o usuário é salvo");
            String email = userRep.getUser();
            CertificateBag cert = keyService.getCertificateBag();
            ParseData parseData = new ParseData();
            parseData.saveUser(email, cert);
            finished(InstallState.USER_CLOUD);
            dialog.dismiss();
        } catch (Exception e) {
            persistState(InstallState.CERTIFICATE_INSTALLED);
            finished(InstallState.CERTIFICATE_INSTALLED);
            Toast.makeText(this.context, "Erro ao enviar dados para a nuvem", Toast.LENGTH_LONG).show();
            Log.e(this.context.getResources().getString(R.string.app_name), "Exceção ao enviar dados para a nuvem", e);
        }
    }

    private void startActivityFromState(InstallState installState) {
        switch (installState) {
            case INITIAL:
                startActivity(DigCertActivity.class);
                break;
            case CERTIFICATE_INSTALLED:
                startActivity(DefUserActivity.class);
                break;
            case USER_DEFINED:
                sendUserToCloud();
                break;
            case USER_CLOUD: case READY:
                Intent intent = new Intent(this.context, LoginActivity.class);
                if (installState == InstallState.USER_CLOUD) {
                    intent.putExtra("init", false);
                    persistState(InstallState.USER_CLOUD);
                } else {
                    intent.putExtra("init", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                break;
        }
    }

    @Override
    public void finished(InstallState state) {
        startActivityFromState(state);
    }

    @Override
    public void persistState(InstallState state) {
        // The context present in the class is not prepared to write in SharedPreferences
        SharedPrefUtil.writeIntTo(context, SharedPrefUtil.KEYCHAIN_PREF, SharedPrefUtil.KEYCHAIN_PREF_STATE, state.ordinal());
    }

    public void cancel() {
        System.exit(0);
    }
}
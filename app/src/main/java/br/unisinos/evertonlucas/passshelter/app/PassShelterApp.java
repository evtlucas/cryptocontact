package br.unisinos.evertonlucas.passshelter.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.io.Serializable;

import br.unisinos.evertonlucas.passshelter.async.UpdateCertificateStatus;
import br.unisinos.evertonlucas.passshelter.bizserv.InstallService;
import br.unisinos.evertonlucas.passshelter.bizserv.KeyService;
import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.rep.UserRep;

/**
 * Class designed for control the application start process
 * Created by everton on 05/09/15.
 */
public class PassShelterApp extends Application implements Serializable {

    private InstallService service = null;
    private static PassShelterApp singleton;
    private KeyService keyService = null;
    private UserRep userRepository = null;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        service = new InstallService(this.getApplicationContext());
        service.initialize();
    }

    public static PassShelterApp getInstance() {
        return singleton;
    }

    public InstallService getInstallService() {
        return service;
    }

    public KeyService getKeyService() {
        return keyService;
    }

    public static KeyService createKeyService(UpdateCertificateStatus status, Activity activity) {
        if (getInstance().keyService == null) {
            getInstance().keyService = new KeyService(status, activity);
        } else {
            getInstance().keyService.setUpdateCertificateStatus(status);
            getInstance().keyService.setActivity(activity);
        }
        return getInstance().keyService;
    }

    public static UserRep createUserRep(Context context, SymmetricEncryption symmetricEncryption) {
        if (getInstance().userRepository == null) {
            getInstance().userRepository = new UserRep(context, symmetricEncryption);
        } else {
            getInstance().userRepository.setContext(context);
        }
        return getInstance().userRepository;
    }
}

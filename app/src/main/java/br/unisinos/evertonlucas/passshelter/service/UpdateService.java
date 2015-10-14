package br.unisinos.evertonlucas.passshelter.service;

import android.app.Activity;
import android.content.Context;

import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.util.SharedPrefUtil;

/**
 * Class responsible for manage update
 * Created by everton on 09/10/15.
 */
public class UpdateService implements UpdateStatus {
    
    private Context context;
    private int versionCode;
    private int currentVersion;
    private UpdateStatus updateStatus;

    public UpdateService(Context context, int versionCode, boolean firstInstallation) {
        this.context = context;
        this.versionCode = versionCode;
        if (firstInstallation)
            writeCurrentVersion(this.versionCode);
        this.currentVersion = SharedPrefUtil.readIntFrom(this.context, SharedPrefUtil.RESOURCE, SharedPrefUtil.VERSION_CODE);
    }

    public boolean isUpdateNeeded() {
        return this.currentVersion < this.versionCode;
    }

    public void update(UpdateStatus updateStatus, Activity activity) {
        this.updateStatus = updateStatus;
        updateToVersion4();
    }

    private void writeCurrentVersion(int versionCode) {
        SharedPrefUtil.writeIntTo(this.context, SharedPrefUtil.RESOURCE, SharedPrefUtil.VERSION_CODE, versionCode);
    }

    public void writeCurrentVersion() {
        writeCurrentVersion(this.versionCode);
    }

    private void updateToVersion4() {
        writeCurrentVersion();
        /*try {
            KeyService service = PassShelterApp.getInstance().getKeyService();
            LocalUserRep rep = new LocalUserRep(this.context, service.getSymmetricEncryption());
            UpdateVersion4 updateVersion4 = new UpdateVersion4(this.context, this, rep.getUser());
            updateVersion4.execute();
        } catch (Exception e) {
            update(false);
        }*/
    }

    @Override
    public void update(boolean status) {
        if (status) {
            writeCurrentVersion(this.versionCode);
        }
        if (updateStatus != null) updateStatus.update(status);
    }

    public boolean isReinstallNeeded() {
        return this.currentVersion < 4;
    }
}

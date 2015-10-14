package br.unisinos.evertonlucas.passshelter.updates;

import android.content.Context;
import android.os.AsyncTask;

import java.security.KeyStore;

import br.unisinos.evertonlucas.passshelter.app.PassShelterApp;
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.data.KeyStoreData;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.util.KeyChainUtil;
import br.unisinos.evertonlucas.passshelter.util.ShowLogExceptionUtil;

/**
 * Class responsible for update Pass Shelter to version 4
 * Created by everton on 09/10/15.
 */
public class UpdateVersion4  extends AsyncTask<Void, Void, Boolean> {
    private final Context context;
    private final UpdateStatus updateStatus;
    private final String user;
    private final KeyService service;

    public UpdateVersion4(Context context, UpdateStatus updateStatus, String user) {
        this.context = context;
        this.updateStatus = updateStatus;
        this.user = user;
        service = PassShelterApp.getInstance().getKeyService();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            String alias = this.service.getAlias();
            if (alias.isEmpty())
                alias = "ID COMODO CA Limited da/do";
            CertificateBag bag = new KeyChainUtil().getCertificateBag(this.context, alias);
            if (!bag.isValid())
                throw new RuntimeException("Certificado inválido");
            KeyStore store = KeyStore.getInstance("AndroidKeyStore");
            store.load(null);
            store.setCertificateEntry(KeyStoreData.ALIAS, bag.getCert());
            return true;
        } catch (Exception e){
            ShowLogExceptionUtil.logException(this.context, "Erro ao atualizar certificado", e);
            KeyStoreData data = new KeyStoreData(this.context);
            try {
                data.generate(user);
            } catch (Exception e1) {
                return false;
            }
            return true;
        }
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        updateStatus.update(status);
    }
}

package br.unisinos.evertonlucas.passshelter.async;

import android.content.Context;
import android.os.AsyncTask;

import br.unisinos.evertonlucas.passshelter.data.KeyStoreData;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.util.ShowLogExceptionUtil;

/**
 * Created by everton on 11/10/15.
 */
public class GenerateKeyPairAsyncTask extends AsyncTask<Void, Void, CertificateBag> {

    private final UpdateCertificate updateCertificate;
    private final String email;
    private final Context ctx;
    private final KeyStoreData keyStoreData;

    public GenerateKeyPairAsyncTask(Context ctx, UpdateCertificate update, String email) {
        this.ctx = ctx;
        updateCertificate = update;
        this.email = email;
        this.keyStoreData = new KeyStoreData(this.ctx);
    }

    @Override
    protected CertificateBag doInBackground(Void... params) {
        try {
            this.keyStoreData.generate(this.email);
            return this.keyStoreData.get();
        } catch (Exception e) {
            ShowLogExceptionUtil.logException(this.ctx, "Erro ao gerar par público/privado", e);
            return new CertificateBag();
        }
    }

    @Override
    protected void onPostExecute(CertificateBag certificateBag) {
        updateCertificate.updateCertificateInfo(certificateBag);
    }
}

/*
Copyright 2015 Everton Luiz de Resende Lucas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
/*
Copyright 2015 Everton Luiz de Resende Lucas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package br.unisinos.evertonlucas.passshelter.async;

import android.content.Context;
import android.os.AsyncTask;

import br.unisinos.evertonlucas.passshelter.data.KeyStoreData;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.util.ShowLogExceptionUtil;

/**
 * Class responsible for reading certificate process by AsyncTask, because getCertificateChain will block UI Thread
 * Created by everton on 24/07/15.
 */
public class CertificateReadAsyncTask extends AsyncTask<Void, Void, CertificateBag> {

    private final UpdateCertificate updateCertificate;
    private final Context ctx;

    public CertificateReadAsyncTask(Context ctx, UpdateCertificate updateCertificate) {
        this.ctx = ctx;
        this.updateCertificate = updateCertificate;
    }

    @Override
    protected CertificateBag doInBackground(Void... params) {
        try {
            return new KeyStoreData(this.ctx).get();
        } catch (Exception e) {
            ShowLogExceptionUtil.logException(this.ctx, "Error reading certificate bag", e);
            return new CertificateBag();
        }
    }

    @Override
    protected void onPostExecute(CertificateBag certificateBag) {
        updateCertificate.updateCertificateInfo(certificateBag);
    }
}

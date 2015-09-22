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
import android.security.KeyChain;
import android.util.Log;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import br.unisinos.evertonlucas.passshelter.model.CertificateBag;

/**
 * Class responsible for read a certificate chain based on an alias.
 * These class should be implemented as AsyncTask because getCertificateChain will block UI Thread
 * Created by everton on 24/07/15.
 */
public class CertificateReadAsyncTask extends AsyncTask<Void, Void, CertificateBag> {

    private final UpdateCertificate updateCertificate;
    private final String alias;
    private final Context ctx;

    public CertificateReadAsyncTask(Context ctx, UpdateCertificate updateCertificate, String alias) {
        this.ctx = ctx;
        this.updateCertificate = updateCertificate;
        this.alias = alias;
    }

    @Override
    protected CertificateBag doInBackground(Void... params) {
        CertificateBag bag;
        try {
            X509Certificate[] chain = KeyChain.getCertificateChain(ctx, alias);
            PrivateKey privateKey = KeyChain.getPrivateKey(ctx, alias);
            if ((chain == null) || (privateKey == null))
                return new CertificateBag(null, null);
            bag = new CertificateBag(chain[0], privateKey);
        } catch (Exception e) {
            Log.e("CryptoContact", "Error reading certificate bag: " + e.getMessage(), e);
            return new CertificateBag(null, null);
        }
        return bag;
    }

    @Override
    protected void onPostExecute(CertificateBag certificateBag) {
        updateCertificate.updateCertificateInfo(certificateBag);
    }
}

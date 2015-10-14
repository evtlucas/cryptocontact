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

package br.unisinos.evertonlucas.passshelter.encryption;

import android.app.Activity;
import android.security.KeyChainAliasCallback;

import java.util.concurrent.ExecutionException;

import br.unisinos.evertonlucas.passshelter.async.CertificateReadAsyncTask;
import br.unisinos.evertonlucas.passshelter.async.GenerateKeyPairAsyncTask;
import br.unisinos.evertonlucas.passshelter.async.UpdateCertificate;
import br.unisinos.evertonlucas.passshelter.util.KeyChainUtil;

/**
 * Class responsible for communicate with KeyChain and KeyStore
 * Created by everton on 20/07/15.
 */
public class KeyStore {

    private final Activity activity;

    public KeyStore(Activity activity) {
        this.activity = activity;
    }

    public void readCertificate(UpdateCertificate update)
            throws ExecutionException, InterruptedException {
        CertificateReadAsyncTask asyncTask = new CertificateReadAsyncTask(activity, update);
        asyncTask.execute();
    }

    public void choosePrivateKeyAlias(KeyChainAliasCallback callback) {
        new KeyChainUtil().choosePrivateKeyAlias(this.activity, callback);;
    }

    public void generateInstallCertificate(UpdateCertificate update, String email) {
        GenerateKeyPairAsyncTask genKeyPair = new GenerateKeyPairAsyncTask(activity, update, email);
        genKeyPair.execute();
    }
}

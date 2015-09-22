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

package br.unisinos.evertonlucas.passshelter.util;

import android.app.Activity;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;

import java.util.concurrent.ExecutionException;

import br.unisinos.evertonlucas.passshelter.async.CertificateReadAsyncTask;
import br.unisinos.evertonlucas.passshelter.async.UpdateCertificate;

/**
 * Class responsible for communicate with KeyChain
 * Created by everton on 20/07/15.
 */
public class KeyStoreUtil {

    private final Activity activity;

    public KeyStoreUtil(Activity activity) {
        this.activity = activity;
    }

    public void readCertificate(UpdateCertificate update, String alias)
            throws ExecutionException, InterruptedException {
        CertificateReadAsyncTask asyncTask = new CertificateReadAsyncTask(activity, update, alias);
        asyncTask.execute();
    }

    public void choosePrivateKeyAlias(KeyChainAliasCallback callback) {
        KeyChain.choosePrivateKeyAlias(this.activity, callback, new String[]{"RSA"}, null, null, -1, null);
    }
}

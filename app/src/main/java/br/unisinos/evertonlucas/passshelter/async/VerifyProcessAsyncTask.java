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
import android.util.Log;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;
import br.unisinos.evertonlucas.passshelter.rep.UserRep;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.service.VerifyResourceService;

/**
 * Class designed for create thread for verify if existent resource to update/insert
 * Created by everton on 20/09/15.
 */
public class VerifyProcessAsyncTask  extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private KeyService service;
    private ResourceRep resourceRep;
    private UpdateStatus updateStatus;
    private ParseData parseData;
    private UserRep userRep;

    public VerifyProcessAsyncTask(Context context, KeyService service, ResourceRep resourceRep,
                                  UpdateStatus updateStatus, ParseData parseData, UserRep userRep) {
        this.context = context;
        this.service = service;
        this.resourceRep = resourceRep;
        this.updateStatus = updateStatus;
        this.parseData = parseData;
        this.userRep = userRep;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        VerifyResourceService verifyResourceService = new VerifyResourceService(context, service, resourceRep, parseData, userRep);
        try {
            verifyResourceService.verifyResource();
        } catch (Exception e) {
            Log.e(context.getResources().getString(R.string.app_name), "Exceção ao enviar recurso", e);
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        this.updateStatus.update(status);
        super.onPostExecute(status);
    }
}

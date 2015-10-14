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

import com.parse.ParseException;

import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.exception.NotFoundException;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.service.SendResourceService;
import br.unisinos.evertonlucas.passshelter.util.ShowLogExceptionUtil;

/**
 * Class designed for resource send process
 * Created by everton on 19/09/15.
 */
public class SendProcessAsyncTask extends AsyncTask<String, Void, Boolean> {

    private Context context;
    private KeyService service;
    private ResourceRep resourceRep;
    private UpdateStatus updateStatus;
    private ParseData parseData;
    private LocalUserRep localUserRep;

    public SendProcessAsyncTask(Context context, KeyService service, ResourceRep resourceRep,
                                UpdateStatus updateStatus, ParseData parseData, LocalUserRep localUserRep) {
        this.context = context;
        this.service = service;
        this.resourceRep = resourceRep;
        this.updateStatus = updateStatus;
        this.parseData = parseData;
        this.localUserRep = localUserRep;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String email = params[0];
        String resourceName = params[1];
        SendResourceService sendResourceService = new SendResourceService(context, service, resourceRep, parseData, localUserRep);
        try {
            sendResourceService.sendResource(email, resourceName);
        } catch (NotFoundException e) {
            ShowLogExceptionUtil.logException(this.context, "Usuário não encontrado", e);
            return false;
        } catch (ParseException e) {
            ShowLogExceptionUtil.logException(this.context, "Exceção da Cloud Parse.com", e);
            return false;
        } catch (Exception e) {
            ShowLogExceptionUtil.logException(this.context, "Exceção ao enviar recurso", e);
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

package br.unisinos.evertonlucas.passshelter.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.model.Group;
import br.unisinos.evertonlucas.passshelter.model.User;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.service.SendResourceService;

/**
 * AsyncTask for send resources to a group
 * Created by everton on 27/09/15.
 */
public class SendProcessGroupAsyncTask  extends AsyncTask<String, Void, Boolean> {
    private Context context;
    private KeyService service;
    private ResourceRep resourceRep;
    private UpdateStatus updateStatus;
    private ParseData parseData;
    private LocalUserRep localUserRep;
    private Group group;

    public SendProcessGroupAsyncTask(Context context, KeyService service, ResourceRep resourceRep,
                                     UpdateStatus updateStatus, ParseData parseData, LocalUserRep localUserRep, Group group) {
        this.context = context;
        this.service = service;
        this.resourceRep = resourceRep;
        this.updateStatus = updateStatus;
        this.parseData = parseData;
        this.localUserRep = localUserRep;
        this.group = group;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String resourceName = params[0];
        SendResourceService sendResourceService = new SendResourceService(context, service, resourceRep, parseData, localUserRep);
        try {
            for(User user : group.getUsers())
                sendResourceService.sendResource(user.getEmail(), resourceName);
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

package br.unisinos.evertonlucas.passshelter.data;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.unisinos.evertonlucas.passshelter.app.FinishedFind;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.model.Resource;

/**
 * Class created for parse.com integration
 * Created by everton on 09/09/15.
 */
public class ParseData {

    public void saveUser(String email, CertificateBag cert) {
        ParseObject user = new ParseObject("User");
        user.put("email", email);
        byte[] encoded = cert.getCert().getPublicKey().getEncoded();
        user.put("public_key", encoded);
        user.saveInBackground();
    }

    public void getEMailUsers(final String email, final FinishedFind find) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereStartsWith("email", email);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {
                if (users != null) {
                    final List<String> emailList = new ArrayList<>();
                    for (ParseObject user : users) {
                        emailList.add(user.getString("email"));
                    }
                    find.notifyFinished(emailList);
                } else {
                    Log.e("passshelter", "Error: " + e.getMessage(), e);
                    find.notifyError();
                }
            }
        });
    }
}

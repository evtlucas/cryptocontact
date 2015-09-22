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

package br.unisinos.evertonlucas.passshelter.data;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import br.unisinos.evertonlucas.passshelter.app.FinishedFind;
import br.unisinos.evertonlucas.passshelter.encryption.PrivateAssymetricCryptography;
import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.model.ExternalResource;
import br.unisinos.evertonlucas.passshelter.model.ExternalUser;
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

    public ExternalUser getExternalUser(String email) throws ParseException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("email", email);
        List<ParseObject> objects = query.find();
        if (objects.size() == 0)
            return new ExternalUser(null, null);
        else {
            ParseObject user = objects.get(0);
            PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(user.getBytes("public_key")));
            return new ExternalUser(user.getString("email"), key);
        }
    }

    public void sendExternalResource(ExternalResource externalResource) throws IllegalBlockSizeException,
            NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        ParseObject object = new ParseObject("ExternalResource");
        object.put("from", externalResource.getFrom());
        object.put("to", externalResource.getTo());
        object.put("crypted_session_key", externalResource.getCryptedSessionKey());
        object.put("name", externalResource.getName());
        object.put("user", externalResource.getUser());
        object.put("password", externalResource.getPassword());
        object.saveInBackground();
    }

    public List<Resource> getExternalResources(String localUser,
                                               PrivateAssymetricCryptography cryptography,
                                               SymmetricEncryption symmetricEncryption)
            throws ParseException, BadPaddingException, IllegalBlockSizeException,
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            UnsupportedEncodingException {
        List<Resource> resourceList = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ExternalResource");
        query.whereEqualTo("to", localUser);
        List<ParseObject> list = query.find();
        for(ParseObject object : list) {
            byte[] sessionKey = cryptography.decrypt(object.getBytes("crypted_session_key"));
            SecretKey key = new SecretKeySpec(sessionKey, "AES");
            SymmetricEncryption sessionDecrypt = new SymmetricEncryption(key);
            Resource resource = new Resource(symmetricEncryption);
            resource.setName(object.getString("name"));
            resource.setUser(new String(sessionDecrypt.decrypt(object.getBytes("user")), "UTF-8"));
            resource.setPassword(new String(sessionDecrypt.decrypt(object.getBytes("password")), "UTF-8"));
            resourceList.add(resource);
        }
        return resourceList;
    }

    public void deleteResources(String email) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ExternalResource");
        query.whereEqualTo("to", email);
        List<ParseObject> list = query.find();
        for(ParseObject object : list)
            object.deleteInBackground();
    }
}

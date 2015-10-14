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

package br.unisinos.evertonlucas.passshelter.service;

import android.content.Context;

import com.parse.ParseException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.encryption.PublicAssymetricCryptography;
import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.exception.NotFoundException;
import br.unisinos.evertonlucas.passshelter.model.ExternalResource;
import br.unisinos.evertonlucas.passshelter.model.ParseUser;
import br.unisinos.evertonlucas.passshelter.model.Resource;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;
import br.unisinos.evertonlucas.passshelter.util.KeyGenerationUtil;

/**
 * Class responsible for send a resource to parse.com
 * Created by everton on 19/09/15.
 */

public class SendResourceService {
    private Context context;
    private KeyService service;
    private ResourceRep resourceRep;
    private ParseData parseData;
    private LocalUserRep localUserRep;

    public SendResourceService(Context context, KeyService service,
                               ResourceRep resourceRep, ParseData parseData, LocalUserRep localUserRep) {
        this.context = context;
        this.service = service;
        this.resourceRep = resourceRep;
        this.parseData = parseData;
        this.localUserRep = localUserRep;
    }

    public void sendResource(String email, String resourceName) throws NoSuchAlgorithmException,
            InvalidKeySpecException, ParseException, NoSuchPaddingException, InvalidKeyException,
            NoSuchProviderException, BadPaddingException, IllegalBlockSizeException {
        SecretKey sessionKey = KeyGenerationUtil.generate();
        SymmetricEncryption encryption = new SymmetricEncryption(sessionKey);
        byte[] cryptedSessionKey = createCryptedSessionKey(email, sessionKey);
        Resource sendResource = createSendResource(resourceName, encryption);
        ExternalResource externalResource = new ExternalResource(localUserRep.getUser(), email, cryptedSessionKey, sendResource);
        parseData.sendExternalResource(externalResource);
    }

    private byte[] createCryptedSessionKey(String email, SecretKey sessionKey) throws ParseException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        ParseUser parseUser = parseData.getExternalUser(email);
        if (!parseUser.isValid())
            throw new NotFoundException();
        PublicAssymetricCryptography cryptography = new PublicAssymetricCryptography(parseUser.getPublicKey());
        return cryptography.encrypt(sessionKey.getEncoded());
    }

    private Resource createSendResource(String resourceName, SymmetricEncryption encryption) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        Resource sendResource = new Resource(encryption);
        Resource resource = resourceRep.getResourceByName(resourceName);
        sendResource.setName(resource.getName());
        sendResource.setUser(resource.getUser());
        sendResource.setPassword(resource.getPassword());
        return sendResource;
    }
}

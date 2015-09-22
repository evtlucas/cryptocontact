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

package br.unisinos.evertonlucas.passshelter.model;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Class designed for represent a resource which should be send to cloud
 * Created by everton on 20/09/15.
 */
public class ExternalResource {
    private final String from;
    private final String to;
    private final byte[] cryptedSessionKey;
    private final Resource sendResource;

    public ExternalResource(String from, String to, byte[] cryptedSessionKey, Resource sendResource) {
        this.from = from;
        this.to = to;
        this.cryptedSessionKey = cryptedSessionKey;
        this.sendResource = sendResource;
    }

    public byte[] getCryptedSessionKey() {
        return cryptedSessionKey;
    }

    public String getName() {
        return sendResource.getName();
    }

    public byte[] getUser() throws InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        return sendResource.getCryptoUser();
    }

    public byte[] getPassword() throws InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        return sendResource.getCryptoPassword();
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}

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

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;

/**
 * Class Resource
 * Created by everton on 30/08/15.
 */
public class Resource implements Serializable {
    private Long id;
    private String name;
    private String user;
    private String password;
    private SymmetricEncryption encryption;
    private byte[] cryptoPassword;

    public Resource(SymmetricEncryption encryption) {
        this.encryption = encryption;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getCryptoUser() throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return this.encryption.encrypt(this.user.getBytes());
    }

    public byte[] getCryptoPassword() throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return this.encryption.encrypt(this.password.getBytes());
    }

    public void setCryptoUser(byte[] cryptoUser) throws IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
            NoSuchPaddingException {
        this.user = new String(this.encryption.decrypt(cryptoUser));
    }

    public void setCryptoPassword(byte[] cryptoPassword) throws IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
            NoSuchPaddingException {
        this.password = new String(this.encryption.decrypt(cryptoPassword));
    }
}

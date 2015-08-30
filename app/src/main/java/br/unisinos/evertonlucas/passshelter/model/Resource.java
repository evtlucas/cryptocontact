package br.unisinos.evertonlucas.passshelter.model;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;

/**
 * Created by everton on 30/08/15.
 */
public class Resource {
    private String name;
    private String user;
    private String password;
    private SymmetricEncryption encryption;
    private byte[] cryptoPassword;

    public Resource(SymmetricEncryption encryption) {
        this.encryption = encryption;
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
}

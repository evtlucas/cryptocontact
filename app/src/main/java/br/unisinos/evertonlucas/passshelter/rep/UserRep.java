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

package br.unisinos.evertonlucas.passshelter.rep;

import android.content.Context;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.util.SharedPrefUtil;

/**
 * Class responsible for persist user data
 * Created by everton on 06/09/15.
 */
public class UserRep {
    private Context context;
    private SymmetricEncryption encryption;

    public UserRep(Context context, SymmetricEncryption encryption) {
        this.context = context;
        this.encryption = encryption;
    }

    public void saveUser(String user) throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] encryptedUser = this.encryption.encrypt(user.getBytes());
        SharedPrefUtil.writeByteTo(this.context, SharedPrefUtil.KEYCHAIN_PREF,
                SharedPrefUtil.KEYCHAIN_PREF_USER, encryptedUser);
    }

    public String getUser() throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] encryptedUser = SharedPrefUtil.readByteFrom(this.context, SharedPrefUtil.KEYCHAIN_PREF,
                SharedPrefUtil.KEYCHAIN_PREF_USER);
        byte[] arrUser = this.encryption.decrypt(encryptedUser);
        return new String(arrUser);
    }

    public void savePassword(String pwd) throws NoSuchAlgorithmException {
        byte[] encryptedPwd = getEncryptedPwd(pwd);
        SharedPrefUtil.writeByteTo(this.context, SharedPrefUtil.KEYCHAIN_PREF,
                SharedPrefUtil.KEYCHAIN_PREF_PWD, encryptedPwd);
    }

    private byte[] getEncryptedPwd(String pwd) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(pwd.getBytes());
    }

    public boolean validatePassword(String pwd) throws NoSuchAlgorithmException {
        byte[] encryptedPwd = getEncryptedPwd(pwd);
        byte[] storedPwd = SharedPrefUtil.readByteFrom(this.context, SharedPrefUtil.KEYCHAIN_PREF,
                SharedPrefUtil.KEYCHAIN_PREF_PWD);
        return Arrays.equals(encryptedPwd, storedPwd);
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

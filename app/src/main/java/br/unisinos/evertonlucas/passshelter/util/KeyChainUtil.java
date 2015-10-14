package br.unisinos.evertonlucas.passshelter.util;

import android.app.Activity;
import android.content.Context;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.security.KeyChainException;
import android.support.annotation.NonNull;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

import br.unisinos.evertonlucas.passshelter.model.CertificateBag;

/**
 * Class responsible for reading Certificate from KeyChain
 * Created by everton on 11/10/15.
 */
public class KeyChainUtil {

    @NonNull
    public CertificateBag getCertificateBag(Context ctx, String alias) throws KeyChainException, InterruptedException {
        CertificateBag bag;
        X509Certificate[] chain = KeyChain.getCertificateChain(ctx, alias);
        PrivateKey privateKey = KeyChain.getPrivateKey(ctx, alias);
        if ((chain == null) || (privateKey == null))
            bag = new CertificateBag();
        else
            bag = new CertificateBag(chain[0], (RSAPrivateKey) privateKey);
        return bag;
    }

    public void choosePrivateKeyAlias(Activity activity, KeyChainAliasCallback callback) {
        KeyChain.choosePrivateKeyAlias(activity, callback, new String[]{"RSA"}, null, null, -1, null);
    }
}

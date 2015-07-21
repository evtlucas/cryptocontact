package br.unisinos.evertonlucas.cryptocontact.service;

import android.util.Log;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by everton on 20/07/15.
 */
public class KeyStoreService {

    private KeyStore keyStore;

    public KeyStoreService() {
        try {
            keyStore = KeyStore.getInstance("AndroidCAStore");
            keyStore.load(null);
        } catch (Exception e) {
            Log.d("CryptoContact", "Erro ao obter KeyStore: " + e.getStackTrace());
        }
    }

    public List<String> getChaves() throws KeyStoreException {
        List<String> keyAliases = new ArrayList<String>();
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = (String) aliases.nextElement();
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
            if (cert.getIssuerDN().getName().contains("user"))
                keyAliases.add(cert.getIssuerDN().getName());
        }
        return keyAliases;
    }
}

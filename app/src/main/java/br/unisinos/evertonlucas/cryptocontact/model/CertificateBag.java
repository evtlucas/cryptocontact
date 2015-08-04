package br.unisinos.evertonlucas.cryptocontact.model;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Created by everton on 02/08/15.
 */
public class CertificateBag {

    private X509Certificate cert;
    private PrivateKey privateKey;

    public CertificateBag(X509Certificate cert, PrivateKey privateKey) {
        this.cert = cert;
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public X509Certificate getCert() {
        return cert;
    }
}

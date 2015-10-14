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

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Class designed for join X509 and Private Key
 * Created by everton on 02/08/15.
 */
public class CertificateBag {

    private X509Certificate cert;
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public CertificateBag(X509Certificate cert, RSAPrivateKey privateKey) {
        this.cert = cert;
        this.privateKey = privateKey;
        this.publicKey = null;
    }

    public CertificateBag(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.cert = null;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public CertificateBag() {
        this.cert = null;
        this.privateKey = null;
        this.publicKey = null;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        if (cert != null)
            return (RSAPublicKey) cert.getPublicKey();
        return this.publicKey;
    }

    public X509Certificate getCert() {
        return cert;
    }

    public boolean isValid() {
        return ((cert != null) || (publicKey != null)) && privateKey != null;
    }
}

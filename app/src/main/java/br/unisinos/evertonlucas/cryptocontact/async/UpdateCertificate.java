package br.unisinos.evertonlucas.cryptocontact.async;

import java.security.cert.X509Certificate;

/**
 * Created by everton on 26/07/15.
 */
public interface UpdateCertificate {
    public void updateCertificateInfo(X509Certificate certificate);
}

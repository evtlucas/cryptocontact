package br.unisinos.evertonlucas.cryptocontact.util;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;

import org.mockito.Mockito;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.security.auth.x500.X500Principal;

import br.unisinos.evertonlucas.cryptocontact.model.CertificateBag;

/**
 * Created by everton on 07/08/15.
 */
public class TestKeyGenerationUtil {
    public static CertificateBag generate(Context ctx) throws NoSuchAlgorithmException, CertificateException {
        KeyPairGeneratorSpec spec = getKeyPairGeneratorSpec(ctx);
        KeyPair pair = getKeyPair();
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        when(cert.getSerialNumber()).thenReturn(spec.getSerialNumber());
        when(cert.getSubjectDN()).thenReturn(spec.getSubjectDN());
        when(cert.getPublicKey()).thenReturn(pair.getPublic());
        CertificateBag bag = new CertificateBag(cert, pair.getPrivate());
        return bag;
    }

    private static KeyPair getKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(256);
        return generator.generateKeyPair();
    }

    private static KeyPairGeneratorSpec getKeyPairGeneratorSpec(Context ctx) throws NoSuchAlgorithmException {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 1);
        return new KeyPairGeneratorSpec.Builder(ctx)
                .setAlias("myKey")
                .setSubject(new X500Principal("CN=myKey"))
                    .setSerialNumber(BigInteger.valueOf(1337))
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                .setKeyType("RSA")
                .build();
    }
}

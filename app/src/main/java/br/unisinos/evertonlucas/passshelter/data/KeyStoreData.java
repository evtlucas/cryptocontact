package br.unisinos.evertonlucas.passshelter.data;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

import br.unisinos.evertonlucas.passshelter.model.CertificateBag;

/**
 * Class responsible for persistence in KeyStore
 * Created by everton on 11/10/15.
 */
public class KeyStoreData {

    private Context context;
    public static final String ALIAS = "pass_shelter_key";

    public KeyStoreData(Context context) {
        this.context = context;
    }

    public void generate(String email) throws NoSuchProviderException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {
        Calendar notBefore = Calendar.getInstance();
        Calendar notAfter = Calendar.getInstance();
        notAfter.add(Calendar.YEAR, 2);
        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(this.context)
                .setAlias(ALIAS)
                .setSubject(
                        new X500Principal(String.format("CN=%s, OU=%s", email,
                                this.context.getPackageName())))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(notBefore.getTime())
                .setEndDate(notAfter.getTime())
                .build();
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
        generator.initialize(spec);
        generator.generateKeyPair();
    }

    public CertificateBag get() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(ALIAS, null);
        RSAPublicKey publicKey = (RSAPublicKey) keyEntry.getCertificate().getPublicKey();
        RSAPrivateKey privKey = (RSAPrivateKey) keyEntry.getPrivateKey();
        CertificateBag bag = new CertificateBag(publicKey, privKey);
        return bag;
    }
}

package br.unisinos.evertonlucas.passshelter.data;

import com.parse.ParseObject;

import br.unisinos.evertonlucas.passshelter.model.CertificateBag;

/**
 * Class created for parse.com integration
 * Created by everton on 09/09/15.
 */
public class ParseData {

    public void saveUser(String email, CertificateBag cert) {
        ParseObject user = new ParseObject("User");
        user.add("email", email);
        byte[] encoded = cert.getCert().getPublicKey().getEncoded();
        user.add("public_key", encoded);
        user.saveInBackground();
    }
}

package br.unisinos.evertonlucas.passshelter.bizserv;

import android.content.Context;
import android.content.Intent;

import java.security.NoSuchAlgorithmException;

import br.unisinos.evertonlucas.passshelter.app.InstallState;
import br.unisinos.evertonlucas.passshelter.app.Main;
import br.unisinos.evertonlucas.passshelter.rep.UserRep;

/**
 * Created by everton on 10/09/15.
 */
public class LoginService {
    private Context context;
    private UserRep userRep;
    private InstallService installService;

    public LoginService(Context context, UserRep userRep, InstallService installService) {
        this.context = context;
        this.userRep = userRep;
        this.installService = installService;
    }

    public boolean login(String password) throws NoSuchAlgorithmException {
        boolean validatePassword = userRep.validatePassword(password);
        if (validatePassword) {
            this.installService.persistState(InstallState.READY);
            this.context.startActivity(new Intent(this.context, Main.class));
        }
        return validatePassword;
    }
}

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

package br.unisinos.evertonlucas.passshelter.service;

import android.content.Context;
import android.content.Intent;

import java.security.NoSuchAlgorithmException;

import br.unisinos.evertonlucas.passshelter.app.InstallState;
import br.unisinos.evertonlucas.passshelter.app.Main;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;

/**
 * Class designed for the login process
 * Created by everton on 10/09/15.
 */
public class LoginService {
    private Context context;
    private LocalUserRep localUserRep;
    private InstallService installService;

    public LoginService(Context context, LocalUserRep localUserRep, InstallService installService) {
        this.context = context;
        this.localUserRep = localUserRep;
        this.installService = installService;
    }

    public boolean login(String password) throws NoSuchAlgorithmException {
        boolean validatePassword = localUserRep.validatePassword(password);
        if (validatePassword) {
            this.installService.persistState(InstallState.READY);
            this.context.startActivity(new Intent(this.context, Main.class));
        }
        return validatePassword;
    }
}

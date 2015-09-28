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

import java.util.List;

import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.encryption.PrivateAssymetricCryptography;
import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.model.Resource;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;

/**
 * Class responsible for verify existent resources on cloud
 * Created by everton on 20/09/15.
 */
public class VerifyResourceService {
    private Context context;
    private KeyService keyService;
    private ResourceRep resourceRep;
    private ParseData parseData;
    private LocalUserRep localUserRep;

    public VerifyResourceService(Context context, KeyService service, ResourceRep resourceRep,
                                 ParseData parseData, LocalUserRep localUserRep) {
        this.context = context;
        this.keyService = service;
        this.resourceRep = resourceRep;
        this.parseData = parseData;
        this.localUserRep = localUserRep;
    }

    public void verifyResource() throws Exception {
        SymmetricEncryption symmetricEncryption = keyService.getSymmetricEncryption();
        String localUser = this.localUserRep.getUser();
        CertificateBag bag = this.keyService.getCertificateBag();
        PrivateAssymetricCryptography cryptography = new PrivateAssymetricCryptography(bag.getPrivateKey());
        List<Resource> resources = parseData.getExternalResources(localUser, cryptography, symmetricEncryption);
        for (Resource resource : resources) {
            Resource resQuery = this.resourceRep.getResourceByName(resource.getName());
            if(resQuery != null) {
                resQuery.setName(resource.getName());
                resQuery.setUser(resource.getUser());
                resQuery.setPassword(resource.getPassword());
                this.resourceRep.updateResource(resQuery);
            } else {
                this.resourceRep.insertResource(resource);
            }
        }
        parseData.deleteResources(localUser);
    }
}

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

import java.security.PublicKey;

/**
 * Class responsible for represent a resource which comes from Parse.com
 * Created by everton on 20/09/15.
 */
public class ParseUser {

    private final String id;
    private final String email;
    private final PublicKey publicKey;

    public ParseUser(String id, String email, PublicKey publicKey) {
        this.id = id;
        this.email = email;
        this.publicKey = publicKey;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public boolean isValid() {
        return (this.id != null) && (this.email != null) && (this.publicKey != null);
    }
}

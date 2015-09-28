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

package br.unisinos.evertonlucas.passshelter.util;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Class responsible for encapsulate generation key objects
 * Created by everton on 27/09/15.
 */
public class KeyFactory {
    public static PublicKey generatePublicKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return java.security.KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
    }
}

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

package br.unisinos.evertonlucas.passshelter;

import junit.framework.TestCase;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.SecretKey;

import br.unisinos.evertonlucas.passshelter.util.KeyGenerationUtil;


/**
 * Class designed for test KyeGenerationUtil
 * Created by everton on 03/08/15.
 */
public class KeyGenerationTest extends TestCase {

    public void testKeyGeneration() throws NoSuchAlgorithmException {
        SecretKey key = KeyGenerationUtil.generate();
        byte[] key1 = key.getEncoded();
        key = KeyGenerationUtil.generate();
        byte[] key2 = key.getEncoded();
        assertFalse(Arrays.equals(key1, key2));
    }

    public void testGenerateEqualSecretKeys() throws NoSuchAlgorithmException {
        SecretKey key = KeyGenerationUtil.generate();
        SecretKey key2 = KeyGenerationUtil.getSecretKey(key.getEncoded());
        final byte[] keyTest = key.getEncoded();
        assertTrue(Arrays.equals(key.getEncoded(), key2.getEncoded()));
    }

}
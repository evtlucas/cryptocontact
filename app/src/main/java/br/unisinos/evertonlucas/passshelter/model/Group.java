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

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by everton on 26/09/15.
 */
public class Group {
    private String name;
    private String admin;
    private Long id;
    private List<User> lstUsers = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getAdmin() {
        return admin;
    }

    public void addUser(User user) {
        lstUsers.add(user);
    }

    public void removeUser(User user) {
        lstUsers.remove(user);
    }

    public List<User> getUsers() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        List<User> users = new ArrayList<>();
        for(User user : lstUsers)
            users.add(user.copy());
        return users;
    }

    public void addAllUsers(List<User> users) {
        lstUsers.addAll(users);
    }

    public void clearUsers() {
        lstUsers.clear();
    }
}

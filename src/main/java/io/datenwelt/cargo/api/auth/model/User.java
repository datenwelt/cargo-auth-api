/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.datenwelt.cargo.api.auth.model;

import io.datenwelt.sql.SqlStorable;

/**
 *
 * @author job
 */
public class User implements SqlStorable {
    
    private int id;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private int active;

    public User() {
    }
    
    public static User createSample() {
        User sample = new User();
        sample.id = 99;
        sample.email = "testmann@test.test";
        sample.firstname = "Test";
        sample.lastname = "Testmann";
        sample.password = "test12345";
        sample.active = 1;
        return sample;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
    
}

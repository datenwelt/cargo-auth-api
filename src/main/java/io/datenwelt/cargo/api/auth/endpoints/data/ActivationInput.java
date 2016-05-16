/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.datenwelt.cargo.api.auth.endpoints.data;

/**
 *
 * @author job
 */
public class ActivationInput {
    
    private String token;

    public ActivationInput(String token) {
        this.token = token;
    }

    public ActivationInput() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
}

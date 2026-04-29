package br.com.produlab.dto;

import br.com.produlab.entity.User;

public class AuthenticationResponse {
    private User user;
    private String token;
    private Boolean shouldUpdatePassword;

    public AuthenticationResponse(User user, String token, Boolean shouldUpdatePassword) {
        this.user = user;
        this.token = token;
        this.shouldUpdatePassword = shouldUpdatePassword;
    }

    public AuthenticationResponse(User user, String token) {
        this.user = user;
        this.token = token;
        this.shouldUpdatePassword = false;
    }

    public Boolean getShouldUpdatePassword() {
        return shouldUpdatePassword;
    }

    public void setShouldUpdatePassword(Boolean shouldUpdatePassword) {
        this.shouldUpdatePassword = shouldUpdatePassword;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

package br.com.produlab.dto;

import br.com.produlab.entity.User;

public class UpdateCredentialsRequest extends AuthenticationRequest {
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

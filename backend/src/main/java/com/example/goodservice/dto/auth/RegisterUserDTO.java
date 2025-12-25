package com.example.goodservice.dto.auth;

import lombok.Getter;

@Getter
public class RegisterUserDTO {

    private String username;
    private String profile;
    private String password;

    public void setUsername(String username) { this.username = username; }

    public void setProfile(String profile) { this.profile = profile; }

    public void setPassword(String password) { this.password = password; }
}

package com.example.goodservice.dto.auth;

import lombok.Getter;

@Getter
public class RegisterUserDTO {

    private String username;
    private String intro;
    private String password;

    public void setUsername(String username) { this.username = username; }

    public void setIntro(String intro) { this.intro = intro; }

    public void setPassword(String password) { this.password = password; }
}

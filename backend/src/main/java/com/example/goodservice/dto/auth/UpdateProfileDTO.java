package com.example.goodservice.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateProfileDTO {
    private String phone;
    private String profile;  // 用户简介
    private String password;

    // 构造方法
    public UpdateProfileDTO() {}

    public UpdateProfileDTO(String phone, String profile, String password) {
        this.phone = phone;
        this.profile = profile;
        this.password = password;
    }
}
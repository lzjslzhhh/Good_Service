package com.example.goodservice.dto.admin;

public class UserProfileDTO {
    private String phone;
    private String intro;
    private String password; // 可为空

    // getter / setter
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

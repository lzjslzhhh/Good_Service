package com.example.goodservice.dto.admin;

public class UserProfileDTO {
    private String phone;
    private String profile;
    private String password; // 可为空

    // getter / setter
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

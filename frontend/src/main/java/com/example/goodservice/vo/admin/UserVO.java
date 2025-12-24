package com.example.goodservice.vo.admin;

public class UserVO {
    private Long id;
    private String username;
    private String role; // admin / user
    private String phone;
    private String intro;
    private Double rating; // 信用评分

    // getter / setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
}

package com.example.goodservice.vo.auth;

public class LoginUserVO {

    private Long id;
    private String username;
    private Integer userType;    // 0-普通用户, 1-服务者, etc.
    private String token;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getUserType() { return userType; }
    public void setuserType(Integer userType) { this.userType = userType; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

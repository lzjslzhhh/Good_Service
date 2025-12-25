package com.example.goodservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private Integer userType;    // 0-普通用户, 1-服务者, etc.
    private String realName;
    private String phone;
    private String profile;      // 个人简介

    private LocalDateTime registerTime;
    private LocalDateTime updateTime;
    public Long getUserId() { return id; }
}

package com.example.goodservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "response")
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responseId;
    private Long needId;
    private Long userId;          // 发布者
    private String content;   // 需求描述
    private String imageUrl;
    private Integer status;       // 0-待接受 1-同意 2-拒绝 3-取消
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}

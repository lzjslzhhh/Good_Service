package com.example.goodservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "need")
public class Need {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long needId;

    private Long userId;          // 发布者
    private String title;         // 需求标题
    private String description;   // 需求描述
    private Integer status;       // 0-待接单 1-进行中 2-已完成

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

package com.example.goodservice.dto.need;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter
public class NeedRequestDTO {
    private Long needId;
    private Long userId;          // 发布者
    private String region;
    private String serviceType;
    private String title;         // 需求标题
    private String description;   // 需求描述
    private Integer status;       // 0-待接单 1-进行中 2-已完成

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

package com.example.goodservice.vo.service;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MyNeedVO {
    private Long needId;
    private Long userId;
    private Long regionId;
    private String region;
    private String title;
    private String serviceType;
    private String description;
    private Integer status;         // Pending / Responded
    private Long responseCount; // 响应数量
    // getter / setter
}

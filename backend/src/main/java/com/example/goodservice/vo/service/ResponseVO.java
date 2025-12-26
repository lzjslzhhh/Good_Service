package com.example.goodservice.vo.service;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResponseVO {
    private Long id;
    private Long needId;
    private String needTitle; // 添加需求标题
    private String responderName;
    private String responderIntro;
    private String serviceContent;
    private Integer status; // Submitted / Accepted / Rejected
}

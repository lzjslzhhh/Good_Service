package com.example.goodservice.vo.service;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class ResponseVO {
    private Long id;
    private Long needId;
    private String needTitle; // 添加需求标题
    private String responderName;
    private String responderIntro;
    private String serviceContent;
    private Double price;
    private Long status; // Submitted / Accepted / Rejected
    // getter / setter
}

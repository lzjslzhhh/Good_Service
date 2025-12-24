package com.example.goodservice.vo.service;

public class NeedItemVO {

    private Long id;
    private String title;
    private String category;
    private String region;
    private String publishTime; // 或 Date 类型，根据数据库
    private String status;      // "Pending" 或 "Responded"

    public NeedItemVO() {}

    public NeedItemVO(Long id, String title, String category, String region, String publishTime, String status) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.region = region;
        this.publishTime = publishTime;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getPublishTime() { return publishTime; }
    public void setPublishTime(String publishTime) { this.publishTime = publishTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

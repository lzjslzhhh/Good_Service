package com.example.goodservice.vo.admin;

public class MonthlyStatVO {

    private String month;
    private String region;
    private Integer publishCount;
    private Integer successCount;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getPublishCount() {
        return publishCount;
    }

    public void setPublishCount(Integer publishCount) {
        this.publishCount = publishCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }
}

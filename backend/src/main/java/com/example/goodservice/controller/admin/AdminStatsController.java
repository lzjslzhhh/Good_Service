package com.example.goodservice.controller.admin;

import com.example.goodservice.common.Result;
import com.example.goodservice.service.AdminStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private AdminStatsService adminStatsService;

    /**
     * 系统总体统计
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(adminStatsService.getOverviewStats());
    }

    /**
     * 需求发布趋势（按月）
     */
    @GetMapping("/need-trend")
    public Result<List<Map<String, Object>>> needTrend() {
        return Result.success(adminStatsService.getNeedTrend());
    }
}

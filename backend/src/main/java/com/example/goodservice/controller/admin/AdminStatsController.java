package com.example.goodservice.controller.admin;

import com.example.goodservice.common.Result;
import com.example.goodservice.service.AdminStatsService;
import com.example.goodservice.vo.admin.MonthlyStatVO;
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

    /**
     * 管理员：月度统计（按月的 publishCount 和 successCount）
     * GET /api/admin/stats/monthly?start=2025-01&end=2025-06&region=北京
     */
    @GetMapping("/monthly")
    public Result<List<MonthlyStatVO>> getMonthlyStats(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String region) {
        return Result.success(adminStatsService.getMonthlyStats(start, end, region));
    }
}

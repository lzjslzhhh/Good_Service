package com.example.goodservice.controller.admin;

import com.example.goodservice.common.Result;
import com.example.goodservice.vo.admin.MonthlyStatVO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminStatsController {

    @GetMapping("/stats/monthly")
    public Result<List<MonthlyStatVO>> getMonthlyStats(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String region
    ) {

        List<MonthlyStatVO> list = new ArrayList<>();

        String[] months = {
                "2023-06", "2023-07", "2023-08",
                "2023-09", "2023-10", "2023-11"
        };

        for (String month : months) {
            MonthlyStatVO vo = new MonthlyStatVO();
            vo.setMonth(month);
            vo.setRegion((region == null || region.isBlank()) ? "全区" : region);
            vo.setPublishCount((int) (Math.random() * 200 + 50));
            vo.setSuccessCount((int) (Math.random() * 100 + 20));
            list.add(vo);
        }

        return Result.success(list);
    }
}

package com.example.goodservice.service;

import com.example.goodservice.repository.NeedRepository;
import com.example.goodservice.repository.UserRepository;
import com.example.goodservice.vo.admin.MonthlyStatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminStatsService {

    @Autowired
    private NeedRepository needRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取系统总体统计数据
     */
    public Map<String, Object> getOverviewStats() {
        Map<String, Object> map = new HashMap<>();

        map.put("totalUsers", userRepository.count());
        map.put("totalNeeds", needRepository.count());
        map.put("finishedNeeds", needRepository.countByStatus(2));

        return map;
    }

    /**
     * 获取需求发布趋势（按月）
     */
    public List<Map<String, Object>> getNeedTrend() {
        List<Object[]> raw = needRepository.countNeedByMonth();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : raw) {
            Map<String, Object> map = new HashMap<>();
            map.put("month", row[0]);
            map.put("count", row[1]);
            result.add(map);
        }
        return result;
    }

    public List<MonthlyStatVO> getMonthlyStats(String start, String end, String region) {
        // 如果未传 start/end，直接返回空列表（或按需改为全量时间范围）
        if (start == null || start.isEmpty() || end == null || end.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询发布数与成功数（status=2 表示已完成）
        List<Object[]> pubRaw = needRepository.countNeedByMonthAndRegionBetween(start, end, region);
        List<Object[]> sucRaw = needRepository.countSuccessByMonthAndRegionBetween(1, start, end, region);

        // 合并：key = month + "||" + region
        Map<String, MonthlyStatVO> map = new LinkedHashMap<>();

        if (pubRaw != null) {
            for (Object[] row : pubRaw) {
                String month = row[0] != null ? row[0].toString() : "";
                String reg = row[1] != null ? row[1].toString() : "未知";
                Integer cnt = row[2] != null ? ((Number) row[2]).intValue() : 0;
                String key = month + "||" + reg;
                MonthlyStatVO vo = new MonthlyStatVO();
                vo.setMonth(month);
                vo.setRegion(reg);
                vo.setPublishCount(cnt);
                vo.setSuccessCount(0);
                map.put(key, vo);
            }
        }

        if (sucRaw != null) {
            for (Object[] row : sucRaw) {
                String month = row[0] != null ? row[0].toString() : "";
                String reg = row[1] != null ? row[1].toString() : "未知";
                Integer cnt = row[2] != null ? ((Number) row[2]).intValue() : 0;
                String key = month + "||" + reg;
                MonthlyStatVO vo = map.getOrDefault(key, new MonthlyStatVO());
                vo.setMonth(month);
                vo.setRegion(reg);
                vo.setSuccessCount(cnt);
                if (vo.getPublishCount() == null) vo.setPublishCount(0);
                map.put(key, vo);
            }
        }

        // 补齐数值
        for (MonthlyStatVO vo : map.values()) {
            if (vo.getPublishCount() == null) vo.setPublishCount(0);
            if (vo.getSuccessCount() == null) vo.setSuccessCount(0);
        }

        // 返回有序列表（map 保持插入顺序）
        return new ArrayList<>(map.values());
    }
}

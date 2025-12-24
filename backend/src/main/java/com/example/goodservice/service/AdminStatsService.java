package com.example.goodservice.service;

import com.example.goodservice.repository.NeedRepository;
import com.example.goodservice.repository.UserRepository;
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
}

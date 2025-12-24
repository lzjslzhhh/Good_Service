package com.example.goodservice.repository;

import com.example.goodservice.entity.Need;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

public interface NeedRepository extends JpaRepository<Need, Long> {

    List<Need> findByUserId(Long userId);

    // 统计总需求数
    long countBy();

    // 统计已完成需求数
    long countByStatus(Integer status);

    // 按月份统计需求发布数量
    @Query("""
        SELECT FUNCTION('DATE_FORMAT', n.createTime, '%Y-%m'), COUNT(n)
        FROM Need n
        GROUP BY FUNCTION('DATE_FORMAT', n.createTime, '%Y-%m')
        ORDER BY FUNCTION('DATE_FORMAT', n.createTime, '%Y-%m')
    """)
    List<Object[]> countNeedByMonth();
}

package com.example.goodservice.repository;

import com.example.goodservice.entity.Need;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NeedRepository extends JpaRepository<Need, Long>, JpaSpecificationExecutor<Need> {

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

    // 按月份统计发布数量，支持按区间（start,end 格式 'YYYY-MM'）
    @Query("""
        SELECT FUNCTION('DATE_FORMAT', n.createTime, '%Y-%m'), COUNT(n)
        FROM Need n
        WHERE FUNCTION('DATE_FORMAT', n.createTime, '%Y-%m') BETWEEN :start AND :end
        GROUP BY FUNCTION('DATE_FORMAT', n.createTime, '%Y-%m')
        ORDER BY FUNCTION('DATE_FORMAT', n.createTime, '%Y-%m')
    """)
    List<Object[]> countNeedByMonthBetween(@Param("start") String start, @Param("end") String end);

    // 按月份统计已完成需求（status），支持按区间
    @Query("""
        SELECT FUNCTION('DATE_FORMAT', n.createTime, '%Y-%m'), COUNT(n)
        FROM Need n
        WHERE n.status = :status AND FUNCTION('DATE_FORMAT', n.createTime, '%Y-%m') BETWEEN :start AND :end
        GROUP BY FUNCTION('DATE_FORMAT', n.createTime, '%Y-%m')
        ORDER BY FUNCTION('DATE_FORMAT', n.createTime, '%Y-%m')
    """)
    List<Object[]> countFinishedNeedByMonthBetween(@Param("status") Integer status, @Param("start") String start, @Param("end") String end);

        // 按月份+地域统计发布数量（支持区间和可选地域名称过滤）
    @Query(value = """
        SELECT DATE_FORMAT(n.create_time, '%Y-%m') AS month,
               COALESCE(r.region_name, '未知') AS region,
               COUNT(*) AS cnt
        FROM need n
        LEFT JOIN region r ON n.region_id = r.region_id
        WHERE DATE_FORMAT(n.create_time, '%Y-%m') BETWEEN :start AND :end
          AND (:region IS NULL OR :region = '' OR r.region_name = :region)
        GROUP BY month, region
        ORDER BY month, region
        """, nativeQuery = true)
    List<Object[]> countNeedByMonthAndRegionBetween(@Param("start") String start,
                                                    @Param("end") String end,
                                                    @Param("region") String region);

    // 按月份+地域统计已完成（status）数量（支持区间和可选地域名称过滤）
    @Query(value = """
        SELECT DATE_FORMAT(n.create_time, '%Y-%m') AS month,
               COALESCE(r.region_name, '未知') AS region,
               COUNT(*) AS cnt
        FROM need n
        LEFT JOIN region r ON n.region_id = r.region_id
        WHERE n.status = :status
          AND DATE_FORMAT(n.create_time, '%Y-%m') BETWEEN :start AND :end
          AND (:region IS NULL OR :region = '' OR r.region_name = :region)
        GROUP BY month, region
        ORDER BY month, region
        """, nativeQuery = true)
    List<Object[]> countFinishedNeedByMonthAndRegionBetween(@Param("status") Integer status,
                                                            @Param("start") String start,
                                                            @Param("end") String end,
                                                            @Param("region") String region);

        // 按月份+地域统计已接受响应（status = :status），支持区间和可选地域名称过滤
    @Query(value = """
        SELECT DATE_FORMAT(r.create_time, '%Y-%m') AS month,
               COALESCE(reg.region_name, '未知') AS region,
               COUNT(*) AS cnt
        FROM response r
        LEFT JOIN need n ON r.need_id = n.need_id
        LEFT JOIN region reg ON n.region_id = reg.region_id
        WHERE r.status = :status
          AND DATE_FORMAT(r.create_time, '%Y-%m') BETWEEN :start AND :end
          AND (:region IS NULL OR :region = '' OR reg.region_name = :region)
        GROUP BY month, region
        ORDER BY month, region
        """, nativeQuery = true)
    List<Object[]> countSuccessByMonthAndRegionBetween(@Param("status") Integer status,
                                                       @Param("start") String start,
                                                       @Param("end") String end,
                                                       @Param("region") String region);
}

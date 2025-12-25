package com.example.goodservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "数据库连接不应为null");
            assertTrue(connection.isValid(2), "数据库连接应该有效");
            System.out.println("✅ 数据库连接测试通过");
        } catch (SQLException e) {
            throw new RuntimeException("数据库连接失败: " + e.getMessage(), e);
        }
    }

    @Test
    public void testJdbcTemplateConnection() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertNotNull(result, "JDBC模板查询结果不应为null");
        assertTrue(result == 1, "应该返回数字1");
        System.out.println("✅ JDBC模板连接测试通过");
    }

    @Test
    public void testDatabaseExists() {
        try {
            jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            System.out.println("✅ 数据库存在性检查通过");
        } catch (Exception e) {
            throw new RuntimeException("数据库不存在或不可访问: " + e.getMessage(), e);
        }
    }
}
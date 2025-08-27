package com.codegym.projectmodule5.controller.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
public class TestConnectionController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/test-connection")
    public String testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return "✅ Kết nối thành công tới database: " + conn.getCatalog();
        } catch (SQLException e) {
            return "❌ Lỗi kết nối: " + e.getMessage();
        }
    }
}

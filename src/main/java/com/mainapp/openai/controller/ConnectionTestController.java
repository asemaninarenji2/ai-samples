package com.mainapp.openai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class ConnectionTestController {

    private final DataSource secondaryDataSource;

    public ConnectionTestController(@Qualifier("postgresDataSource") DataSource secondaryDataSource) {
        this.secondaryDataSource = secondaryDataSource;
    }

    @GetMapping("/test-postgres")
    public String testPostgresConnection() {
        System.out.println("Have non null datasource for Postgress:"+ secondaryDataSource.toString());
        try (Connection conn = secondaryDataSource.getConnection()) {
            if (conn.isValid(5)) {
                return "PostgreSQL connection successful!";
            }
        } catch (Exception e) {
            return "PostgreSQL connection failed: " + e.getMessage();
        }
        return "Connection test failed";
    }
}

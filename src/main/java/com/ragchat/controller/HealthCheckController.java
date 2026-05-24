package com.ragchat.controller;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {
	 private static final Logger log = LoggerFactory.getLogger(HealthCheckController.class);
	 
	    @Value("${info.app.version:1.0.0}")
	    private String appVersion;
	    
		@Autowired
	    private DataSource dataSource;

	    @GetMapping
	    @Operation(summary = "Full health check",
	               description = "Returns service status including database connectivity.")
	    public ResponseEntity<Map<String, Object>> health() {
	        Map<String, Object> response = new LinkedHashMap<>();
	        response.put("service", "RAG Chat Storage Microservice");
	        response.put("version", appVersion);
	        response.put("timestamp", LocalDateTime.now());
	        response.put("status", checkDatabase() ? "UP" : "DEGRADED");
	        response.put("database", checkDatabase() ? "UP" : "DOWN");
	        log.info("Health check is UP ");
	        return ResponseEntity.ok(response);
	    }
	    
		private boolean checkDatabase() {
			try (Connection conn = dataSource.getConnection()) {
				return conn.isValid(2);
			} catch (Exception e) {
				return false;
			}
		}

}

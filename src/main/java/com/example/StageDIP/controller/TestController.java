package com.example.StageDIP.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
@RequestMapping("/test")
public class TestController {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping("/db")
	public String Testconnection() {
		try {
			jdbcTemplate.queryForObject("SELECT 1", Integer.class);
			return "connexion reussite";
		}catch (Exception e) {
			return"echec" + e.getMessage();
		}
	}

}

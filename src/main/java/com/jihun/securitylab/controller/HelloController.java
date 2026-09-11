package com.jihun.securitylab.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/")
	public String hello() {
		return "Hello Spring Security";
	}

	@GetMapping("/public")
	public String publicPage() {
		return "Public Page";
	}

	@GetMapping("/private")
	public String privatePage() {
		return "Private Page";
	}

}
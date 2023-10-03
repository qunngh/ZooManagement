package com.fzoo.zoomanagementsystem.zoomanagementsystem;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.ResponseEntity;

public class ServletInitializer extends SpringBootServletInitializer {

	public ResponseEntity
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ZooManagementSystemApplication.class);
	}

}
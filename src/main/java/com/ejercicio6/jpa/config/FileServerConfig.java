package com.ejercicio6.jpa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("file.server")
public class FileServerConfig {
	private String home;

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}
	
	
}

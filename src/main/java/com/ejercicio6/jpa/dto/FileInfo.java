package com.ejercicio6.jpa.dto;

public class FileInfo {
	private final String filePath;
	
	public FileInfo(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFilePath() {
		return filePath;
	}
}

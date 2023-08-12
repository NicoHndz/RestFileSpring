package com.ejercicio6.jpa.dto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.springframework.core.io.Resource;

public interface FileService {
	Path getBasePath();

	Resource loadFileAsResource(Path filePath) throws FileNotFoundException;

	FileList getFilesInfo(Path filePath) throws IOException;

	void saveFile(Path filePath, InputStream inputSteam) throws IOException;

	void delete(Path filePath) throws IOException;

	void createDirectory(Path filePath) throws IOException;
}
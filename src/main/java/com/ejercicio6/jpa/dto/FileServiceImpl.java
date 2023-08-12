package com.ejercicio6.jpa.dto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import com.ejercicio6.jpa.config.FileServerConfig;

@Service
public class FileServiceImpl implements FileService {

	private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

	private final Path fileStorageLocation;

	public FileServiceImpl(FileServerConfig fileServerConfig) {
		LOG.info("fileStorageLocation)= {}", fileServerConfig.getHome());
		fileStorageLocation = Paths.get(fileServerConfig.getHome()).toAbsolutePath().normalize();
	}

	@Override
	public Path getBasePath() {
		return fileStorageLocation;
	}

	@Override
	public Resource loadFileAsResource(Path filePath) throws FileNotFoundException {
		LOG.info("loadFileAsResource: {}", filePath);

		try {
			Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
			Resource resource = new UrlResource(resolvedFilePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileNotFoundException("Archivo no existe" + filePath);
			}
		} catch (MalformedURLException ex) {
			throw new FileNotFoundException("file not found" + filePath);
		}

	}

	@Override
	public FileList getFilesInfo(Path filePath) throws IOException {
		LOG.info("getFilesInfo: {}", filePath);
		FileList fileList = new FileList(filePath.toString());
		Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
		try (Stream<Path> fileWalk = Files.walk(resolvedFilePath, 1)) {
			fileWalk.forEach(fw -> {
				if (resolvedFilePath.endsWith(fw)) {

				} else if (Files.isDirectory(fw)) {
					fileList.add(new DirectoryInfo(fw.getFileName().toString()));
				} else {
					fileList.add(new FileInfo(fw.getFileName().toString()));
				}
			});
		}
		return fileList;
	}

	@Override
	public void saveFile(Path filePath, InputStream inputStream) throws IOException {
		LOG.info("save file:{}", filePath);
		Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
		byte[] buffer = new byte[inputStream.available()];
		inputStream.read(buffer);

		File targetFile = resolvedFilePath.toFile();
		OutputStream outStream = new FileOutputStream(targetFile);
		outStream.write(buffer);
	}

	@Override
	public void delete(Path filePath) throws IOException {
		LOG.info("delete: {}" + filePath);
		Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
		LOG.info("deleting:{}", resolvedFilePath.toString());
		if (Files.isDirectory(resolvedFilePath)) {
			FileSystemUtils.deleteRecursively(resolvedFilePath);
		}
	}

	@Override
	public void createDirectory(Path filePath) throws IOException {
		LOG.info("create: {}" + filePath);

		Path resolvedFilePath = this.fileStorageLocation.resolve(filePath).normalize();
		Files.createDirectories(resolvedFilePath);
	}

}

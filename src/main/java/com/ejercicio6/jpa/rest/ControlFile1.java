package com.ejercicio6.jpa.rest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ejercicio6.jpa.dto.FileList;
import com.ejercicio6.jpa.dto.FileService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = ControlFile1.URI_PREFIX)
public class ControlFile1 {
	private static final Logger LOG = LoggerFactory.getLogger(ControlFile1.class);
	public static final String URI_PREFIX = "/service/file";
	public static final String LIST_PREFIX = "/list/";
	public static final String DOWNLOAD_PREFIX = "/download/";
	public static final String UPLOAD_PREFIX = "/upload/";
	public static final String DELETE_PREFIX = "/delete/";
	public static final String CREATEDIR_PREFIX = "/createdir/";

	@Autowired
	private FileService fileService;
	@Autowired
	private HttpServletRequest httpServletRequest;

	@GetMapping(DOWNLOAD_PREFIX + "**")
	public ResponseEntity<Resource> downloadFile() {
		try {
			String contextPath = httpServletRequest.getRequestURI();
			Path filePath = Paths.get(contextPath.substring((URI_PREFIX + DOWNLOAD_PREFIX).length()));
			LOG.info("download file:{}", contextPath);
			Resource resource = fileService.loadFileAsResource(filePath);
			String contentType = "application/octet-stream";
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (FileNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping(LIST_PREFIX + "**")
	public ResponseEntity<FileList> getFiles() {
		try {
			String contextPath = httpServletRequest.getRequestURI();
			Path filePath = Paths.get(contextPath.substring((URI_PREFIX + LIST_PREFIX).length()));
			LOG.info("get files: {}", filePath);
			FileList fileInfo = fileService.getFilesInfo(filePath);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(fileInfo);
		} catch (IOException ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping(UPLOAD_PREFIX + "**")
	public ResponseEntity<Resource> fileUpload(@RequestParam("file") MultipartFile file){
		try {
			String contextPath = httpServletRequest.getRequestURI();
			Path filePath = Paths.get(contextPath.substring((URI_PREFIX + UPLOAD_PREFIX).length()));
			LOG.info("upload: {}", filePath);
			fileService.saveFile(filePath, file.getInputStream());//aqui entra al catch
			return ResponseEntity.ok().build();
		}catch(IOException ex) {
			System.out.println(ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@DeleteMapping(DELETE_PREFIX + "**")
	public ResponseEntity<Resource> deleteFile(){
		try {
			String contextPath = httpServletRequest.getRequestURI();
			Path filePath = Paths.get(contextPath.substring((URI_PREFIX + DELETE_PREFIX).length()));
			LOG.info("delete file: {}", filePath);
			fileService.delete(filePath);
			return ResponseEntity.ok().build();
		}catch(IOException ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping(CREATEDIR_PREFIX + "**")
	public ResponseEntity<Resource> createDir(){
		try {
			String contextPath = httpServletRequest.getRequestURI();
			Path filePath = Paths.get(contextPath.substring((URI_PREFIX + CREATEDIR_PREFIX).length()));
			LOG.info("create file: {}", filePath);
			fileService.createDirectory(filePath);
			return ResponseEntity.ok().build();
		}catch(IOException ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}

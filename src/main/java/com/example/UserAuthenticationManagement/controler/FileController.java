package com.example.UserAuthenticationManagement.controler;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Optional;

import javax.annotation.processing.FilerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.UserAuthenticationManagement.dao.FileRepo;
import com.example.UserAuthenticationManagement.entity.FileEntity;
import com.example.UserAuthenticationManagement.service.FileService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("v1/file/")
@Slf4j
public class FileController {

	@Autowired
	private FileService fileService;

	@Autowired
	private FileRepo fileRepo;

	@PostMapping("/readXmlFile")
	//@PreAuthorize()
	ResponseEntity<?> readXmlFile(@RequestParam MultipartFile file) throws FilerException {
		return new ResponseEntity<>(fileService.readXmlFile(file), HttpStatus.ACCEPTED);
	}

	@PostMapping("/readJsonFile")
	ResponseEntity<?> readFile(@RequestParam MultipartFile file) throws FilerException {
		try {
			log.info("read File Api calling....");
			return new ResponseEntity<>(fileService.readJsonFile(file), HttpStatus.ACCEPTED);
		} catch (FilerException e) {
			throw new FilerException(e.getLocalizedMessage().toString());
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	@PostMapping("/uploadFile")
	ResponseEntity<?> upoadFile(@RequestParam String docType, @RequestParam(required = false) MultipartFile file)
			throws FilerException, FileAlreadyExistsException {
		log.info("uploadFile Api calling..... " + docType + " " + file);
		return new ResponseEntity<>(fileService.saveFile(file, docType), HttpStatus.OK);

	}

	@GetMapping("/downloadFile/{fileId}")
	ResponseEntity<?> downloadFile(@PathVariable long fileId) throws FileNotFoundException {

		log.info("download file api calling... " + fileId);
		Optional<FileEntity> fileEntity = fileRepo.findById(fileId);
		if (fileEntity.isEmpty()) {
			// throw new RuntimeException("file Not found for fileId " + fileId);
			return new ResponseEntity<>("file Not found for fileId: " + fileId, HttpStatus.NOT_FOUND);
		}
		String fileName = fileEntity.get().getFileName();

		Resource resource = fileService.downloadFile(fileName);
		if (resource == null) {
			return ResponseEntity.notFound().build();
		}

		else {
			String contentType = "application/octet-stream";
			String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, headerValue).body(resource);
		}

	}

	@DeleteMapping("/deleteFile")
	ResponseEntity<?> deleteFile(@RequestParam(required = false) String docType,
			@RequestParam(required = false) String fileName) throws FileNotFoundException {
		log.info("delete api calling...." + fileName);
		if (fileService.deleteFile(docType, fileName)) {
			return new ResponseEntity<>("file deleted Succesfull: " + fileName, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("file not Exist on path: " + fileName, HttpStatus.NOT_FOUND);
		}

	}

}

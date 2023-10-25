package com.example.UserAuthenticationManagement.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import javax.annotation.processing.FilerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.UserAuthenticationManagement.dao.FileRepo;
import com.example.UserAuthenticationManagement.dto.ProductDto;
import com.example.UserAuthenticationManagement.dto.UserDto;
import com.example.UserAuthenticationManagement.entity.FileEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileService {

	private final Path finalStorageLocation;

	@Autowired
	private FileRepo fileRepo;
	
	@Autowired
	private ObjectMapper maper;
	// private ObjectMapper maper;

	@Autowired
	public FileService(FileEntity fileEntity) {
		this.finalStorageLocation = Paths.get(fileEntity.getUploadDir()).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.finalStorageLocation);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage().toString());
		}
	}

	public String saveFile(MultipartFile file, String DocType) throws FilerException, FileAlreadyExistsException {

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		Path targetLocation = this.finalStorageLocation.resolve(fileName);

		Optional<FileEntity> fileEntity = Optional.ofNullable(fileRepo.findAllByFileName(fileName));
		if (fileEntity.isPresent()) {
			throw new FileAlreadyExistsException(fileName + " alredy exist");
		}
		try {
			FileEntity entity = new FileEntity();
			entity.setDocumentType(DocType);
			entity.setFileType(file.getContentType());
			entity.setFileName(fileName);
			entity.setSize(file.getSize());
			entity.setUploadDir(finalStorageLocation.toString());
			fileRepo.save(entity);

			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new FilerException(e.getLocalizedMessage().toString());
			// e.printStackTrace();
		}

		return fileName;
	}

	public Resource downloadFile(String fileName) throws FileNotFoundException {

		Path filepath = this.finalStorageLocation.resolve(fileName).normalize();
		try {
			Resource resorce = new UrlResource(filepath.toUri());

			if (resorce.exists()) {
				return resorce;
			} else {
				return null;
				// throw new FileNotFoundException("file not found :" + fileName);
			}

		} catch (MalformedURLException e) {
			throw new FileNotFoundException(e.getLocalizedMessage().toString());
			// e.printStackTrace();
		}
		// return null;
	}

	public boolean deleteFile(String docType, String fileName) throws FileNotFoundException {
		FileEntity fleEntity = fileRepo.findAllByFileName(fileName);
		if (fleEntity == null) {
			throw new FileNotFoundException("file record not found in DB: " + fileName);
		}
		fileRepo.deleteById(fleEntity.getFileId());
		log.info("file Record deleted from table...");
		Path filePath = this.finalStorageLocation.resolve(fileName).normalize();
		try {
			return Files.deleteIfExists(filePath);
		} catch (IOException e) {
			throw new FileNotFoundException("file path not found for: " + fileName);
			// e.printStackTrace();
		}

	}

	public ProductDto readJsonFile(MultipartFile file) throws FilerException {
		try {
			
			// RealState realState = maper.readValue(file.getInputStream(),
			// RealState.class);
			ProductDto order = maper.readValue(file.getInputStream(), ProductDto.class);
			return order;
		} catch (IOException e) {
			throw new FilerException(e.getLocalizedMessage().toString());
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public UserDto readXmlFile(MultipartFile file) throws FilerException {
            try {
			XmlMapper xmlmaper = new XmlMapper();
            UserDto userDto = xmlmaper.readValue(file.getInputStream(), UserDto.class);
            return userDto;
			} catch (IOException e) {
				throw new FilerException("xml file exception: "+e.getLocalizedMessage().toString());
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
	}

}

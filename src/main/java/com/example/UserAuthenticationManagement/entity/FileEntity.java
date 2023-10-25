package com.example.UserAuthenticationManagement.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "file")
@Component
@Entity
@Data
public class FileEntity extends DateAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long fileId;
	private String fileName;
	private String DocumentType;
	private String fileType;
	private long size;
	private String uploadDir;

}

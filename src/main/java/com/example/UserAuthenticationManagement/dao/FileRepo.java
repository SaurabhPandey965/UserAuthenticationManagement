package com.example.UserAuthenticationManagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.UserAuthenticationManagement.entity.FileEntity;

public interface FileRepo  extends JpaRepository<FileEntity, Long>{
	
	   FileEntity findAllByFileName(String fileName);

}

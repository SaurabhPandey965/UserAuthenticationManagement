 package com.example.UserAuthenticationManagement.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.UserAuthenticationManagement.entity.Authority;

public interface AuthorityDao extends JpaRepository<Authority, Long> {
	
	

}

 package com.example.UserAuthenticationManagement.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.UserAuthenticationManagement.entity.Authority;
import java.util.List;


public interface AuthorityDao extends JpaRepository<Authority, Long> {
	
	List<Authority> findByCode(String code);
	
	

}

package com.example.UserAuthenticationManagement.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.UserAuthenticationManagement.entity.Roles;
import java.util.List;


public interface RolesDao  extends JpaRepository<Roles, Long>{

	
	Roles findByName(String name);
}

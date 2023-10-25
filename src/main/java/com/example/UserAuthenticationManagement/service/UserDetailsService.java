package com.example.UserAuthenticationManagement.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.UserAuthenticationManagement.entity.UserEntity;



public interface UserDetailsService {
	
   UserEntity	loadByUsername(String username) throws UsernameNotFoundException;

}

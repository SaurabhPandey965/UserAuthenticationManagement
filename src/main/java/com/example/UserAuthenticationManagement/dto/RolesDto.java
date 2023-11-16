package com.example.UserAuthenticationManagement.dto;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class RolesDto {
	
	long id;
	String name;
	Integer level;
	List<AuthorityDto> authorities;
	

}

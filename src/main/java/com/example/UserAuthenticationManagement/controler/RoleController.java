package com.example.UserAuthenticationManagement.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.UserAuthenticationManagement.dto.RolesDto;
import com.example.UserAuthenticationManagement.service.RoleService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("v1/api/role")
public class RoleController {

	@Autowired
	private RoleService roleService;
	
	@PostMapping("/creteRole")
	ResponseEntity<?> createRole(@RequestBody RolesDto roleDto){
		log.info("createRole api calling.......");
		return new ResponseEntity<>(roleService.createRole(roleDto), HttpStatus.CREATED);
				
	}
	
	@GetMapping("/getRole")
	ResponseEntity<?> getRoles(){
		return new ResponseEntity<>(roleService.findAllRoles(), HttpStatus.OK);
	}
}

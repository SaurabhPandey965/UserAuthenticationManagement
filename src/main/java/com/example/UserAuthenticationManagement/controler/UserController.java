package com.example.UserAuthenticationManagement.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.UserAuthenticationManagement.dto.UserDto;
import com.example.UserAuthenticationManagement.service.UserService;
import com.example.UserAuthenticationManagement.utils.Authorities;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/api/user")
@Slf4j
//@SecurityRequirement(name = "bearerAuth")

public class UserController {

	@Autowired
	private UserService userService;

	// @ApiOperation(value = "", authorizations = { @Authorization(value =
	// "jwtToken") })

	@GetMapping("/getUser/{userName}")
	@PreAuthorize("hasAuthority('" + Authorities.READ_AUTHORITY + "')")
	ResponseEntity<?> getUser(@PathVariable String userName) {
		log.info("getUser api calling.....");
		return new ResponseEntity<>(userService.getUserByUserName(userName), HttpStatus.OK);

	}

	@GetMapping("/page")
	@PreAuthorize("hasAuthority('" + Authorities.READ_AUTHORITY + "')")

	ResponseEntity<?> getAllUserInPage(@RequestParam(required = false) int pageNo,
			@RequestParam(required = false) int pageSize, String sortByFieldName) {

		return new ResponseEntity<>(userService.findAllByPageination(pageNo, pageSize, sortByFieldName), HttpStatus.OK);
	}
	// @ApiOperation(value = "", authorizations = { @Authorization(value =
	// "jwtToken") })

	@GetMapping("/getAllUser")
	@PreAuthorize("hasAuthority('" + Authorities.READ_AUTHORITY + "')")

	ResponseEntity<?> getAllUser() {
		log.info("getAllUser api calling......");
		return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
	}

	@DeleteMapping("/deleteUser")
	public void deleteUser() {
		userService.deleteUser();
	}

	@PutMapping("/updateUser/{id}")
	ResponseEntity<Void> updateUser(@PathVariable Integer id, @RequestBody UserDto userDto) {
		userService.updateUser(id, userDto);
		return ResponseEntity.ok().build();

	}

	@PatchMapping("/updateFiled")
	ResponseEntity<Void> updateUserField(@RequestParam int id, @RequestParam String name) {
		userService.updateUserField(id, name);
		return ResponseEntity.ok().build();
	}
}

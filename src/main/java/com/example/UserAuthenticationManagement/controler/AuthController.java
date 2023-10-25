package com.example.UserAuthenticationManagement.controler;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.UserAuthenticationManagement.dto.AuthRequestDto;
import com.example.UserAuthenticationManagement.dto.AuthResponseDTO;
import com.example.UserAuthenticationManagement.dto.UserDto;
import com.example.UserAuthenticationManagement.entity.UserEntity;
import com.example.UserAuthenticationManagement.exception.UserException;
import com.example.UserAuthenticationManagement.service.UserDetailsServiceImpl;
import com.example.UserAuthenticationManagement.service.UserService;
import com.example.UserAuthenticationManagement.utils.Constant;
import com.example.UserAuthenticationManagement.utils.JwtUtills;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/v1/auth/")
public class AuthController {

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtills jwtTokenUtil;

	@Autowired
	private UserService userService;

	@PostMapping("creatUser")
	ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
		log.info("creatUser api calling :" + userDto);
		return new ResponseEntity<>(userDetailsServiceImpl.createUser(userDto), HttpStatus.CREATED);
	}

	@PostMapping("getJwtToken")
	ResponseEntity<AuthResponseDTO> getToken(@RequestBody AuthRequestDto authRequestDto) {
		log.info("getToken Api calling " + authRequestDto);

		try {
			/*
			 * Authentication authentication = authenticationManager.authenticate(new
			 * UsernamePasswordAuthenticationToken( authRequestDto.getUserName(),
			 * authRequestDto.getPassword())); User user = (User)
			 * authentication.getPrincipal();
			 */
			UserEntity userEnty = userService.getUserByUserName(authRequestDto.getUserName());
			AuthResponseDTO authResponseDTO = new AuthResponseDTO();
			if (userEnty != null) {

				authResponseDTO.setAccessToken("Bearer " + jwtTokenUtil.generateToken(authRequestDto.getUserName().toLowerCase()));
				authResponseDTO.setExpiresIn(jwtTokenUtil.getExpiredDateFromToken(authResponseDTO.getAccessToken().replaceAll(Constant.TOKEN_PREFIX, "")));
				return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(authResponseDTO, HttpStatus.BAD_REQUEST);
			}
		} catch (BadCredentialsException e) {
			throw new UserException(HttpStatus.NOT_FOUND, "INVALID_CREDENTIALS", "Invalid username or password");

		}
		// return null;

	}

}

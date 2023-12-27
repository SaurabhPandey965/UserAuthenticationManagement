package com.example.UserAuthenticationManagement.controler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.UserAuthenticationManagement.dto.AuthRequestDto;
import com.example.UserAuthenticationManagement.dto.AuthResponseDTO;
import com.example.UserAuthenticationManagement.dto.RolesDto;
import com.example.UserAuthenticationManagement.dto.UserDto;
import com.example.UserAuthenticationManagement.entity.Roles;
import com.example.UserAuthenticationManagement.entity.UserEntity;
import com.example.UserAuthenticationManagement.exception.UserException;
import com.example.UserAuthenticationManagement.service.UserDetailsServiceImpl;
import com.example.UserAuthenticationManagement.service.UserService;
import com.example.UserAuthenticationManagement.utils.Constant;
import com.example.UserAuthenticationManagement.utils.JwtUtills;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/v1/auth/")
@RequiredArgsConstructor
public class AuthController {

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtills jwtTokenUtil;

	@Autowired
	private UserService userService;

	private Collection<? extends GrantedAuthority> autho;

	@PostMapping("creatUser")
	ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
		log.info("creatUser api calling :" + userDto);
		return new ResponseEntity<>(userDetailsServiceImpl.createUser(userDto), HttpStatus.CREATED);
	}

	@PostMapping("getJwtToken")
	ResponseEntity<AuthResponseDTO> getToken(@RequestBody AuthRequestDto authRequestDto) {
		log.info("getToken Api calling " + authRequestDto);

		try {

			/*Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authRequestDto.getUserName(), authRequestDto.getPassword()));
			UserDto user = (UserDto) authentication.getPrincipal();
              if(authentication.isAuthenticated()) {
            */	  
              
			// UserEntity userEnty =
			// userService.getUserByUserName(authRequestDto.getUserName());
		 UserDetails user = userService.loadUserByUsername(authRequestDto.getUserName());
			AuthResponseDTO authResponseDTO = new AuthResponseDTO();
			if (user != null) {

			    Set<Roles> roles = userService.getRoleByUsername(user.getUsername());
				authResponseDTO.setAccessToken(Constant.TOKEN_PREFIX
						+ jwtTokenUtil.generateToken(authRequestDto.getUserName().toLowerCase(), roles.stream().toList()));
				authResponseDTO.setExpiresIn(jwtTokenUtil.getExpiredDateFromToken(
						authResponseDTO.getAccessToken().replaceAll(Constant.TOKEN_PREFIX, "")));
				authResponseDTO.setRefreshToken(jwtTokenUtil.generateRefreshToken(user.getUsername()));
				return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(authResponseDTO, HttpStatus.BAD_REQUEST);
			}
			
			 // } else { throw new ResponseStatusException(HttpStatus.BAD_REQUEST); }
			 
		} catch (BadCredentialsException e) {
			throw new UserException(HttpStatus.NOT_FOUND, "INVALID_CREDENTIALS", "Invalid username or password");

		}
		// return null;
		
	}

}

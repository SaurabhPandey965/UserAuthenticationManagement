package com.example.UserAuthenticationManagement.authentication;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.UserAuthenticationManagement.entity.UserEntity;
import com.example.UserAuthenticationManagement.service.UserDetailsServiceImpl;
import com.example.UserAuthenticationManagement.service.UserService;
import com.example.UserAuthenticationManagement.utils.Constant;
import com.example.UserAuthenticationManagement.utils.JwtUtills;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JWTAuthorizationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtills jwtUtills;

	/*
	 * @Autowired private UserDetailsServiceImpl userDetailsServiceImpl;
	 */


	@Autowired
	private UserService userService;

	/*
	 * public JWTAuthorizationFilter() { super(); // TODO Auto-generated constructor
	 * stub }
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		final String header = request.getHeader(Constant.HEADER_STRING);
		// System.out.println("header " + header);
		if (header == null || !header.startsWith(Constant.TOKEN_PREFIX)) {
			chain.doFilter(request, response);
			return;
		}
		UsernamePasswordAuthenticationToken authentication = getAuthenticationToken(request);
		// authentication.setDetails(new
		// WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);

		// doFilterInternal(request, response, chain);
	}

	// read the jwt and validate token from request header
	// get the user from token and check whether user is present in db or not

	private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest request) {
		String token = request.getHeader(Constant.HEADER_STRING);
		System.out.println("token :" + token);
		// String jwtToken = null;
		if (token != null) {
			// token.substring(7);
			String jwtToken = token.replaceAll(Constant.TOKEN_PREFIX, "");
			String userName = jwtUtills.getUsernameFromToken(jwtToken);
			System.out.println("userName " + userName);
			// UserDetails user = userDetailsServiceImpl.loadUserByUsername(userName);
			UserEntity user = userService.getUserByUserName(userName);
			System.out.println(user);
			if (jwtUtills.validateToken(jwtToken, user.getUserName())) {
				System.out.println("true");
				return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
			}

			return null;
		}

		return null;

	}

}

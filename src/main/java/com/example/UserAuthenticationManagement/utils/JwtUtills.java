package com.example.UserAuthenticationManagement.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtills {

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
	private String secret = "saurabhkumarpandey";
	public static final String TOKEN_PREFIX = "Bearer ";

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		System.out.println(token);
		// token.replaceAll("Bearer ", "");

		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	/*
	 * public Date getExpirationDateFromToken(String token) {
	 * System.out.println("getExpirationDateFromToken"); // return
	 * getClaimFromToken(token, Claims::getExpiration); return
	 * getExpirationDateFromToken(token); }
	 */
	public Date getExpiredDateFromToken(String token) {
		try {
			Claims claims = getAllClaimsFromToken(token);
			return claims.getExpiration();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		System.out.println("getClaimFromToken" + token);
		final Claims claims = getAllClaimsFromToken(token);
		Date exp = claims.getExpiration();
		System.out.println("exp" + exp);
		return claimsResolver.apply(claims);
	} // for

	// retrieveing any information from token we will need the secret key

	private Claims getAllClaimsFromToken(String token) {
		// token.replaceAll("Bearer ", "");
		System.out.println("getAllClaimsFromToken" + token);
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {

		final Date expiration = getExpiredDateFromToken(token);
		System.out.println("isTokenExpired" + expiration);
		return expiration.before(new Date());
	}

	// generate token for user
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userName);
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string
	private String doGenerateToken(Map<String, Object> claims, String subject) {

		String token = Jwts.builder().setClaims(claims).setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 5000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
		return token;
	}

	// validate token
	public Boolean validateToken(String token, String userName) {
		final String username = getUsernameFromToken(token);
		System.out.println("validateToken" + username + "userDetails " + userName);
		if (username.equals(userName) && !isTokenExpired(token)) {
			return true;
		}
		return false;
		// && !isTokenExpired(token));
	}
}

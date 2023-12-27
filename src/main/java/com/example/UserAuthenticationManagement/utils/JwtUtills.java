package com.example.UserAuthenticationManagement.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.UserAuthenticationManagement.entity.Roles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.lang.Objects;

@Component
public class JwtUtills {
	// 5 hr token expiration time
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
	private String secret = "saurabhkumarpandey";
	public static final String TOKEN_PREFIX = "Bearer ";

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		System.out.println(token);

		return getAllClaimsFromToken(token).getSubject();

		// return getClaimFromToken(token, Claims::getSubject);
	}

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
		String userName = claims.getSubject();
		System.out.println("exp: " + exp + "userName: " + userName);
		return claimsResolver.apply(claims);
	} // for

	// retrieveing any information from token we will need the secret key

	private Claims getAllClaimsFromToken(String token) {
		// token.replaceAll("Bearer ", "");
		System.out.println("getAllClaimsFromToken: " + token);
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {

		final Date expiration = getExpiredDateFromToken(token);
		System.out.println("isTokenExpired: " + expiration);
		return expiration.before(new Date());
	}

	// generate token for user
	public String generateToken(String userName, List<Roles> roles) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(Constant.CLAIM_KEY_USERNAME, userName);
		claims.put(Constant.CLAIM_KEY_ROLES, roles.stream().map(a -> a.getName()).toList());
		return doGenerateToken(claims, JWT_TOKEN_VALIDITY);
	}

	public String generateRefreshToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(Constant.CLAIM_KEY_USERNAME, userName);
		return doGenerateToken(claims, Constant.REFRESHTOKEN_LIFE_TIME);
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string
	private String doGenerateToken(Map<String, Object> claims, long expirationTime) {

		String token = Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
		return token;
	}

	// validate token
	public Boolean validateToken(String token, String userName) {
		final String userNameFromToken = getUsernameFromToken(token);
		System.out.println("getUsernameFromToken: " + userNameFromToken + " userDetails: " + userName);
		if (userNameFromToken.equals(userName) && !isTokenExpired(token)) {
			return true;
		}
		return false;
		// && !isTokenExpired(token));
	}
}

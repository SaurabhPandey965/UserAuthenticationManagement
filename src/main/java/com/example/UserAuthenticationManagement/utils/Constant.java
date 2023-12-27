package com.example.UserAuthenticationManagement.utils;

public class Constant {

	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final long ACCESSTOKEN_LIFE_TIME = 600000;
	public static final long REFRESHTOKEN_LIFE_TIME = 3600000;
	public static final String CLAIM_KEY_USERNAME = "sub";
	public static final String CLAIM_KEY_CREATED = "created";
	public static final String CLAIM_KEY_ROLES = "roles";
}

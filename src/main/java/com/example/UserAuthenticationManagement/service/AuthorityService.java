package com.example.UserAuthenticationManagement.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.UserAuthenticationManagement.dao.AuthorityDao;
import com.example.UserAuthenticationManagement.dto.AuthorityDto;
import com.example.UserAuthenticationManagement.entity.Authority;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthorityService {

	@Autowired
	AuthorityDao authorityDao;
	private ModelMapper modelMapper;

	
	public void findAllById(long id) {
		
	}
	public void saveAuthority(AuthorityDto authorityDto) throws Exception {

		Optional<Authority> authority = authorityDao.findById(authorityDto.getId());
		if(authority.isEmpty()) {
		Authority entity = mapDtoToAuthority(authorityDto);
		authorityDao.save(entity);
		}
		throw new Exception("Record allready Found");
	}

	public AuthorityDto mapToDto(final Authority authority, AuthorityDto authorityDto) {
	   return modelMapper.map(authority, authorityDto.getClass());
	}
	public Authority mapDtoToAuthority(AuthorityDto authorityDto) {

		Authority entity = modelMapper.map(authorityDto, Authority.class);
		return entity;
		//authorityDao.save(entity);
	}
}

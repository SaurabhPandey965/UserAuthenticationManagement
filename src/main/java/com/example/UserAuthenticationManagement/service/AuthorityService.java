package com.example.UserAuthenticationManagement.service;

import java.util.List;
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

	/*
	 * @Autowired private ModelMapper modelMapper;
	 */
	public void findAllById(long id) {

	}

	public void saveAuthority(AuthorityDto authorityDto)  {

		List<Authority> authority = authorityDao.findByCode(authorityDto.getCode());
		if (authority.isEmpty()) {
			Authority entity = mapDtoToAuthority(authorityDto);
			authorityDao.save(entity);
		}
		//throw new Exception("Record allready Found");
	}

	public AuthorityDto mapToDto(final Authority authority, AuthorityDto authorityDto) {

		authorityDto.setCode(authority.getCode());
		authorityDto.setDescription(authority.getDescription());
		return authorityDto;
		// modelMapper.map(authority, authorityDto.getClass());
	}

	public Authority mapDtoToAuthority(AuthorityDto authorityDto) {
              
		Authority entity = new Authority();
		entity.setCode(authorityDto.getCode());
		entity.setDescription(authorityDto.getDescription());
		//Authority entity = modelMapper.map(authorityDto, Authority.class);
		return entity;
		// authorityDao.save(entity);
	}
}

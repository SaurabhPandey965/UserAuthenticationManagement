package com.example.UserAuthenticationManagement.service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.UserAuthenticationManagement.dao.AuthorityDao;
import com.example.UserAuthenticationManagement.dao.RolesDao;
import com.example.UserAuthenticationManagement.dto.AuthorityDto;
import com.example.UserAuthenticationManagement.dto.RolesDto;
import com.example.UserAuthenticationManagement.entity.Authority;
import com.example.UserAuthenticationManagement.entity.Roles;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoleService {

	@Autowired
	AuthorityService authorityService;
	@Autowired
	AuthorityDao authorityDao;
	@Autowired
	RolesDao rolesDao;

	public long createRole(RolesDto rolesDto) {
		final Roles roles = new Roles();
		mapToEntity(rolesDto, roles);
		return rolesDao.save(roles).getId();
	}

	public List<RolesDto> findAllRoles() {
		return rolesDao.findAll().stream().map(roles -> mapToDto(new RolesDto(), roles)).toList();
		
	}

	public void deleteAllRole() {
		rolesDao.deleteAll();
	}

	public void deleteById(long id) {
		rolesDao.deleteById(id);
	}

	public RolesDto get(long id) {
		return rolesDao.findById(id).map(roles -> mapToDto(new RolesDto(), roles))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	public RolesDto mapToDto(RolesDto rolesDto, final Roles roles) {
		rolesDto.setId(roles.getId());
		rolesDto.setLevel(roles.getLevel());
		rolesDto.setName(roles.getName());
		if (roles.getAuthorities() != null) {
			rolesDto.setAuthorities(roles.getAuthorities().stream()
					.map(authority -> authorityService.mapToDto(authority, new AuthorityDto())).toList());
		}
		return rolesDto;
	}

	public void mapToEntity(RolesDto rolesDto, final Roles roles) {

		roles.setId(rolesDto.getId());
		roles.setName(rolesDto.getName());
		roles.setLevel(roles.getLevel());
		if (rolesDto.getAuthorities() != null) {
			List<Authority> authority = authorityDao
					.findAllById(rolesDto.getAuthorities().stream().map(a -> a.getId()).toList());
			if (authority.size() != rolesDto.getAuthorities().size()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "one of authority not found");
			}
			roles.setAuthorities(new HashSet<>(authority));
		}

	}

}

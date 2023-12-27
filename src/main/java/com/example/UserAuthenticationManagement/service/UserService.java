package com.example.UserAuthenticationManagement.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.UserAuthenticationManagement.dao.AuthorityDao;
import com.example.UserAuthenticationManagement.dao.RolesDao;
import com.example.UserAuthenticationManagement.dao.UserDao;
import com.example.UserAuthenticationManagement.dto.AuthorityDto;
import com.example.UserAuthenticationManagement.dto.RolesDto;
import com.example.UserAuthenticationManagement.dto.UserDto;
import com.example.UserAuthenticationManagement.entity.Authority;
import com.example.UserAuthenticationManagement.entity.Roles;
import com.example.UserAuthenticationManagement.entity.UserEntity;
import com.example.UserAuthenticationManagement.utils.Authorities;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

	@Value("${initializer.username}")
	private String adminUsername;
	@Value("${initializer.password}")
	private String adminPassword;
	@Autowired
	UserDao userDao;

	@Autowired
	RolesDao rolesDao;

	@Autowired
	RoleService roleService;

	@Autowired
	AuthorityDao authorityDao;
	@Autowired
	AuthorityService authorityService;

	// private final BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	// private ModelMapper modeMapper;

	public void syncAuthoritiesToDatabase() throws Exception {
		for (Field field : Authorities.class.getFields()) {
			Object target = new Object();
			String value = (String) field.get(target);

			AuthorityDto authorityDTO = new AuthorityDto();
			authorityDTO.setCode(value);
			authorityDTO.setDescription(value);

			authorityService.saveAuthority(authorityDTO);
		}
	}

	public void syncAuthoritiesToSuperAdmin() {
		Roles superAdmin = rolesDao.findByName("SUPER_ADMIN");
		if (superAdmin != null) {
			List<Authority> allAuthorities = authorityDao.findAll();
			superAdmin.setAuthorities(new HashSet<>(allAuthorities));
			rolesDao.save(superAdmin);
		}
	}

	public void createAdminIfNoExist() {
		List<UserEntity> adminUser = userDao.findUserByRole("SUPER_ADMIN");

		Roles adminRole = new Roles();
		if (adminUser.isEmpty()) {
			adminRole.setName("SUPER_ADMIN");
			adminRole.setLevel(0);
			Roles admin = rolesDao.findByName("SUPER_ADMIN");
			List<Authority> allAuthorities = authorityDao.findAll();
			if (admin != null) {
				adminRole = admin;
				if (adminRole.getAuthorities().size() < allAuthorities.size()) {
					adminRole.setAuthorities(new HashSet<>(allAuthorities));
					rolesDao.save(adminRole);
				}
			} else {
				adminRole.setAuthorities(new HashSet<>(allAuthorities));
				rolesDao.save(adminRole);

			}
			Optional<UserEntity> user = userDao.findByUserName(adminUsername);
			if (user.isEmpty()) {
				UserDto userDto = new UserDto();
				userDto.setUsername(adminUsername);
				userDto.setPassword(adminPassword);
				userDto.setFirstName("super");
				userDto.setLastName("admin");
				userDto.setEmail(adminUsername);

				userDto.setRoles(new ArrayList<>());
				userDto.getRoles().add(roleService.get(adminRole.getId()));
				createUser(userDto);

			}
		}

	}

	public Set<Roles> getRoleByUsername(String username) {
		Optional<UserEntity> user = userDao.findByUserName(username);
		if (user.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		Set<Roles> roles = user.get().getRoles();
		return roles;
	}

	public UserEntity createUser(UserDto userDto) {
		Optional<UserEntity> user = userDao.findByUserName(userDto.getUsername());
		if (user.isPresent()) {
			throw new RuntimeException("user already present");
		}
		// ModelMapper modeMapper = new ModelMapper();
		// UserEntity usr = modeMapper.map(userDto, UserEntity.class);
		// usr.setPassword(passwordEncoder.encode(usr.getPassword()));

		return userDao.save(mapDtoTOEntity(userDto));

	}

	public List<UserEntity> findAllByPageination(int pageNo, int pageSize, String sortByField) {

		PageRequest pageble = PageRequest.of(pageNo, pageSize, Sort.by(sortByField).ascending());
		Page<UserEntity> page = userDao.findAll(pageble);
		List<UserEntity> userList = page.getContent();
		return userList;
	}

	public UserDto getUserByUserName(String username) {
		Optional<UserEntity> user = userDao.findByUserName(username);
		if (user.isEmpty()) {
			throw new RuntimeException("Record not found for " + username);
		}
		// List<UserEntity> user = userDao.findAll();
		UserDto userDto = new UserDto();
       UserDto dto = mapToDto(userDto, user.get());
		return dto;

	}

	public List<UserEntity> getAllUser() {

		Optional<List<UserEntity>> user = Optional.ofNullable(userDao.findAll());
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

	public void updateUser(Integer id, UserDto userDto) {
		Optional<UserEntity> user = userDao.findById(id);
		if (user.isEmpty()) {
			throw new RuntimeException("user Not found for " + id);
		}
		UserEntity userEntity = user.get();
		// UserEntity userEntity = modeMapper.map(userDto, UserEntity.class);
		userEntity.setId(id);
		userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
		userEntity.setUserName(userDto.getFirstName());
		userEntity.setEmail(userDto.getEmail());
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		userEntity.setPhone(userDto.getPhone());
		// UserEntity userEntity = modeMapper.map(userDto, user.get().getClass());

		userDao.save(userEntity);
		log.info("user update succesfully...");
	}

	public void updateUserField(Integer id, String name) {

		Optional<UserEntity> user = userDao.findById(id);
		if (user.isPresent()) {
			UserEntity userEntity = user.get();
			userEntity.setUserName(name);

			userDao.save(userEntity);
			log.info("user updated succesfully " + name);
		} else {
			throw new RuntimeException("userNot found for" + id);

		}
	}

	public void deleteUser() {

		userDao.deleteAll();

	}

	public void deletuserById(int id) {
		userDao.deleteById(id);
		log.info("user deleted for :" + id);
	}

	public UserDto mapToDto(UserDto userDto, final UserEntity userEntity) {
      log.info("UserEntity: "+userEntity);
		userDto.setFirstName(userEntity.getFirstName());
		userDto.setLastName(userEntity.getLastName());
		userDto.setUsername(userEntity.getUserName());
		// userDto.setPassword(userEntity.getPassword());
		userDto.setEmail(userEntity.getEmail());
		userDto.setPhone(userEntity.getPhone());
		if (userEntity.getRoles() != null) {
			userDto.setRoles(
					userEntity.getRoles().stream().map(roles -> roleService.mapToDto(new RolesDto(), roles)).toList());
		}
		return userDto;
	}

	public UserEntity mapDtoTOEntity(UserDto userDto) {
		log.info("userDto: "+userDto);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(userDto.getId());
		userEntity.setEmail(userDto.getEmail());
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		userEntity.setUserName(userDto.getUsername());
		userEntity.setPhone(userDto.getPhone());
		userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
		if (userDto.getRoles() != null) {
			List<Roles> roles = rolesDao.findAllById(userDto.getRoles().stream().map(r -> r.getId()).toList());
			if (roles.size() != userDto.getRoles().size()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "one of role not found");
			}
			userEntity.setRoles(new HashSet<>(roles));
		}
		return userEntity;

	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserEntity> user = userDao.findByUserName(username);
		if (user.isEmpty()) {
			throw new UsernameNotFoundException("user not found");
		}
		UserDto userDto = mapToDto(new UserDto(), user.get());
		userDto.setPassword(user.get().getPassword());
		return userDto;
	}

}

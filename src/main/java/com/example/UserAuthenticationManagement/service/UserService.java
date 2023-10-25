package com.example.UserAuthenticationManagement.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.UserAuthenticationManagement.dao.UserDao;
import com.example.UserAuthenticationManagement.dto.UserDto;
import com.example.UserAuthenticationManagement.entity.UserEntity;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Autowired
	UserDao userDao;

	// private final BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private ModelMapper modeMapper;

	public UserEntity createUser(UserDto userDto) {
		Optional<UserEntity> user = userDao.findByUserName(userDto.getUserName());
		if (user.isPresent()) {
			throw new RuntimeException("user already present");
		}
		// ModelMapper modeMapper = new ModelMapper();
		UserEntity usr = modeMapper.map(userDto, UserEntity.class);
		usr.setPassword(passwordEncoder.encode(usr.getPassword()));

		return userDao.save(usr);

	}

	public List<UserEntity> findAllByPageination(int pageNo, int pageSize, String sortByField) {

		PageRequest pageble = PageRequest.of(pageNo, pageSize, Sort.by(sortByField).ascending());
		Page<UserEntity> page = userDao.findAll(pageble);
		List<UserEntity> userList = page.getContent();
		return userList;
	}

	public UserEntity getUserByUserName(String username) {
		Optional<UserEntity> user = userDao.findByUserName(username);
		if (user.isEmpty()) {
			throw new RuntimeException("Record not found for " + username);
		}
		// List<UserEntity> user = userDao.findAll();

		return user.get();

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
		userEntity.setUserName(userDto.getUserName());
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

	public UserEntity setDtoTOEntity(UserDto userDto) {
		UserEntity userEntity = new UserEntity();
		userEntity.setId(userDto.getId());
		userEntity.setEmail(userDto.getEmail());
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		userEntity.setUserName(userDto.getUserName());
		userEntity.setPhone(userDto.getPhone());
		userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
		return userEntity;

	}

}

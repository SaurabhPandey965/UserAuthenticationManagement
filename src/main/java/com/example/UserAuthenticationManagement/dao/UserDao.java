package com.example.UserAuthenticationManagement.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.UserAuthenticationManagement.entity.UserEntity;

import java.util.List;
import java.util.Optional;



@Repository
public interface UserDao extends JpaRepository<UserEntity, Integer> {

	  
    //UserEntity findByUserName(String userName);
	 Optional<UserEntity> findByUserName(String userName);
	 
	 

}

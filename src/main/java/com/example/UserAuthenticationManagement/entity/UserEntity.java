package com.example.UserAuthenticationManagement.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.print.attribute.standard.DateTimeAtCreation;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Data

public class UserEntity extends DateAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String userName;
	private String Password;
	private String firstName;
	private String lastName;
	private String phone;
	private String email;

}

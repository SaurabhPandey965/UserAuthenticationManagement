package com.example.UserAuthenticationManagement.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "role_id", referencedColumnName = "id") })
	// @JoinColumn(name = "user_role_id", referencedColumnName = "user_id")
	Set<Roles> roles;

}

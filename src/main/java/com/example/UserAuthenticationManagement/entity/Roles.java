package com.example.UserAuthenticationManagement.entity;

import java.util.Set;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Roles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private Integer level;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)

	@JoinTable(name = "role_authorities", joinColumns = {
			@JoinColumn(name = "role_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "authority_id", referencedColumnName = "id") })

	// @JoinColumn(name = "role_authority_id", referencedColumnName = "role_id")
	private Set<Authority> authorities;

	@ManyToMany(mappedBy = "roles")
	Set<UserEntity> users;

}

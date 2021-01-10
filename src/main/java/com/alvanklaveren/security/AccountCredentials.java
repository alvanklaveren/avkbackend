package com.alvanklaveren.security;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class AccountCredentials {

	private String username;
	private String password;
}
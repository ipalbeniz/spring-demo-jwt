package com.example.service;

import com.example.model.Permission;
import com.example.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

	public Optional<User> getByUsername(String username) {

		User user = new User();

		user.setUsername(username);
		user.setPermissions(Stream.of(Permission.READ_MESSAGE)
				.collect(Collectors.toSet()));

		return Optional.of(user);
	}
}

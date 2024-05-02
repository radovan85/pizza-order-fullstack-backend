package com.radovan.spring.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.radovan.spring.dto.UserDto;

public interface UserService {

	void deleteUser(Integer id);

	UserDto getUserById(Integer id);

	List<UserDto> listAllUsers();

	UserDto getUserByEmail(String email);

	UserDto getCurrentUser();

	void suspendUser(Integer userId);

	void clearSuspension(Integer userId);

	Boolean isAdmin();

	Optional<Authentication> authenticateUser(String username, String password);

}

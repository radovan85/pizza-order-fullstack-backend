package com.radovan.spring.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repository.RoleRepository;
import com.radovan.spring.repository.UserRepository;
import com.radovan.spring.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	@Transactional
	public void deleteUser(Integer id) {
		// TODO Auto-generated method stub
		Optional<UserEntity> userOptional = userRepository.findById(id);
		if (userOptional.isPresent()) {
			userRepository.deleteById(id);
			userRepository.flush();
		} else {
			Error error = new Error("The user has not been found!");
			throw new InstanceUndefinedException(error);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getUserById(Integer id) {
		// TODO Auto-generated method stub
		UserDto returnValue = null;
		Optional<UserEntity> userOptional = userRepository.findById(id);
		if (userOptional.isPresent()) {
			returnValue = tempConverter.userEntityToDto(userOptional.get());
		} else {
			Error error = new Error("The user has not been found!");
			throw new InstanceUndefinedException(error);
		}

		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> listAllUsers() {
		// TODO Auto-generated method stub
		List<UserDto> returnValue = new ArrayList<>();
		List<UserEntity> allUsers = userRepository.findAll();
		if (!allUsers.isEmpty()) {
			allUsers.forEach((userEntity) -> {
				UserDto userDto = tempConverter.userEntityToDto(userEntity);
				returnValue.add(userDto);
			});
		}

		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getUserByEmail(String email) {
		return userRepository.findByEmail(email).map(tempConverter::userEntityToDto)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("Invalid user!")));
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getCurrentUser() {
		// TODO Auto-generated method stub
		UserDto returnValue = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUsername = authentication.getName();
			Optional<UserEntity> userOptional = userRepository.findByEmail(currentUsername);
			if (userOptional.isPresent()) {
				returnValue = tempConverter.userEntityToDto(userOptional.get());
			} else {
				Error error = new Error("Invalid user!");
				throw new InstanceUndefinedException(error);
			}
		} else {
			Error error = new Error("Invalid user!");
			throw new InstanceUndefinedException(error);
		}

		return returnValue;
	}

	@Override
	@Transactional
	public void suspendUser(Integer userId) {
		// TODO Auto-generated method stub
		UserDto userDto = getUserById(userId);
		UserEntity userEntity = tempConverter.userDtoToEntity(userDto);
		userEntity.setEnabled((byte) 0);
		userRepository.saveAndFlush(userEntity);

	}

	@Override
	@Transactional
	public void clearSuspension(Integer userId) {
		// TODO Auto-generated method stub
		UserDto userDto = getUserById(userId);
		UserEntity userEntity = tempConverter.userDtoToEntity(userDto);
		userEntity.setEnabled((byte) 1);
		userRepository.saveAndFlush(userEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean isAdmin() {
		// TODO Auto-generated method stub
		Boolean returnValue = false;
		UserDto currentUser = getCurrentUser();
		RoleEntity roleAdmin = roleRepository.findByRole("ADMIN");
		List<Integer> rolesIds = currentUser.getRolesIds();
		if (rolesIds.contains(roleAdmin.getId())) {
			returnValue = true;
		}

		return returnValue;
	}

	@Override
	public Optional<Authentication> authenticateUser(String username, String password) {
		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
		Optional<UserEntity> userOptional = userRepository.findByEmail(username);
		return userOptional.flatMap(user -> {
			try {
				Authentication auth = authenticationManager.authenticate(authReq);
				return Optional.of(auth);
			} catch (AuthenticationException e) {
				// Handle authentication failure
				return Optional.empty();
			}
		});
	}

}

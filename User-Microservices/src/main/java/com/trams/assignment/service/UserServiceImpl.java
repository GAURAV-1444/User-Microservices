package com.trams.assignment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trams.assignment.dto.LoginRequest;
import com.trams.assignment.dto.LoginResponse;
import com.trams.assignment.dto.UserRequest;
import com.trams.assignment.dto.UserResponse;
import com.trams.assignment.entity.User;
import com.trams.assignment.event.UserCreatedEvent;
import com.trams.assignment.exception.EmailAlreadyExistsException;
import com.trams.assignment.exception.UserNotFoundException;
import com.trams.assignment.messaging.UserEventPublisher;
import com.trams.assignment.repository.UserRepository;
import com.trams.assignment.security.JwtService;
import com.trams.assignment.exception.InvalidCredentialsException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserEventPublisher userEventPublisher;
	private final JwtService jwtService;

	@Override
	public UserResponse registerUser(UserRequest request) {

		if (userRepository.existsByEmail(request.getEmail())) {

			throw new EmailAlreadyExistsException("Email already registered");
		}

		User user = new User();

		user.setName(request.getName());
		user.setEmail(request.getEmail());

		user.setPassword(passwordEncoder.encode(request.getPassword()));

		user.setRole("USER");

		User savedUser = userRepository.save(user);

		UserCreatedEvent event = new UserCreatedEvent(savedUser.getId(), savedUser.getName(), savedUser.getEmail());

		userEventPublisher.publishUserCreated(event);

		return mapToResponse(savedUser);
	}

	@Override
	public UserResponse getUserById(Long id) {

		User user = userRepository.findUserById(id);

		if (user == null) {

			throw new UserNotFoundException("User not found with id: " + id);
		}

		return mapToResponse(user);
	}

	@Override
	public List<UserResponse> getAllUsers() {

		List<User> users = userRepository.findAll();

		List<UserResponse> responses = new ArrayList<>();

		for (User user : users) {

			responses.add(mapToResponse(user));
		}

		return responses;
	}

	@Override
	public UserResponse updateUser(Long id, UserRequest request) {

		User user = userRepository.findUserById(id);

		if (user == null) {

			throw new UserNotFoundException("User not found with id: " + id);
		}

		if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {

			throw new EmailAlreadyExistsException("Email already registered");
		}

		user.setName(request.getName());
		user.setEmail(request.getEmail());

		user.setPassword(passwordEncoder.encode(request.getPassword()));

		User updatedUser = userRepository.save(user);

		return mapToResponse(updatedUser);
	}

	@Override
	public void deleteUser(Long id) {

		User user = userRepository.findUserById(id);

		if (user == null) {

			throw new UserNotFoundException("User not found with id: " + id);
		}

		userRepository.delete(user);
	}

	@Override
	public LoginResponse loginUser(LoginRequest request) {

		User user = userRepository.findByEmail(request.getEmail());

		if (user == null) {

			throw new InvalidCredentialsException(
			        "Invalid email or password"
			);
		}

		boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!passwordMatches) {

			throw new InvalidCredentialsException(
			        "Invalid email or password"
			);
		}

		String token = jwtService.generateToken(user.getEmail());

		return new LoginResponse(token, user.getEmail());
	}

	private UserResponse mapToResponse(User user) {

		return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
	}
}
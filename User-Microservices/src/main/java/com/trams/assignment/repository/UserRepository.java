package com.trams.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trams.assignment.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

	User findUserById(Long id);
	
    boolean existsByEmail(String email);
}
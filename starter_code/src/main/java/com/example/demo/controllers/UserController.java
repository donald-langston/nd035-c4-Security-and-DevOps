package com.example.demo.controllers;

import java.util.Optional;

import antlr.ASTFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.*;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import org.apache.logging.log4j.*;


@RestController
@RequestMapping("/api/user")
public class UserController {

	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	//Logger log = LoggerFactory.getLogger(UserController.class);
	Logger log = LogManager.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		if(createUserRequest.getPassword().length() < 7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {

			log.error("Error with user password. Cannot create user {}", createUserRequest.getUsername());
			//System.out.println("Error with user password. Cannot create user " + createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		userRepository.save(user);
		log.info("Created user {} successfully", createUserRequest.getUsername());
		return ResponseEntity.ok(user);
	}
	
}

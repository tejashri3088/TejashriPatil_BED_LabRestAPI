package com.greatlearning.config;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.github.javafaker.Faker;
import com.greatlearning.entity.Role;
import com.greatlearning.entity.Student;
import com.greatlearning.entity.User;
import com.greatlearning.repository.StudentRepository;
import com.greatlearning.repository.UserRepository;

@Configuration
public class BootstrapAppData {

	@Bean
	public RestTemplate resTemplate() {
		return new RestTemplate();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Faker faker = new Faker();

	@EventListener(ApplicationReadyEvent.class)
	public void addStudents(ApplicationReadyEvent event) {
		for (int i = 0; i < 5; i++) {
			Student student = new Student();
			student.setFirstName(faker.name().firstName());
			student.setLastName(faker.name().lastName());
			student.setCourse(faker.name().title());
			student.setCountry(faker.country().name());
			this.studentRepository.saveAndFlush(student);
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	public void addUserAndRoles(ApplicationReadyEvent event) {

		User nikita = new User();
		User manoj = new User();

		nikita.setUsername("nikita");
		nikita.setPassword(this.passwordEncoder.encode("admin"));

		manoj.setUsername("manoj");
		manoj.setPassword(this.passwordEncoder.encode("user"));

		Role userRole = new Role();
		Role adminRole = new Role();

		userRole.setName("ROLE_USER");
		adminRole.setName("ROLE_ADMIN");

		nikita.addRole(userRole);
		nikita.addRole(adminRole);

		manoj.addRole(userRole);

		this.userRepository.save(nikita);
		this.userRepository.save(manoj);
	}
}

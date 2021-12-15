package com.example.demo

import com.example.demo.dtos.UserDto
import com.example.demo.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DemoApplicationTests {

	@Autowired var userService :UserService? = null

	@Test
	fun contextLoads() {
		userService?.saveUser(UserDto("argaman", "arg12345"))
	}

}
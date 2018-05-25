package com.digitala.galoshes;

import com.digitala.galoshes.category.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {


	@Autowired CategoryService categoryService;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		// DebitCardService myDbCardService = new DebitCardService();
		 //myDbCardService.testDatabaseConnection();

		System.out.println("Hello, Ian");
	}
}

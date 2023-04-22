package com.example.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineStoreApplication {

	public static int minus(int x1, int x2){
		System.out.println(x1);
		System.out.println(x2);
		return x1-x2;
	}

	public static void main(String[] args) {

		SpringApplication.run(OnlineStoreApplication.class, args);
	}

}

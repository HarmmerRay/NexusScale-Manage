package com.nexuscale.nexusscalemanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nexuscale.nexusscalemanage.dao")
public class NexusScaleManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(NexusScaleManageApplication.class, args);
	}

}

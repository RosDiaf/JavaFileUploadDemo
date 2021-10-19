package com.example.myIntelliJDemo;

import com.example.myIntelliJDemo.Model.StorageProperties;
import com.example.myIntelliJDemo.Service.FilesStorageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class MyIntelliJDemoApplication implements CommandLineRunner {

	@Resource
	FilesStorageService storageService;

	public static void main(String[] args) {

		SpringApplication.run(MyIntelliJDemoApplication.class, args);

		int i=0;

		String filename="result.csv";

		Path pathToFile = Paths.get(filename);

		System.out.println(pathToFile.toAbsolutePath());
	}

	@Override
	public void run(String... arg) throws Exception {
		storageService.deleteAll();
		storageService.init();
	}
}
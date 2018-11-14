package com.example.webcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

/**
 * Main class responsible for running the web cache service.
 * The 'webcache.maxSize' parameter can be specified as a command line argument (--webcache.maxSize=N),
 * otherwise the default cache max size is used (defined in the application.properties file).
 */
@SpringBootApplication
public class WebCacheApplication implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebCacheApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WebCacheApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<String> maxSizeParam = args.getOptionValues("webcache.maxSize");
		if (maxSizeParam != null && maxSizeParam.size() == 1) {
			LOGGER.info("Running a WebCache with capacity of {}", maxSizeParam.get(0));
		} else {
			LOGGER.info("Running a WebCache with the default capacity");
		}
	}

}

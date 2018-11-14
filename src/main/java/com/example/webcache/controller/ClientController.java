package com.example.webcache.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This class is responsible for displaying a simple JavaScript-based webcache client page.
 */
@Controller
public class ClientController {

	@RequestMapping("/client")
	public String showClient() {
		return "client.html";
	}

}

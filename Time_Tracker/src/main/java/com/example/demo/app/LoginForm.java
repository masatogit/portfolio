package com.example.demo.app;

import jakarta.validation.constraints.NotNull;


public class LoginForm {

	
	@NotNull(message = "空白です")
	private String id;
	
	@NotNull(message = "空白です")
	private String pass;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

}

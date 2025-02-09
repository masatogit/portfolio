package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserForm {

	@Pattern(regexp = "^[a-zA-Z0-9]{4,6}$", message = "idは半角英数字で4～6文字で入力してください")
	@NotBlank(message = "idは必須項目です")
	private String id;

	@Pattern(regexp = "^[a-zA-Z0-9]{4,6}$", message = "passは半角英数字で4～6文字で入力してください")
	@NotBlank(message = "passは必須項目です")
	private String pass;

	// デフォルトコンストラクタ
	public UserForm() {
	}

	// Getters and Setters
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

package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginForm {

	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "半角英数字で入力してください")
	@NotBlank(message = "idは必須項目です")
	@Size(min = 4, max = 6, message = "idは4文字以上6文字以下で入力してください")
	private String id;

	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "半角英数字で入力してください")
	@NotBlank(message = "passは必須項目です")
	@Size(min = 4, max = 6, message = "passは4文字以上6文字以下で入力してください")
	private String pass;

	// getter,setter
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

	// デフォルトコンストラクタ
	public LoginForm() {

	}

}

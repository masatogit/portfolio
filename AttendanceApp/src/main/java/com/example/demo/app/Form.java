package com.example.demo.app;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Form {
	
	
	//フィールドの作成
	private int id;
	
	
	@Min(value = 0, message = "0以上の値を入力してください")
    @Max(value = 999999999, message = "999999999以下の値を入力してください")
	@NotNull(message = "入力してください")
	private  int commuting_allowance;

	
	//getter,setterの作成
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCommuting_allowance() {
		return commuting_allowance;
	}

	public void setCommuting_allowance(int commuting_allowance) {
		this.commuting_allowance = commuting_allowance;
	}
	
	
	//デフォルトコンストラクタの作成
	public Form() {
	
	}
	
}

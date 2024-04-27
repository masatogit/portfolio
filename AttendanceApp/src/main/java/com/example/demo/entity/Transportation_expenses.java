package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Transportation_expenses {
	
	
	//フィールドの作成
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	//private int commuting_allowance;
	private int commuting_allowance;
	
	
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
	public Transportation_expenses() {
	}
	

}

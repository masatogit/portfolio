package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Join {
	
	
	//フィールド
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private int commuting_allowance;
	
	
	//getter,setter
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	public int getCommuting_allowance() {
		return commuting_allowance;
	}
	public void setCommuting_allowance(int commuting_allowance) {
		this.commuting_allowance = commuting_allowance;
	}
	
	//デフォルトコンストラクタ
	public Join() {
		super();
	}
	
	
	
	

}

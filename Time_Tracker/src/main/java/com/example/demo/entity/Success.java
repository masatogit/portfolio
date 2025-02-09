package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "success")
public class Success {

	// フィールド
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime checkInTime;

	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime checkOutTime;

	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime breakTime;

	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime workingTime;

	private String transportation_expenses;

	private String remarks;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// デフォルトコンストラクタ
	public Success() {
	}

	// getter&setter
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(LocalTime checkInTime) {
		this.checkInTime = checkInTime;
	}

	public LocalTime getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(LocalTime checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public LocalTime getBreakTime() {
		return breakTime;
	}

	public void setBreakTime(LocalTime breakTime) {
		this.breakTime = breakTime;
	}

	public LocalTime getWorkingTime() {
		return workingTime;
	}

	public void setWorkingTime(LocalTime workingTime) {
		this.workingTime = workingTime;
	}

	public String getTransportation_expenses() {
		return transportation_expenses;
	}

	public void setTransportation_expenses(String transportation_expenses) {
		this.transportation_expenses = transportation_expenses;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}

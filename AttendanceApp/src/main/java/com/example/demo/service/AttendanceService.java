package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entity.Join;

public interface AttendanceService {
	
	
	//出勤処理
	public void clockIn(LocalDateTime time);
	
	//退勤処理
	public void clockOut(LocalDateTime time);
	
	//交通費処理
	public void Transportation_expenses(int commuting_allowance);
	
	//勤怠状況一覧取得
	public List<Join> search();
	

}

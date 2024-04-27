package com.example.demo.repository;

import java.util.List;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.Join;
import com.example.demo.entity.Transportation_expenses;

public interface AttendanceDao {
	
	
	//出退勤時間の登録処理
	public void saveAttendance(Attendance attendance);
	
	
	//交通費登録処理
	public void saveTransportation_expenses(Transportation_expenses transportation_expenses);
	
	
	//勤怠状況取り出し
	public List<Join> search();
	
	
}

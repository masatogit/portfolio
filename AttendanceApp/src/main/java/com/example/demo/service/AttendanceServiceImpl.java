package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.Join;
import com.example.demo.entity.Transportation_expenses;
import com.example.demo.repository.AttendanceDao;

@Service
public class AttendanceServiceImpl implements AttendanceService {

	@Autowired
	private AttendanceDao attendanceDao;
	
	public AttendanceServiceImpl(AttendanceDao attendanceDao) {
        this.attendanceDao = attendanceDao;
	}
	
	//出勤処理
	@Override
	public void clockIn(LocalDateTime time) {
		Attendance attendance = new Attendance();
        attendance.setStartTime(time);
        attendanceDao.saveAttendance(attendance);
	}

	//退勤処理
	@Override
	public void clockOut(LocalDateTime time) {
		Attendance attendance = new Attendance();
        attendance.setEndtime(time);
        attendanceDao.saveAttendance(attendance);

	}

	
	//交通費処理
	@Override
	public void Transportation_expenses(int commuting_allowance) {
	
		Transportation_expenses transportation_expenses = new Transportation_expenses();
		
		try {
			
			//交通費がnullでないことを確認
			Objects.requireNonNull(commuting_allowance, "交通費が入力されていません");
			
			
		} catch (NullPointerException e) {
			
			//null値が渡された場合のエラーハンドリング
			throw new IllegalArgumentException("交通費が入力されていません", e);
		} catch (IllegalArgumentException e) {
			
			//不正な値が渡された場合のエラーハンドリング
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
		
		transportation_expenses.setCommuting_allowance(commuting_allowance);
		attendanceDao.saveTransportation_expenses(transportation_expenses);
	}

	
	//勤怠管理一覧取得
	@Override
	public List<Join> search() {
		return attendanceDao.search();
	}

}

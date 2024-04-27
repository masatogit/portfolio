package com.example.demo.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.Join;
import com.example.demo.entity.Transportation_expenses;



@Repository
public class AttendanceDaoImpl implements AttendanceDao {

	private final JdbcTemplate jdbcTemplate;

	public AttendanceDaoImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	//出退勤時間の登録処理
	@Override
	public void saveAttendance(Attendance attendance) {
		String sql = "INSERT INTO attendance (startTime, endTime) VALUES(?, ?)";
		jdbcTemplate.update(sql, attendance.getStartTime(), attendance.getEndtime());
	}

	//交通費の登録処理・・・commuting_allowanceは通勤手当の意味
	@Override
	public void saveTransportation_expenses(Transportation_expenses transportation_expenses) {
		String sql = "INSERT INTO transportation_expenses (commuting_allowance) VALUE(?)";
		jdbcTemplate.update(sql, transportation_expenses.getCommuting_allowance());

	}

	@Override
	public  List<Join> search() {
		String sql = "SELECT attendance.id, attendance.startTime, attendance.endTime, transportation_expenses.commuting_allowance " +
	             "FROM attendance INNER JOIN transportation_expenses ON attendance.id = transportation_expenses.id";

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

		List<Join> list = new ArrayList<>();

		for (Map<String, Object> obj : resultList) {
			Join join = new Join();
			//idを取得
			join.setId((int) obj.get("id"));
			
			//出勤時間の取得とnullチェック
			Timestamp startTimeTimestamp = (Timestamp) obj.get("startTime");
			LocalDateTime startTime = startTimeTimestamp != null ? startTimeTimestamp.toLocalDateTime() : null;
			join.setStartTime(startTime);
			
			//退勤時間の取得とnullチェック
			Timestamp endTimeTimesstamp = (Timestamp) obj.get("endTime");
			LocalDateTime endTime = endTimeTimesstamp != null ? endTimeTimesstamp.toLocalDateTime() : null;
			join.setEndTime(endTime);
			
			//交通費を取得
			join.setCommuting_allowance((int) obj.get("commuting_allowance"));
			list.add(join);
		}
		return list;

	}

}

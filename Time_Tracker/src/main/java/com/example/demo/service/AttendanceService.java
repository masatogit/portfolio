package com.example.demo.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
//import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Success;
import com.example.demo.repository.AttendanceRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class AttendanceService {

	@Autowired
	private AttendanceRepository attendanceRepository;

	// 勤怠登録処理
	public boolean saveAttendance(Success attendance) {
		
		
		
		
		try {
			// 登録処理
			attendanceRepository.save(attendance);
			return true;
		} catch (Exception e) {
			// エラー内容をコンソール上に出力
			e.printStackTrace();
			// エラー発生時
			return false;
		}
	}

	// 勤怠一覧取得処理
	public List<Success> getAttendancesByCurrentMonth(HttpSession session) {
		LocalDate currentDate = LocalDate.now();
		LocalDate startDate = currentDate.withDayOfMonth(1);
		LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

		String loggedInUserId = (String) session.getAttribute("id");

		// 全勤怠データを取得（期間を指定）
		List<Success> attendances = attendanceRepository.findByDateBetween(startDate, endDate);

		// ログインユーザーの勤怠データのみを格納するリスト
		List<Success> loginUserId = new ArrayList<>();

		// 勤怠データからログインユーザーのデータのみ抽出
		for (Success attendance : attendances) {
			// attendance.getUser():勤怠データに紐づくユーザー情報を取得
			// attendance.getUser() != null:ユーザー情報がnullではないか確認
			// attendance.getUser().getId().equals(loggedInUserId):勤怠データのユーザーidとログイン中のユーザーid（loggedInUserId）が一致しているか確認
			if (attendance.getUser() != null && attendance.getUser().getId().equals(loggedInUserId)) {

				// 条件に一致したデータ（ログインユーザーの勤怠データ）をリストloginUserIdに追加
				loginUserId.add(attendance);
			}
		}

		// 選び出したデータに対して勤務時間の計算
		List<Success> result = new ArrayList<>();

		for (Success attendance : loginUserId) {
			Success calculatedAttendance = calculateWorkTime(attendance);
			result.add(calculatedAttendance);
		}
		return result;
	}

	// 勤怠の合計稼働時間を計算
	public Duration calculateTotalWorkingTime(List<Success> attendances) {

		// 合計時間の初期化
		Duration totalDuration = Duration.ZERO;

		// 出勤記録のリストをループ処理
		for (Success attendance : attendances) {
			// 勤務時間がnullでないかチェック
			if (attendance.getWorkingTime() != null) {
				// 勤務時間をDurationに変換
				Duration workingTimeDuration = Duration.between(LocalTime.MIN, attendance.getWorkingTime());
				// 合計に加算
				totalDuration = totalDuration.plus(workingTimeDuration);
			}
		}

		// 合計の勤務時間を返す
		return totalDuration;
	}

	// DurationをLocalTimeに変換するメソッド
	public LocalTime calculateTotalWorkingLocalTime(List<Success> attendances) {
		Duration totalWorkingTime = calculateTotalWorkingTime(attendances);
		// DurationをLocalTimeに変換
		return LocalTime.of((int) (totalWorkingTime.toHours() % 24), (int) totalWorkingTime.toMinutes() % 60);

	}

	// 合計稼働時間を時間と分で返すメソッド
	public Map<String, Integer> getTotalWorkingTimeComponents(List<Success> attendances) {
		Duration totalWorkingTime = calculateTotalWorkingTime(attendances);
		return Map.of("hours", (int) totalWorkingTime.toHours(), "minutes", (int) totalWorkingTime.toMinutes() % 60);
	}

	// 勤務時間を計算するメソッド
	private Success calculateWorkTime(Success success) {

		LocalTime checkInTime = success.getCheckInTime();
		LocalTime checkOutTime = success.getCheckOutTime();
		LocalTime breakTime = success.getBreakTime();
		LocalDate date = success.getDate();

		// LocalDateTimeに変換し、日付情報を含めた計算処理
		LocalDateTime checkInDateTime = LocalDateTime.of(date, checkInTime);

		// 退勤時間が出勤時間よりも早い場合日を跨ぐ計算処理
		LocalDateTime checkOutDateTime;

		if (checkOutTime.isBefore(checkInTime)) {
			checkOutDateTime = LocalDateTime.of(date.plusDays(1), checkOutTime);
		} else {
			checkOutDateTime = LocalDateTime.of(date, checkOutTime);
		}

		Duration workDuration = Duration.between(checkInDateTime, checkOutDateTime);
		Duration breakDuration = Duration.between(LocalTime.MIN, breakTime); // 休憩時間をDurationに変換
		Duration actualWorkTime = workDuration.minus(breakDuration);

		// 計算した労働時間をSuccessオブジェクトに設定
		LocalTime workingTime = LocalTime.of((int) actualWorkTime.toHours(), (int) actualWorkTime.toMinutes() % 60);
		success.setWorkingTime(workingTime); // LocalTime型の値を設定

		return success;

	}

	// 対象日付を検索し編集する処理
	public Optional<Success> findByDate(LocalDate date, String userId) {
		return attendanceRepository.findByDateAndUserId(date, userId);
	}

	public boolean update(Success success, String userId) {
		try {
			// 1. 日付とユーザーIDで対象データを検索
			Optional<Success> attendanceOpt = attendanceRepository.findByDateAndUserId(success.getDate(), userId);

			// 2. 同じ日付で別のIDが存在するかチェック（重複チェック）
			Optional<Success> duplicateOpt = attendanceRepository.findByDateAndUserId(success.getDate(), userId);
			if (duplicateOpt.isPresent() && duplicateOpt.get().getId() != success.getId()) {
				// 同じ日付で異なるデータが存在する場合は更新せず false を返す
				//return false;
			}

			if (attendanceOpt.isPresent()) {
				Success existingAttendance = attendanceOpt.get();

				// 3. データの更新
				existingAttendance.setCheckInTime(success.getCheckInTime());
				existingAttendance.setCheckOutTime(success.getCheckOutTime());
				existingAttendance.setBreakTime(success.getBreakTime());
				existingAttendance.setRemarks(success.getRemarks());
				existingAttendance.setTransportation_expenses(success.getTransportation_expenses());

				// 4. 更新データを保存
				attendanceRepository.save(existingAttendance);
				return true;
			} else {
				return false; // データが見つからなかった場合
			}
		} catch (Exception e) {
			// エラー内容をコンソール上に出力
			e.printStackTrace();
			// エラー発生時
			return false;
		}
	}

}

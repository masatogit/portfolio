package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Success;

public interface AttendanceRepository extends JpaRepository<Success, String> {

	// 勤怠一覧取得処理
	List<Success> findByDateBetween(LocalDate startDate, LocalDate endDate);

	// 対象の日付とユーザーidを検索し編集する処理
	Optional<Success> findByDateAndUserId(LocalDate date, String userId);

}

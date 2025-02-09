package com.example.demo.app;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Success;
import com.example.demo.entity.User;
import com.example.demo.form.LoginForm;
import com.example.demo.form.UserForm;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AppController {

	@Autowired
	private UserService userService;

	@Autowired
	private AttendanceService attendanceService;

	// ログイン画面アクセス処理
	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("title", "勤怠管理");
		model.addAttribute("loginForm", new LoginForm());
		return "login";
	}

	// ログイン判定処理&success画面(一覧画面)遷移処理
	@PostMapping("/login")
	public String login(@ModelAttribute @Validated LoginForm loginForm, BindingResult bindingResult, Model model, HttpSession session) {

		if (bindingResult.hasErrors()) {
	        // エラーメッセージをモデルに追加
	        model.addAttribute("error", "入力内容に誤りがあります。");
	        return "login"; // 再度ログイン画面に戻る
	    }
		
		boolean isAuthenticated = userService.authenticate(loginForm.getId(), loginForm.getPass());

		// ログイン成功時
		if (isAuthenticated) {

			// 当月の勤怠データ取得
			List<Success> attendances = attendanceService.getAttendancesByCurrentMonth(session);

			// セッション管理（id,pass,attendances）viewに渡す
			session.setAttribute("attendances", attendances);
			session.setAttribute("id", loginForm.getId());
			session.setAttribute("pass", loginForm.getPass());

			// 当月の取得し、セッションでviewへ年月の情報を渡す
			LocalDate currentDate = LocalDate.now();
			session.setAttribute("Year", currentDate.getYear());
			session.setAttribute("Month", currentDate.getMonthValue());

			return "redirect:success";

			// ログイン失敗時
		} else {
			model.addAttribute("title", "勤怠管理");
			model.addAttribute("error", "ログインIDまたはパスワードが正しくありません");
			return "login";
		}
	}

	// formから戻る処理
	@GetMapping("/success")
	public String back(Model model, HttpSession session) {

		// 勤怠データの取得
		List<Success> attendances = attendanceService.getAttendancesByCurrentMonth(session);
		Map<String, Integer> totalWorkingTime = attendanceService.getTotalWorkingTimeComponents(attendances);
		
		// 時間・分をModelにセット
		model.addAttribute("totalWorkingHours", totalWorkingTime.get("hours"));
		model.addAttribute("totalWorkingMinutes", totalWorkingTime.get("minutes"));

		if (attendances == null) {
			attendances = attendanceService.getAttendancesByCurrentMonth(session);
		}

		// セッションからIDとパスワードを取得
		String id = (String) session.getAttribute("id");
		String pass = (String) session.getAttribute("pass");

		model.addAttribute("id", id);
		model.addAttribute("pass", pass);

		// 日付取得
		LocalDate currentDate = LocalDate.now();
		int Year = currentDate.getYear();
		int Month = currentDate.getMonthValue();

		// viewに受け渡し
		model.addAttribute("Year", Year);
		model.addAttribute("Month", Month);
		model.addAttribute("attendances", attendances);

		return "success";
	}

	// 新規登録画面表示処理
	@GetMapping("/register")
	public String showRegistrationForm(UserForm userForm, Model model) {
		
		model.addAttribute("user", new UserForm());
		model.addAttribute("title", "勤怠管理 - 新規登録");
		return "register";
	}

	// アカウント新規登録処理
	@PostMapping("/register")
	public String register(@ModelAttribute @Validated UserForm userForm, BindingResult bindingResult, Model model) {
		
		// 入力チェック
		if (bindingResult.hasErrors()) {
	        // エラーメッセージをモデルに追加
	        model.addAttribute("error", "入力内容に誤りがあります。");
	        return "register"; // 再度登録画面に戻る
	    }
		
		 // フォームデータをEntityに変換
	    User user = new User();
	    user.setId(userForm.getId());
	    user.setPass(userForm.getPass());

		// アカウント登録処理
		boolean isRegistered = userService.register(user);

		// 登録成功
		if (isRegistered) {
			return "redirect:/login";
			// 登録失敗
		} else {
			model.addAttribute("error", "登録に失敗しました。再度お試しください。");
			return "register"; // 再度登録画面に戻る 
		}
	}

	// 入力画面処理
	@PostMapping("/form")
	public String form(@ModelAttribute Success success, Model model, HttpSession session) {

		// セッションからIDとパスワードを取得
		String id = (String) session.getAttribute("id");
		String pass = (String) session.getAttribute("pass");

		// Modelにidとpassを追加
		model.addAttribute("id", id);
		model.addAttribute("pass", pass);

		return "form";
	}

	// 勤怠情報の登録処理
	@PostMapping("/attendanceRegister")
	
	// 入力パラメータを設定
	public String registerAttendance(HttpSession session,
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam("checkInTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalTime checkInTime,
			@RequestParam("checkOutTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalTime checkOutTime,
			@RequestParam("breakTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalTime breakTime,
			@RequestParam("remarks") String remarks,
			@RequestParam("transportation_expenses") String transportation_expenses,
			RedirectAttributes redirectAttributes, Model model) {

		// 休憩時間の範囲を設定（0から2時間以内）
		if (breakTime.isBefore(LocalTime.of(0, 0)) || breakTime.isAfter(LocalTime.of(2, 0))) {
			redirectAttributes.addFlashAttribute("errorTime", "休憩時間は0時間から2時間の範囲で入力してください。");
			return "redirect:/form";
		}

		try {
			// セッションからIDを取得
			String userId = (String) session.getAttribute("id");
			// UserService.javaからセッションに保存されているidを取得し、変数loginUserIdに格納
			User loginUserId = userService.findById(userId);

			// Successオブジェクトを作成して、フォームのデータをセットする
			Success attendance = new Success();
			attendance.setDate(date);// 日付をセット
			attendance.setCheckInTime(checkInTime); // 出勤時間をセット
			attendance.setCheckOutTime(checkOutTime); // 退勤時間をセット
			attendance.setBreakTime(breakTime); // 休憩時間をセット
			attendance.setTransportation_expenses(transportation_expenses); // 交通費をセット
			attendance.setRemarks(remarks); // 備考をセット
			attendance.setUser(loginUserId); // ログイン中のユーザー情報をセット

			// セットした勤怠情報をデータベースに保存する
			boolean isRegistered = attendanceService.saveAttendance(attendance);

			// 最新の勤怠情報を取得して、セッションに保存する
			List<Success> attendances = attendanceService.getAttendancesByCurrentMonth(session);
			session.setAttribute("attendances", attendances);

			// 登録が成功したかチェックしてメッセージを設定する
			if (isRegistered) {
				redirectAttributes.addFlashAttribute("message", "勤怠情報が正常に登録されました");
			} else {
				redirectAttributes.addFlashAttribute("error", "登録に失敗しました。再度お試しください。");
			}
		} catch (IllegalArgumentException e) {
			// 入力ミスなどのエラーが起きた場合の対応
			redirectAttributes.addFlashAttribute("error", e.getMessage());

		} catch (Exception e) {
			// 予期しないエラーが起きた場合の対応
			redirectAttributes.addFlashAttribute("error", "予期せぬエラーが発生しました。再度お試しください。");
		}

		// 登録後の遷移先
		return "redirect:/form";
	}
	
	// Get時の処理
	@GetMapping("/attendanceRegister")
	public String getAttendanceRegister(Model model, HttpSession session) {
		String id = (String) session.getAttribute("id");
		String pass = (String) session.getAttribute("pass");

		model.addAttribute("id", id);
		model.addAttribute("pass", pass);

		return "form";
	}
	
	// 登録結果formのGETメソッドを許可
	@GetMapping("/form")
	public String formResult(Model model, HttpSession session) {
		String id = (String) session.getAttribute("id");
		String pass = (String) session.getAttribute("pass");

		model.addAttribute("id", id);
		model.addAttribute("pass", pass);

		return "form";
	}

	// 編集処理
	@GetMapping("/edit/{date}")
	public String edit(@PathVariable("date") @ModelAttribute("attendances") String dateStr,
			Model model,
			RedirectAttributes redirectAttributes, HttpSession session) {

		// 日付を文字列からLocalDateに変換
		LocalDate date = LocalDate.parse(dateStr);

		// セッションからログインユーザーのidを取得
		String userId = (String) session.getAttribute("id");

		// 日付に該当する勤怠情報を取得
		Optional<Success> attendances = attendanceService.findByDate(date, userId);

		if (!attendances.isEmpty()) {
			// Successの最初の勤怠情報を取得
			Success attendance = attendances.get();

			String id = (String) session.getAttribute("id");
			String pass = (String) session.getAttribute("pass");

			// Modelに勤怠情報とユーザー情報を追加（viewへ渡す）
			model.addAttribute("attendances", attendance);
			model.addAttribute("id", id);
			model.addAttribute("pass", pass);

			return "editForm";
		} else {

			// データが存在しなかった場合の処理
			redirectAttributes.addFlashAttribute("error", "データが見つかりませんでした。");
			return "redirect:/success";
		}
	}

	@PostMapping("/updatesuccess")
	public String updatesuccess(@ModelAttribute("attendances") Success success, Model model,
			RedirectAttributes redirectAttributes, HttpSession session) {

		// セッションからログイン中のuserIdを取得
		String userId = (String) session.getAttribute("id");

		// 更新処理
		boolean isUpdated = attendanceService.update(success, userId);

		// メッセージの設定
		if (isUpdated) {
			model.addAttribute("message", "勤怠情報が正常に更新されました。");
		} else {
			model.addAttribute("error", "更新に失敗しました。再度お試しください。");
		}

		// 勤怠データの再取得
		List<Success> attendances = attendanceService.getAttendancesByCurrentMonth(session);
		Map<String, Integer> totalWorkingTime = attendanceService.getTotalWorkingTimeComponents(attendances);

		// 勤怠データと稼働時間をモデルに設定
		model.addAttribute("attendances", attendances);
		model.addAttribute("totalWorkingHours", totalWorkingTime.get("hours"));
		model.addAttribute("totalWorkingMinutes", totalWorkingTime.get("minutes"));

		String id = (String) session.getAttribute("id");
		String pass = (String) session.getAttribute("pass");

		model.addAttribute("id", id);
		model.addAttribute("pass", pass);

		// 現在の年月を設定
		LocalDate currentDate = LocalDate.now();
		model.addAttribute("Year", currentDate.getYear());
		model.addAttribute("Month", currentDate.getMonthValue());

		// 更新後も勤怠一覧ページを表示
		return "success";
	}

	// 更新画面Getで取得する処理
	@GetMapping("/updatesuccess")
	public String updatesuccessRedirect(Model model) {
		return "redirect:/success";
	}

}
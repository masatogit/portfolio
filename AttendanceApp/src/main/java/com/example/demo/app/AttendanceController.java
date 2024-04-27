package com.example.demo.app;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.Join;
import com.example.demo.service.AttendanceService;

@Controller
@RequestMapping("/")
public class AttendanceController {

	@Autowired
	private final AttendanceService attendanceService;
	

	public AttendanceController(AttendanceService attendanceService) {
		this.attendanceService = attendanceService;
	}

	//attendanceにアクセス
	@GetMapping("/attendance")
	public String attendance(Model model, Attendance attendance, @ModelAttribute("complete") String complete) {
		model.addAttribute("title", "勤怠管理");
		return "attendance";

	}
	
	//clocInの画面遷移処理
	@PostMapping("/clockIn")
	public String clockIn(Model model) {
		LocalDateTime currentTime = LocalDateTime.now();
		attendanceService.clockIn(currentTime);
		model.addAttribute("title", "出勤完了");
		return "clockIn";
	}

	//clockOutの画面遷移処理
	@PostMapping("/clockOut")
	public String clockOut(Model model) {
		LocalDateTime currentTime = LocalDateTime.now();
		attendanceService.clockOut(currentTime);
		model.addAttribute("title", "退勤完了");
		return "clockOut";
	}
	
	
	//勤怠状況取得
	@PostMapping("/join")
	public String join(Model model, Join join) {
		List<Join> list = attendanceService.search();
		model.addAttribute("title", "勤怠状況");
		model.addAttribute("join", list);
		
		return "join";
	}

	//commuting_allowanceの画面遷移
	@PostMapping("/commutingAllowance")
	public String commutingAllowance(@ModelAttribute @Validated Form form, Model model ) {
		model.addAttribute("title", "交通費入力");
		return "commutingAllowance";
	}

	
	//完了処理
	@PostMapping("/complete")
	public String complete(@ModelAttribute @Validated Form form, BindingResult result, 
			Model model, Integer commuting_allowance,
			RedirectAttributes redirectAttributes) {
		
		attendanceService.Transportation_expenses(commuting_allowance);
		model.addAttribute("title", "交通費入力");
		
		
		if (result.hasErrors()) {
			model.addAttribute("title", "入力値にエラーがあります");
			return "commutingAllowance";
		}
		
		redirectAttributes.addFlashAttribute("complete", "完了しました");
		return "redirect:attendance";

	}
	
	
}

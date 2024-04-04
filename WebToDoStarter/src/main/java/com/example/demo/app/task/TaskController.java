package com.example.demo.app.task;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Task;
import com.example.demo.service.TaskService;

/**
 * ToDoアプリ
 */
@Controller
@RequestMapping("/task")
public class TaskController {

	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * タスクの一覧を表示します
	 * @param taskForm
	 * @param model
	 * @return resources/templates下のHTMLファイル名
	 */
	
	
	//一覧表示
	@GetMapping
	public String task(TaskForm taskForm, Model model) {

		//新規登録か更新かを判断する仕掛け,falseだと一覧が表示されない
		taskForm.setNewTask(true);
		
		//Taskのリストを取得する
		List<Task> list = taskService.findAll();
		
		//変数listに taskService.findAllで取得した値を格納した後
		//modelで格納した値をviewへ渡す
		model.addAttribute("list", list);
		model.addAttribute("title", "タスク一覧");

		return "task/index";
	}

	/**
	 * タスクデータを一件挿入
	 * @param taskForm
	 * @param result
	 * @param model
	 * @return
	 */
	
	//送信ボタンを押した時　新規登録
	@PostMapping("/insert")
	public String insert(
			
			//@Validatedで問題なし。
			@Valid @ModelAttribute TaskForm taskForm,
			BindingResult result,
			Model model) {

		Task task = makeTask(taskForm, 0);

		//TaskFormのデータをTaskに格納
		//        	Task task = new Task();
		//        	task.setUserId(1);
		//        	task.setTypeId(taskForm.getTypeId());
		//        	task.setTitle(taskForm.getTitle());
		//        	task.setDetail(task.getDetail());
		//        	task.setDeadline(taskForm.getDeadline());

		//バリデーションエラーがなかった場合
		//!は論理否定演算子で、エラーがない場合に真（true）
		if (!result.hasErrors()) {
			//一件挿入後リダイレクト
			taskService.insert(task);
			return "redirect:/task";
		
			//バリデーションエラーあった場合
		} else {
			taskForm.setNewTask(true);
			model.addAttribute("taskForm", taskForm);
			List<Task> list = taskService.findAll();
			model.addAttribute("list", list);
			model.addAttribute("title", "タスク一覧（バリデーション）");

			return "task/index";
		}
	}

	/**
	 * 一件タスクデータを取得し、フォーム内に表示
	 * @param taskForm
	 * @param id
	 * @param model
	 * @return
	 */
	
	
	//編集ボタンを押した時
	@GetMapping("/{id}")
	public String showUpdate(
			TaskForm taskForm,
			@PathVariable int id,
			Model model) {

		//Taskを取得(Optionalでラップ)
		//Optional<型> 変数 = 
		Optional<Task> taskOpt = taskService.getTask(id);

		//TaskFormへの詰め直し
		//取り出した値をtに格納する
		Optional<TaskForm> taskFormOpt = taskOpt.map(t -> makeTaskForm(t));

		
		//taskFormOptがPresent(値が存在する場合はtrue,存在しない場合はfalse)
		//taskFormOptがPresentである場合(taskFormがnullでない場合)
		//taskFormにtaskFormOptの値を設定する。＝　taskFormはnullでない事が保証される
		//TaskFormがnullでなければ中身を取り出し
		if(taskFormOpt.isPresent()) {
			taskForm = taskFormOpt.get();
		}

		
		//setNewTaskを表示出来るような文を挿入、デフォルトの値の確認
		//taskForm.setNewTask(true);

		model.addAttribute("taskForm", taskForm);
		List<Task> list = taskService.findAll();
		model.addAttribute("list", list);
		model.addAttribute("taskId", id);
		model.addAttribute("title", "更新用フォーム");

		return "task/index";
	}

	/**
	 * タスクidを取得し、一件のデータ更新
	 * @param taskForm
	 * @param result
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	
	//送信ボタンを押した時　編集画面から1件のタスクを更新
	@PostMapping("/update")
	public String update(
			@Valid @ModelAttribute TaskForm taskForm,
			BindingResult result,
			@RequestParam("typeId") int taskId,
			Model model,
			
			//メッセージを残したい時、フラッシュスコープ
			RedirectAttributes redirectAttributes) {

		if (!result.hasErrors()) {
			//TaskFormのデータをTaskに格納
			Task task = makeTask(taskForm, taskId);

			//更新処理、フラッシュスコープの使用、リダイレクト（個々の編集ページ）
			taskService.update(task);
			redirectAttributes.addFlashAttribute("complete", "変更が完了しました");
			//@GetMapping("/{id}")に戻る
			return "redirect:/task/" + taskId;
		} else {
			model.addAttribute("taskForm", taskForm);
			model.addAttribute("title", "タスク一覧");
			return "task/index";
		}
	}

	/**
	 * タスクidを取得し、一件のデータ削除
	 * @param id
	 * @param model
	 * @return
	 */
	
	
	//削除ボタンを押した時
	@PostMapping("/delete")
	public String delete(
			@RequestParam("taskId") int id,
			Model model) {

		//タスクを一件削除しリダイレクト
		taskService.deleteById(id);

		return "redirect:/task";
	}

	
	
	//下記は無し---------------------------------------------------------
	
	
	/**
	 * 複製用に一件タスクデータを取得し、フォーム内に表示
	 * @param taskForm
	 * @param id
	 * @param model
	 * @return
	 */
	//1-1　"/duplicate"に対してマッピングを行うアノテーションを記述する
	public String duplicate(
			TaskForm taskForm,
			//1-2　Requestパラメータから"taskId"の名前でint idを取得するようにする
			int id,
			Model model) {

		//1-3　taskService.getTaskを用いてTaskを取得する
		Optional<Task> taskOpt = null;

		//TaskFormへの詰め直し
		Optional<TaskForm> taskFormOpt = taskOpt.map(t -> makeTaskForm(t));

		//TaskFormがnullでなければ中身を取り出し
		if (taskFormOpt.isPresent()) {
			taskForm = taskFormOpt.get();
		}

		//新規登録のためNewTaskにtrueをセット
		taskForm.setNewTask(true);

		model.addAttribute("taskForm", taskForm);
		List<Task> list = taskService.findAll();
		model.addAttribute("list", list);
		model.addAttribute("title", "タスク一覧");

		return "task/index";
	}

	/**
	 * 選択したタスクタイプのタスク一覧を表示
	 * @param taskForm
	 * @param id
	 * @param model
	 * @return
	 */
	//2-4 "/selectType"に対してマッピングを行うアノテーションを記述する
	public String selectType(
			TaskForm taskForm,
			//2-5 Requestパラメータから"typeId"の名前でint idを取得するようにする
			int id,
			Model model) {

		//新規登録か更新かを判断する仕掛け
		taskForm.setNewTask(true);

		//2-6 taskService.findByTypeを用いてTaskのリストを取得する
		List<Task> list = null;

		model.addAttribute("list", list);
		model.addAttribute("title", "タスク一覧");

		return "task/index";
	}

	/**
	 * TaskFormのデータをTaskに入れて返す
	 * @param taskForm
	 * @param taskId 新規登録の場合は0を指定
	 * @return
	 */
	
	//makeTaskメソッドの定義
	//TaskFormクラスのインスタンスとtaskIdを引数として受け取る
	private Task makeTask(TaskForm taskForm, int taskId) {
		Task task = new Task();
		
		//taskIdが0でない場合(=有効な値を持っている)には
		//task.setId(taskId)を実行して、taskのidプロパティにtaskIdの値を設定
		if (taskId != 0) {
			task.setId(taskId);
		}
		
		//task.setUserId(1)を実行して1を設定
		task.setUserId(1);
		
		//taskFormから取得した値を使用してtaskの他のプロパティに値を設定
		task.setTypeId(taskForm.getTypeId());
		task.setTitle(taskForm.getTitle());
		task.setDetail(taskForm.getDetail());
		task.setDeadline(taskForm.getDeadline());
		//呼び出し元に値を返す
		return task;
	}

	/**
	 * TaskのデータをTaskFormに入れて返す
	 * @param task
	 * @return
	 */
	private TaskForm makeTaskForm(Task task) {

		TaskForm taskForm = new TaskForm();

		taskForm.setTypeId(task.getTypeId());
		taskForm.setTitle(task.getTitle());
		taskForm.setDetail(task.getDetail());
		taskForm.setDeadline(task.getDeadline());
		taskForm.setNewTask(false);

		return taskForm;
	}
}


//private TaskForm makeTaskForm(Task task) メソッドの戻り値の部分である TaskForm は、このメソッドが生成した新しい TaskForm オブジェクトを表しています。メソッドの役割は、Task オブジェクトから TaskForm オブジェクトを作成し、それを呼び出し元に返すことです。
//通常、このようなメソッドはデータ変換（Data Conversion）やマッピング（Mapping）を行うために使用されます。例えば、データベースから取得したエンティティ（Task オブジェクト）を、Webフォームなどの表示用のフォームオブジェクト（TaskForm オブジェクト）に変換する場合に使われます。
//具体的には、このメソッドが Task オブジェクトを受け取り、その内容をもとに新しい TaskForm オブジェクトを生成して返します。これにより、異なるデータモデルを持つオブジェクト間で情報を変換し、プレゼンテーション層やフォームの表示に適した形に整形することができます。

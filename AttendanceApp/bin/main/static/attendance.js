function setfig(num) {
	var ret;
	if (num < 10) { ret = "0" + num; }
	else { ret = num; }
	return ret;
}
function showClock() {
	var nowTime = new Date();						//現在日時を得る
	var nowHour = setfig(nowTime.getHours());		//時を抜き出す
	var nowMin = setfig(nowTime.getMinutes());		//分を抜き出す
	var nowSec = setfig(nowTime.getSeconds());		//秒を抜き出す
	var msg = "現在時刻は、" + nowHour + ":" + nowMin + ":" + nowSec + " です。";//メッセージ
	document.getElementById("RealtimeClockArea").innerHTML = msg;
}
setInterval('showClock()', 1000);





function showAlert() {
	alert("退勤確認をしてください");
}

// 画面が読み込まれた時にタイマーを開始する関数
function startTimer() {
	// 一定時間が経過したら showAlert() 関数を実行する
	var timer = setTimeout(showAlert, 10000); // 

	// マウスが移動したりキーが押された時にタイマーをリセットする
	document.addEventListener("mousemove", resetTimer);
	document.addEventListener("keydown", resetTimer);

	// タイマーをリセットする関数
	function resetTimer() {
		clearTimeout(timer);
		timer = setTimeout(showAlert, 10000); // 
	}
}

// 画面が読み込まれた時に startTimer() 関数を実行する
document.addEventListener("DOMContentLoaded", startTimer);


document.getElementById("clockInForm").addEventListener("submit", function(event) {
	
	//ボタンが押されたかどうか確認する
	if (!confirm("出勤してもよろしいですか？")) {
		//フォーム送信をキャンセルする
		event.preventDefault();
	}else {
		//フォームのデータを取得して、サーバーに送信する
		var clockTime = new Date().toISOString();//現在の時刻を取得
		document.getElementById("clockTime").value = clockTime;
	}
});


document.getElementById("clockOutForm").addEventListener("submit", function(event) {
	
	//ボタンが押されたかどうか確認する
	if (!confirm("退勤してもよろしいですか？")) {
		//フォーム送信をキャンセルする
		event.preventDefault();
	}else {
		//フォームのデータを取得して、サーバーに送信する
		var clockTime = new Date().toISOString();//現在の時刻を取得
		document.getElementById("clockTime").value = clockTime;
	}
});



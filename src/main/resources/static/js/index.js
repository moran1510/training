$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	 //$("#publishModal").modal("hide");
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	var startTime = $("#startTime").val();
	var endTime = $("#endTime").val();

	$.post(
		CONTEXT_PATH + "/course/add",
		{"title":title,"content":content,"startTime":startTime,"endTime":endTime},
		function (data){
			data = $.parseJSON(data);
			if (data.code != 0){
				$("#hintBody").text(data.msg);
				$("#hintModal").modal("show");
				setTimeout(function (){
					$("#hintModal").modal("hide");
				},2000);
			}else {
				$("#hintBody").text(data.msg);
				$("#hintModal").modal("show");
				setTimeout(function(){
					$("#hintModal").modal("hide");
					if (data.code == 0){
						window.location.reload();
					}
				}, 2000);
			}

		}

	);
}
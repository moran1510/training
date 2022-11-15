function cha(btn, useId) {
    var $userId2 = $("#userId2");
    $userId2.value = userId;
}

$(function(){
    $("#publishBtn").click(publish);
});

function publish() {
    //$("#publishModal").modal("hide");
    var courseId = $("#courseId").val();
    var userId = $("#userId").val();
    var score = $("#recipient-score").val();
    var content = $("#message-evaluate").val();

    $.post(
        CONTEXT_PATH + "/teacher/evaluate",
        {"courseId":courseId,"userId":userId,"score":score,"evaluate":content},
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
$(function(){
    $("#publishBtn").click(publish);
});

function publish() {
    //$("#publishModal").modal("hide");
    var courseId = $("#courseId").val();
    var evaluate = $("#message-evaluate").val();

    $.post(
        CONTEXT_PATH + "/course/courseDiscuss",
        {"courseId":courseId,"discuss":evaluate},
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
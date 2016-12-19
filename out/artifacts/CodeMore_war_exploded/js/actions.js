$(document).ready(function() {
    $("#main-content").height(450 + $(".incoming").height());
    $(".close_incoming").on("click", function () {
        $(".incoming").slideUp();
    });
    $("body").on("click", "#logout", function () {
        $.ajax({
            url: "/logout",
            type: "post",
            data: {},
            success: function (result) {
                window.location = "/";
            }
        });
    });
    $("body").on("click", "#project-search-btn", function () {
        $(".cover").show();
        $(".loader").show();
        $.ajax({
            url: "/search",
            type: "post",
            data: {search_project: $("#search-project").val()},
            success: function (result) {
                $(".cover").hide();
                $(".loader").hide();
                $("#projects_search_results").html(result);
            },
            error: function () {
                $(".cover").hide();
                $(".loader").hide();
                $("#projects_search_results").html("<span>Your query returned no results.<br/>Boarden your search terms<br/>and try again.</span>");
            }
        });
    });
    $(document).on("keypress", "#search-project", function (e) {
        if (e.keyCode == 13) {
            $(".cover").show();
            $(".loader").show();
            $.ajax({
                url: "/search",
                type: "post",
                data: {search_project: $("#search-project").val()},
                success: function (result) {
                    $(".cover").hide();
                    $(".loader").hide();
                    $("#projects_search_results").html(result);
                },
                error: function () {
                    $(".cover").hide();
                    $(".loader").hide();
                    $("#projects_search_results").html("<span>Your query returned no results.<br/>Boarden your search terms<br/>and try again.</span>");
                }
            });
        }
    });
    $("body").on("click", ".select_incoming", function () {
        $(".cover").show();
        $(".loader").show();
        $.ajax({
            url: "/select",
            type: "post",
            data: {chat: $(this).attr('id')},
            success: function (result) {
                $(".cover").hide();
                $(".loader").hide();
                $(".incoming").slideUp();
                $(".chat-room").slideDown();
                $("#chat-room-title").html("<span>CodeMore with: " + result + "</span>");
                $("#search-username").val("");
                GetMessages();
            },
            error: function () {
                $(".cover").hide();
                $(".loader").hide();
                alert("Internal Error. Please try again!");
            }
        });
    });
    $("body").on("click", ".select_searched_project", function () {
        $(".cover").show();
        $(".loader").show();
        $.ajax({
            url: "/select",
            type: "post",
            data: {chat: $(this).attr('id')},
            success: function (result) {
                $(".cover").hide();
                $(".loader").hide();
                $(".project-tree").html(result.split("___own___")[1]);
                $(".project-tree-container").slideDown();
                $("#chat-room-title").empty();
                $("#chat-room-inner").empty();
                $("#message").empty();
                $(".chat-room").slideDown();
                $("#chat-room-title").html("<span>CodeMore with: " + result.split("___own___")[0] + "</span>");
                $("#search-username").val("");
                GetMessages();
            },
            error: function () {
                $(".cover").hide();
                $(".loader").hide();
                alert("Internal Error. Please try again!");
            }
        });
    });
    $("body").on("click", "#send-btn", function () {
        $.ajax({
            url: "/send",
            type: "post",
            data: {message: $("#message").val()},
            success: function (result) {
                if (result == "ok") {
                    $("#message").val("");
                    GetMessages();
                }
            },
            error: function () {
                alert("Internal Error. Please try again!");
            }
        });
    });
    $("body").on("click", ".admin-register-btn", function () {
        var formdata = $("#admin-registration-form").serialize();
        $.ajax({
            url: "/admin_actions",
            type: "post",
            data: {action: "add-admin", formdata: formdata},
            success: function (result) {
                $("#add-admin-result").html(result);
                $("#add-admin-result").slideDown();
                setTimeout(function () {
                    $("#add-admin-result").slideUp();
                }, 2000);
            },
            error: function () {
                alert("Internal Error. The new administrator was not added! Make sure the passwords match and try again.");
            }
        });
    });
    $("body").on("click", "#show-users", function () {
        $(".cover").show();
        $(".loader").show();
        $.ajax({
            url: "/admin_actions",
            type: "post",
            data: {action: "show-users"},
            success: function (result) {
                $(".cover").hide();
                $(".loader").hide();
                $("#show-users-tab").empty().append(result);
            },
            error: function () {
                $(".cover").hide();
                $(".loader").hide();
                alert("Internal Error. Please try again!");
            }
        });
    });
    $("body").on("click", ".delete-btn", function () {
        $(".cover").show();
        $(".loader").show();
        $.ajax({
            url: "/admin_actions",
            type: "post",
            data: {action: "delete-user", who: $(this).attr("id")},
            success: function (result) {
                $("#show-users-tab").empty().append(result);
                $.ajax({
                    url: "/admin_actions",
                    type: "post",
                    data: {action: "show-users"},
                    success: function (result) {
                        $(".cover").hide();
                        $(".loader").hide();
                        $("#show-users-tab").append(result);
                    },
                    error: function () {
                        $(".cover").hide();
                        $(".loader").hide();
                        alert("Internal Error. Please try again!");
                    }
                });
            },
            error: function () {
                $(".cover").hide();
                $(".loader").hide();
                alert("Internal Error. Please try again!");
            }
        });
    });
    function GetMessages() {
        $.ajax({
            url: "/get_messages",
            type: "post",
            data: {},
            success: function (result) {
                $("#chat-room-inner").empty();
                $("#chat-room-inner").append(result);
                $("#chat-room-inner").scrollTop($("#chat-room-inner").height() + 500);
                //setTimeout(GetMessages, 1000);
            },
            error: function () {

            }
        });
    };
});

$(document).ready(function(){
    $("#down-arrow").mouseenter(function(){
        $("#dropdown").slideDown();
    });
    $("#dropdown").mouseleave(function(){
        $("#dropdown").slideUp();
    });

})
$(document).ready(function(){
    $("#down-arrow").mouseenter(function(){
        $("#dropdown").slideDown();
    });
    $("#dropdown").mouseleave(function(){
        $("#dropdown").slideUp();
    });
    $("#add-admin").click(function(){
        $("#show-users-tab").slideUp();
        $("#add-admin-tab").slideDown();
    });
    $("#show-users").click(function(){
        $("#show-users-tab").empty();
        $("#add-admin-tab").slideUp();
        $("#show-users-tab").slideDown();
    });
})

$(document).ready(function(){
    $(document).on('click', ".file", function(){
        $(".file-viewer-container").slideUp();
        $(".file").css("background-color","");
        $(this).css("background-color","#26C2F0");
        var filename = $(this).text();
        var fileid = $(this).attr('id');
        $.ajax({
            url: "/directory_actions",
            type: "post",
            data: {id: $(this).attr("id"), mode: "view-file"},
            success: function (result) {
                $(".file-viewer").text(result);
                $(".file-viewer-container").slideDown();
                $("#file-name-viewer").html(filename);
                $("#update-file-id").val(fileid);
            },
            error: function () {

            }
        });
    });
})

$(document).ready(function(){
    $(".project-remove").click(function(){
        $(".hidden-remove").val($(this).attr('id'));
        $("#modal-remove").modal('show');
    });
})
$(document).ready(function(){
    $(".project-add").click(function(){
        $(".hidden-add").val($(this).attr('id'));
        $("#modal-add").modal('show');
    });
    $("#select-add-directory").change(function(){
        if ($(this).val() == "folder"){
            $("#add-file-details").hide();
            $("#add-folder-details").slideDown();
            $("#form-directory-add").removeAttr("enctype").removeAttr("encoding");
        } else if ($(this).val() == "file"){
            $("#add-folder-details").hide();
            $("#add-file-details").slideDown();
            $("#form-directory-add").attr("enctype", "multipart/form-data").attr("encoding", "multipart/form-data");
        } else {
            $("#add-folder-details").hide();
            $("#add-file-details").hide();
        }
    });
})
$(document).ready(function(){
    $(".project-move").click(function(){
        $(".hidden-move").val($(this).attr('id'));
        $("#modal-move").modal('show');
    });
})
$(document).ready(function(){
    $("#new-project").click(function(){
        $("#modal-new").modal('show');
    });
})
$(document).ready(function(){
    $("#delete-project").click(function(){
        $("#modal-delete").modal('show');
    });
})

$(document).ready(function(){
    $("#update-file").click(function(){
        $("#file-viewer-hidden").text($(".file-viewer").text());
    })
})

$(document).ready(function(){
    if ($("#new-project").val() == "Create a new project"){
        $(".project-tree").css('border','none');
    }
})
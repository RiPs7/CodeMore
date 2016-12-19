//Form initialization during document ready
jQuery(document).ready(function() {
    resetForms();
});

function resetForms(){
    jQuery('form').each(function() { this.reset() });
}

//Login form handling
jQuery(document).ready(function($) {
    $("#login-form").submit(function(e){
        if ($("#username").val() == "") {
            $("#username").addClass("login-input-required");
            $("#username_required").show();
            e.preventDefault();
        }
        if ($("#password").val() == "") {
            $("#password").addClass("login-input-required");
            $("#password_required").show();
            e.preventDefault();
        }
    });
    $("#username").focus(function(){
        $("#username").removeClass("login-input-required");
        $("#username_required").hide();
    });
    $("#password").focus(function(){
        $("#password").removeClass("login-input-required");
        $("#password_required").hide();
    });
});

//Administrator login form handling
jQuery(document).ready(function($) {
    $("#admin-login-form").submit(function(e){
        if ($("#admin-username").val() == "") {
            $("#admin-username").addClass("login-input-required");
            $("#username_required").show();
            e.preventDefault();
        }
        if ($("#admin-password").val() == "") {
            $("#admin-password").addClass("login-input-required");
            $("#password_required").show();
            e.preventDefault();
        }
    });
    $("#admin-username").focus(function(){
        $("#admin-username").removeClass("login-input-required");
        $("#username_required").hide();
    });
    $("#admin-password").focus(function(){
        $("#admin-password").removeClass("login-input-required");
        $("#password_required").hide();
    });
});

//Registration form handling
jQuery(document).ready(function($){
    $("#firstname").focus(function(){
        $("#firstname").removeClass("signup-input-required");
        if ($("#firstname").val() == "First Name") {
            $("#firstname").val("");
            $("#firstname").removeClass("signup-input-unfocused").addClass("signup-input-focused");
            $("#firstname_required").hide();
        }
    });
    $("#firstname").focusout(function(){
        if ($("#firstname").val() == "First Name") {
            $("#firstname").removeClass("signup-input-focused").addClass("signup-input-unfocused");
        }else if($("#firstname").val() == ""){
            $("#firstname").val("First Name");
            $("#firstname").removeClass("signup-input-focused").addClass("signup-input-unfocused").addClass("signup-input-required");
            $("#firstname_required").show();
        }
    });

    $("#lastname").focus(function(){
        $("#lastname").removeClass("signup-input-required");
        if ($("#lastname").val() == "Last Name") {
            $("#lastname").val("");
            $("#lastname").removeClass("signup-input-unfocused").addClass("signup-input-focused");
            $("#lastname_required").hide();
        }
    });
    $("#lastname").focusout(function(){
        if ($("#lastname").val() == "Last Name") {
            $("#lastname").removeClass("signup-input-focused").addClass("signup-input-unfocused");
        }else if($("#lastname").val() == ""){
            $("#lastname").val("Last Name");
            $("#lastname").removeClass("signup-input-focused").addClass("signup-input-unfocused").addClass("signup-input-required");
            $("#lastname_required").show();
        }
    });

    $("#reg_username").focus(function(){
        $("#reg_username").removeClass("signup-input-required");
        if ($("#reg_username").val() == "Username") {
            $("#reg_username").val("");
            $("#reg_username").removeClass("signup-input-unfocused2").addClass("signup-input-focused2");
            $("#reg_username_required").hide();
        }
    });
    $("#reg_username").focusout(function(){
        if ($("#reg_username").val() == "Username") {
            $("#reg_username").removeClass("signup-input-focused2").addClass("signup-input-unfocused2");
        }else if($("#reg_username").val() == ""){
            $("#reg_username").val("Username");
            $("#reg_username").removeClass("signup-input-focused2").addClass("signup-input-unfocused2").addClass("signup-input-required");
            $("#reg_username_required").show();
        }
    });

    $("#reg_password").focus(function(){
        $("#reg_password").removeClass("signup-input-required");
        if ($("#reg_password").val() == "Password") {
            $("#reg_password").val("");
            $("#reg_password").removeClass("signup-input-unfocused2").addClass("signup-input-focused2");
            $("#reg_password").attr("type", "password");
            $("#reg_password_required").hide();
        }
    });
    $("#reg_password").focusout(function(){
        if ($("#reg_password").val() == "Password") {
            $("#reg_password").removeClass("signup-input-focused2").addClass("signup-input-unfocused2");
        }else if($("#reg_password").val() == ""){
            $("#reg_password").val("Password");
            $("#reg_password").removeClass("signup-input-focused2").addClass("signup-input-unfocused2").addClass("signup-input-required");
            $("#reg_password").attr("type", "text");
            $("#reg_password_required").show();
        }
    });

    $("#reg_re_password").focus(function(){
        $("#reg_re_password").removeClass("signup-input-required");
        if ($("#reg_re_password").val() == "Retype Password") {
            $("#reg_re_password").val("");
            $("#reg_re_password").removeClass("signup-input-unfocused2").addClass("signup-input-focused2");
            $("#reg_re_password").attr("type", "password");
            $("#reg_re_password_required").hide();
        }
    });
    $("#reg_re_password").focusout(function(){
        if ($("#reg_re_password").val() == "Retype Password") {
            $("#reg_re_password").removeClass("signup-input-focused2").addClass("signup-input-unfocused2");
        }else if($("#reg_re_password").val() == ""){
            $("#reg_re_password").val("Retype Password");
            $("#reg_re_password").removeClass("signup-input-focused2").addClass("signup-input-unfocused2").addClass("signup-input-required");
            $("#reg_re_password").attr("type", "text");
            $("#reg_re_password_required").show();
        }
    });

    $("#reg_email").focus(function(){
        $("#reg_email").removeClass("signup-input-required");
        if ($("#reg_email").val() == "Email") {
            $("#reg_email").val("");
            $("#reg_email").removeClass("signup-input-unfocused2").addClass("signup-input-focused2");
            $("#reg_email_required").hide();
        }
    });
    $("#reg_email").focusout(function(){
        if ($("#reg_email").val() == "Email") {
            $("#reg_email").removeClass("signup-input-focused2").addClass("signup-input-unfocused2");
        }else if($("#reg_email").val() == ""){
            $("#reg_email").val("Email");
            $("#reg_email").removeClass("signup-input-focused2").addClass("signup-input-unfocused2").addClass("signup-input-required");
            $("#reg_email_required").show();
        }
    });
});

//Administrator Registration form handling
jQuery(document).ready(function($){
    $("#admin-firstname").focus(function(){
        $("#admin-firstname").removeClass("signup-input-required");
        if ($("#admin-firstname").val() == "First Name") {
            $("#admin-firstname").val("");
            $("#admin-firstname").removeClass("signup-input-unfocused").addClass("signup-input-focused");
            $("#firstname_required").hide();
        }
    });
    $("#admin-firstname").focusout(function(){
        if ($("#admin-firstname").val() == "First Name") {
            $("#admin-firstname").removeClass("signup-input-focused").addClass("signup-input-unfocused");
        }else if($("#admin-firstname").val() == ""){
            $("#admin-firstname").val("First Name");
            $("#admin-firstname").removeClass("signup-input-focused").addClass("signup-input-unfocused").addClass("signup-input-required");
            $("#firstname_required").show();
        }
    });

    $("#admin-lastname").focus(function(){
        $("#admin-lastname").removeClass("signup-input-required");
        if ($("#admin-lastname").val() == "Last Name") {
            $("#admin-lastname").val("");
            $("#admin-lastname").removeClass("signup-input-unfocused").addClass("signup-input-focused");
            $("#lastname_required").hide();
        }
    });
    $("#admin-lastname").focusout(function(){
        if ($("#admin-lastname").val() == "Last Name") {
            $("#admin-lastname").removeClass("signup-input-focused").addClass("signup-input-unfocused");
        }else if($("#admin-lastname").val() == ""){
            $("#admin-lastname").val("Last Name");
            $("#admin-lastname").removeClass("signup-input-focused").addClass("signup-input-unfocused").addClass("signup-input-required");
            $("#lastname_required").show();
        }
    });

    $("#admin-reg_username").focus(function(){
        $("#admin-reg_username").removeClass("signup-input-required");
        if ($("#admin-reg_username").val() == "Username") {
            $("#admin-reg_username").val("");
            $("#admin-reg_username").removeClass("signup-input-unfocused2").addClass("signup-input-focused2");
            $("#reg_username_required").hide();
        }
    });
    $("#admin-reg_username").focusout(function(){
        if ($("#admin-reg_username").val() == "Username") {
            $("#admin-reg_username").removeClass("signup-input-focused2").addClass("signup-input-unfocused2");
        }else if($("#admin-reg_username").val() == ""){
            $("#admin-reg_username").val("Username");
            $("#admin-reg_username").removeClass("signup-input-focused2").addClass("signup-input-unfocused2").addClass("signup-input-required");
            $("#reg_username_required").show();
        }
    });

    $("#admin-reg_password").focus(function(){
        $("#admin-reg_password").removeClass("signup-input-required");
        if ($("#admin-reg_password").val() == "Password") {
            $("#admin-reg_password").val("");
            $("#admin-reg_password").removeClass("signup-input-unfocused2").addClass("signup-input-focused2");
            $("#admin-reg_password").attr("type", "password");
            $("#reg_password_required").hide();
        }
    });
    $("#admin-reg_password").focusout(function(){
        if ($("#admin-reg_password").val() == "Password") {
            $("#admin-reg_password").removeClass("signup-input-focused2").addClass("signup-input-unfocused2");
        }else if($("#admin-reg_password").val() == ""){
            $("#admin-reg_password").val("Password");
            $("#admin-reg_password").removeClass("signup-input-focused2").addClass("signup-input-unfocused2").addClass("signup-input-required");
            $("#admin-reg_password").attr("type", "text");
            $("#reg_password_required").show();
        }
    });

    $("#admin-reg_re_password").focus(function(){
        $("#admin-reg_re_password").removeClass("signup-input-required");
        if ($("#admin-reg_re_password").val() == "Retype Password") {
            $("#admin-reg_re_password").val("");
            $("#admin-reg_re_password").removeClass("signup-input-unfocused2").addClass("signup-input-focused2");
            $("#admin-reg_re_password").attr("type", "password");
            $("#reg_re_password_required").hide();
        }
    });
    $("#admin-reg_re_password").focusout(function(){
        if ($("#admin-reg_re_password").val() == "Retype Password") {
            $("#admin-reg_re_password").removeClass("signup-input-focused2").addClass("signup-input-unfocused2");
        }else if($("#admin-reg_re_password").val() == ""){
            $("#admin-reg_re_password").val("Retype Password");
            $("#admin-reg_re_password").removeClass("signup-input-focused2").addClass("signup-input-unfocused2").addClass("signup-input-required");
            $("#admin-reg_re_password").attr("type", "text");
            $("#reg_re_password_required").show();
        }
    });

    $("#admin-reg_email").focus(function(){
        $("#admin-reg_email").removeClass("signup-input-required");
        if ($("#admin-reg_email").val() == "Email") {
            $("#admin-reg_email").val("");
            $("#admin-reg_email").removeClass("signup-input-unfocused2").addClass("signup-input-focused2");
            $("#reg_email_required").hide();
        }
    });
    $("#admin-reg_email").focusout(function(){
        if ($("#admin-reg_email").val() == "Email") {
            $("#admin-reg_email").removeClass("signup-input-focused2").addClass("signup-input-unfocused2");
        }else if($("#admin-reg_email").val() == ""){
            $("#admin-reg_email").val("Email");
            $("#admin-reg_email").removeClass("signup-input-focused2").addClass("signup-input-unfocused2").addClass("signup-input-required");
            $("#reg_email_required").show();
        }
    });
});

//Find users form handling
jQuery(document).ready(function($) {
    $("#find-users-form").submit(function(e) {
        if ($("#search-username").val() == "" || $("#search-username").val() == "Search usernames") {
            $("#search-username").addClass("login-input-required");
            $("#search_username_required").show();
            e.preventDefault();
        }
    });
    $("#search-username").focus(function(){
        $("#search_username_required").hide();
        if ($("#search-username").val() == "Search usernames") {
            $("#search-username").val("");
            $("#search-username").removeClass("signup-input-unfocused").addClass("signup-input-focused");
        }
    });
    $("#search-username").focusout(function(){
        if ($("#search-username").val() == "Search usernames") {
            $("#search-username").removeClass("signup-input-focused").addClass("signup-input-unfocused");
        }else if($("#search-username").val() == ""){
            $("#search-username").val("Search usernames");
            $("#search-username").removeClass("signup-input-focused").addClass("signup-input-unfocused").addClass("signup-input-required");
        }
    });
});
<%@ page import="Core.User" %>
<%@ page import="Core.Admin" %>
<%--
  Created by IntelliJ IDEA.
  User: PeriklisMaravelias
  Date: 1/1/16
  Time: 4:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>CodeMore Administration</title>
    <meta charset="UTF-8">
    <meta name="description" content="">
    <meta name="author" content="">
    <meta name="robots" content="none">
    <!--Stylesheets-->
    <link rel="stylesheet" type="text/css" href="css/theme.css">
    <!--Scripts-->
    <script src="js/jquery-1.11.3.min.js"></script>
    <script src="js/jquery.validate.min.js"></script>
    <script src="js/form_validations.js"></script>
    <script src="js/actions.js"></script>
</head>
<body>
<%
    Admin admin = (Admin)request.getSession().getAttribute("admin");
    if (admin == null)
    {
        response.sendRedirect("");
        return;
    }
%>
<div class="heading-row">
    <div class="heading-logo">
        <img src="images/code_more_logo.png" class="logo-image"/>
        <a href="login.jsp">
            <div class="heading-text">
                <span style="font-size:40px">
                    <strong>
                        <span style="color:white">Code</span><span style="color:black">More</span>
                    </strong>
                </span>
            </div>
        </a>
    </div>
    <div style="float:right; margin:50px 20px 0px 0px;">
        <span>Welcome <% out.print(admin.getUsername()); %><span id="down-arrow" style="cursor:pointer"> &#x25BE</span></span>
        <div id="dropdown" style="display:none; z-index:100; border-radius:5px; background-color:white; text-align:right; margin:10px 0px 0px; height:50px; text-align:center;"><br/>
            <span style="text-align:right; cursor:pointer" id="logout">Log out</span>
        </div>
    </div>
</div>
<div id="main-content" style="height:450px; width:98%; background-color: white; padding-top:50px; padding-left:2%;">
    <div class="main-content">
        <div style="width:100%">
            <div style="width:49%; float:left;"><input type="submit" id="add-admin" name="add-admin" value="Add Admin" style="margin:auto; width:99%; height:50px;"/></div>
            <div style="width:49%; float:left;"><input type="submit" id="show-users" name="show-users" value="Show Users" style="margin:auto; width:99%; height:50px;"/></div>
        </div>
    </div>
    <br/>
    <div id="action-content" style="width:97%; margin-top:50px; padding-left:1%;">
        <div id="add-admin-tab" style="z-index:1; height:80%; display:none;">
            <form id="admin-registration-form">
                <div>
                    <div style="margin:10px">
                        <div style="float:left; margin-bottom:10px">
                            <input type="text" class="signup-input-unfocused" name="admin-firstname" id="admin-firstname" value="First Name"><br/><span class="indicate-required" id="firstname_required">This field is required!</span>
                        </div>
                        <div style="float:left; margin-left:25px; margin-bottom:10px">
                            <input type="text" class="signup-input-unfocused" name="admin-lastname" id="admin-lastname" value="Last Name"><br/><span class="indicate-required" id="lastname_required">This field is required!</span>
                        </div>
                    </div><br/>
                    <div style="margin: 25px 0px 0px 10px;">
                        <input type="text" class="signup-input-unfocused2" name="admin-reg_username" id="admin-reg_username" value="Username"><br/><span class="indicate-required" id="reg_username_required">This field is required!</span>
                    </div>
                    <div style="margin:15px 0px 10px 10px;">
                        <input type="text" class="signup-input-unfocused2" name="admin-reg_password" id="admin-reg_password" value="Password"><br/><span class="indicate-required" id="reg_password_required">This field is required!</span>
                    </div>
                    <div style="margin:15px 0px 10px 10px;">
                        <input type="text" class="signup-input-unfocused2" name="admin-reg_re_password" id="admin-reg_re_password" value="Retype Password"><br/><span class="indicate-required" id="reg_re_password_required">This field is required!</span>
                    </div>
                    <div style="margin:15px 0px 10px 10px;">
                        <input type="text" class="signup-input-unfocused2" name="admin-reg_email" id="admin-reg_email" value="Email"><br/><span class="indicate-required" id="reg_email_required">This field is required!</span>
                    </div>
                    <div style="margin:15px 0px 10px 10px;">
                        <input type="button" class="admin-register-btn" value="Add Admin" name="action" id="action"/>
                    </div>
                </div>
            </form>
            <div id="add-admin-result" style="width:40%; height:40px; display:none; background-color:orange; border-radius:5px;">

            </div>
        </div>
        <div id="show-users-tab" style="z-index:1; height:80%; display:none; overflow-y:auto;">

        </div>
    </div>
</div>

<div class="loader" style="display:none; position:absolute; top:20%; left:45%; z-index:1001;">
    <img src="images/loading_spinner.gif"/>
</div>
<div class="cover" style="position:fixed; top:0; left:0; width:100%; height:100%; z-index:1000; background: rgba(0, 0, 0, 0.5); display:none"></div>
</body>
<footer>
    <div class="footer-section">
        <div class="footer-section-left">
            <span>Designed and developed by:</span><br/><br/>
            <span><a href="mailto:perimara@hotmail.com" style="color:white;">Periklis Maravelias</a>, <a href="mailto:tassosgeo14@gmail.com" style="color:white;">Anastasios Georgiadis</a></span>
        </div>
    </div>
</footer>
</html>

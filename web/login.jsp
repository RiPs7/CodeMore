<%@ page import="Core.User" %>
<%@ page import="Core.Values" %>
<%--
  Created by IntelliJ IDEA.
  User: PeriklisMaravelias
  Date: 11/22/15
  Time: 6:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>CodeMore</title>
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
    </head>

    <body>
        <%
            User user = (User)request.getSession().getAttribute("user");
            if (user != null)
            {
                response.sendRedirect("home.jsp");
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
            <div class="heading-login">
                <form id="login-form" method="post" action="login">
                    <div id="error-display" class="login-div" style="color:red; margin-top:20px; margin-right:10px">
                        <span id="error-display-inner">
                            <%
                                String error = request.getParameter("error");
                                if (error != null)
                                {
                                    if (error.equals(Values.CredentialCombinationNotFound)) { %> Wrong Credentials <% }
                                    if (error.equals(Values.InternalError)) { %> Internal Server Error <% }
                                }
                            %>
                        </span>
                    </div>
                    <div class="login-div">
                        <label for="username" id="label-username">Username</label><br/>
                        <input type="text" class="login-txtbox" id="username" name="username" tabindex="1"/><br/>
                        <span class="indicate-required" id="username_required">This field is required!<br/></span>
                    </div>
                    <div class="login-div">
                        <label for="password" id="label-password">Password</label><br/>
                        <input type="password" class="login-txtbox" id="password" name="password" tabindex="2"/><br/>
                        <span class="indicate-required" id="password_required">This field is required!<br/></span>
                    </div>
                    <div class="login-div" style="margin-top:16px">
                        <input type="submit" class="login-btn" value="Log In"/>
                    </div>
                </form>
            </div>
        </div>
        <div style="height:427px; background-color: white; padding-top:50px;">
            <div style="width:60%; height:100%; float:left;">
                <img src="images/connect-the-world-background.png"/>
            </div>
            <div style="width:40%; height:100%; float:left; align-items:center;">
                <div style="padding:10px">
                    <span><strong>Don't have an account?</strong><br/><strong>Sign up now for FREE!</strong></span>
                </div>
                <form id="registration-form" method="post" action="register">
                    <div>
                        <div style="margin:10px">
                            <div style="float:left; margin-bottom:10px">
                                <input type="text" class="signup-input-unfocused" name="firstname" id="firstname" value="First Name"><br/><span class="indicate-required" id="firstname_required">This field is required!</span>
                            </div>
                            <div style="float:left; margin-left:25px; margin-bottom:10px">
                                <input type="text" class="signup-input-unfocused" name="lastname" id="lastname" value="Last Name"><br/><span class="indicate-required" id="lastname_required">This field is required!</span>
                            </div>
                        </div><br/>
                        <div style="margin:10px">
                            <input type="text" class="signup-input-unfocused2" name="reg_username" id="reg_username" value="Username"><br/><span class="indicate-required" id="reg_username_required">This field is required!</span>
                        </div>
                        <div style="margin:10px">
                            <input type="text" class="signup-input-unfocused2" name="reg_password" id="reg_password" value="Password"><br/><span class="indicate-required" id="reg_password_required">This field is required!</span>
                        </div>
                        <div style="margin:10px">
                            <input type="text" class="signup-input-unfocused2" name="reg_re_password" id="reg_re_password" value="Retype Password"><br/><span class="indicate-required" id="reg_re_password_required">This field is required!</span>
                        </div>
                        <div style="margin:10px">
                            <input type="text" class="signup-input-unfocused2" name="reg_email" id="reg_email" value="Email"><br/><span class="indicate-required" id="reg_email_required">This field is required!</span>
                        </div>
                        <div>
                            <input type="submit" class="register-btn" value="Create Account"/>
                        </div>
                    </div>
                </form>
                <%
                    if (error != null) {
                        if (error.equals(Values.PasswordsNotMatch)){ %> <div><span style="color:red">Passwords do not match!</span></div> <%}
                    }
                %>
            </div>
        </div>
        <div class="footer-section">
            <div class="footer-section-left">
                <span>Designed and developed by:</span><br/><br/>
                <span><a href="mailto:perimara@hotmail.com" style="color:white;">Periklis Maravelias</a>, <a href="mailto:tassosgeo14@gmail.com" style="color:white;">Anastasios Georgiadis</a></span>
            </div>
            <div class="footer-section-right">
                <span><a href="admin.jsp" style="color:white">Login as administrator</a></span>
            </div>
        </div>
    </body>
</html>
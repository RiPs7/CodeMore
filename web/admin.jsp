<%--
  Created by IntelliJ IDEA.
  User: PeriklisMaravelias
  Date: 12/1/15
  Time: 11:34 PM
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
</head>
<body>
    <%
        if (request.getSession().getAttribute("admin") != null) {
            response.sendRedirect("backend.jsp");
            return;
        }
    %>
    <div style="height:617px; background-color: white;">
        <div class="admin-login-panel">
            <div style="margin-top:1em; margin-left:3em;">
                <div style="left:15%; margin-bottom: 20px; position: relative; ">
                    <h4>Log in to the Administrator Panel</h4>
                </div>
                <form id="admin-login-form" method="post" action="login">
                    <div class="login-div">
                        <label for="admin-username" id="label-username">Username</label><br/>
                        <input type="text" class="login-txtbox" id="admin-username" name="admin-username" tabindex="1"/><br/>
                        <span class="indicate-required" id="username_required">This field is required!<br/></span>
                    </div>
                    <div class="login-div">
                        <label for="admin-password" id="label-password">Password</label><br/>
                        <input type="password" class="login-txtbox" id="admin-password" name="admin-password" tabindex="2"/><br/>
                        <span class="indicate-required" id="password_required">This field is required!<br/></span>
                    </div>
                    <div class="login-div" style="margin-top:16px">
                        <input type="submit" class="login-btn" value="Log In"/>
                    </div>
                </form>
                <div style="float:right; margin-right: 5px;">
                    <img src="images/code_more_logo.png" style="height:60px; width:60px;"/>
                </div>
                <div id="error-display" class="login-div" style="color:red; margin-top:20px; margin-right:10px">
                        <span id="error-display-inner">
                            <%
                                String error = request.getParameter("error");
                                if (error != null)
                                {
                                    if (error.equals("Q3JlZGVudGlhbENvbWJpbmF0aW9uTm90Rm91bmQ=")) { %> Wrong Credentials <% }
                                    if (error.equals("SW50ZXJuYWxFcnJvcg==")) { %> Internal Server Error <% }
                                }
                            %>
                        </span>
                </div>
            </div>
        </div>
    </div>
    <div class="footer-section">
        <div class="footer-section-left">
            <span>Designed and developed by:</span><br/><br/>
            <span><a href="mailto:perimara@hotmail.com" style="color:white;">Periklis Maravelias</a>, <a href="mailto:tassosgeo14@gmail.com" style="color:white;">Anastasios Georgiadis</a></span>
        </div>
    </div>
</body>
</html>

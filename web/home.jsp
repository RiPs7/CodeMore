<%@ page import="com.sun.org.apache.xml.internal.security.utils.Base64" %>
<%@ page import="Core.*" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
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
    <script src="js/actions.js"></script>
</head>

<body>
<%
    User user = (User)request.getSession().getAttribute("user");
    if (user == null)
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
        <span>Welcome <% out.print(user.getUsername());%><span id="down-arrow" style="cursor:pointer"> &#x25BE</span></span>
        <div id="dropdown" style="display:none; z-index:100; border-radius:5px; background-color:white; text-align:right; margin:10px 0px 0px; height:85px; text-align:center; box-shadow: 7px 7px 5px gray;"><br/>
            <form action="/getproject" method="post">
                <input type="submit" value="My Project" style="border:none; font-size:16px; background:transparent; cursor:pointer"/>
            </form>
            <br/>
            <span style="text-align:right; cursor:pointer" id="logout">Log out</span>
        </div>
    </div>
</div>
<div id="main-content" style="height:450px; width:100%; background-color: white; padding-top:50px;">
    <div class="main-content">
        <%
            //search for incoming messages
            Connection conn = null;
            try {
                InitialContext ctx = new InitialContext();
                DataSource datasource = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
                conn = datasource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=\"CodeMore\"");
                ResultSet rs = stmt.executeQuery();
                boolean incoming_messages = false;
                String html_output = "<div class=\"incoming\">";
                while (rs.next()) {
                    if (rs.getString(1).contains(Values.ChatDS)) {
                        if (rs.getString(1).split(Values.ChatDS)[1].equals(user.getUsername())) {
                            PreparedStatement stmt2 = conn.prepareStatement("SELECT `status`, `from` FROM `" + rs.getString(1) + "`");
                            ResultSet rs1 = stmt2.executeQuery();
                            String current_from = null;
                            while (rs1.next()) {
                                if (rs1.getInt(1) == Values.Pending && !rs1.getString(2).equals(current_from)) {
                                    current_from = rs1.getString(2);
                                    incoming_messages = true;
                                    html_output += "<a class=\"select_incoming\" id=\"" + new String(Base64.encode(("You have a new message from " + rs1.getString(2)).getBytes("UTF-8"))) + "\" style=\"color:black; text-decoration:underline; cursor:pointer;\" class=\"close_incoming\"><span>You have a new message from " + rs1.getString(2) + "</span></a><br/>";
                                }
                            }
                        }
                    }
                }
                html_output += "</div>";
                if (incoming_messages) {
                    out.print(html_output);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                try {conn.close();} catch(Exception e) {}
            }
        %>
        <div class="project-finder">
            <span>Search for projects</span><br/><br/>
            <input style="border:1px solid black; margin-bottom:10px; font-size: 15px;" type="text" id="search-project" name="search-project" placeholder="Search projects"/><br/>
            <span class="indicate-required" id="search_project_required">Fill in a project to Search.<br/><br/></span>
            <input type="button" style="margin-bottom:5px;" id="project-search-btn" name="search-btn" value="Find"/><br/>
            <div id="projects_search_results"></div>
        </div>
        <div class="project-tree-container" style="width:20%; display:none;">
            <div class="project-tree">
            </div>
        </div>
        <div class="file-viewer-container" style="display:none; width:27%">
            <div style="margin-top: -30px; font-size: 18px;"><span id="file-name-viewer"></span></div>
            <div class="file-viewer-inner">
                <pre><code class="file-viewer"></code></pre>
            </div>
        </div>
        <div class="chat-room" style="display:none">
            <div id="chat-room-title" name="chat-room-title" style="height:5%; border:1px solid black;"></div>
            <div id="chat-room-inner" style="height:70%; padding:10px; border:1px solid black; overflow-y: auto;">

            </div>
            <div id="chat-room-message" style="height:18%;">
                <textarea class="chatmsg" id="message" name="message"></textarea>
                <input type="submit" id="send-btn" name="send-btn" value="Send"/>
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
<div class="loader" style="display:none; position:absolute; top:20%; left:45%; z-index:1001;">
    <img src="images/loading_spinner.gif"/>
</div>
<div class="cover" style="position:fixed; top:0; left:0; width:100%; height:100%; z-index:1000; background: rgba(0, 0, 0, 0.5); display:none"></div>
</body>
</html>
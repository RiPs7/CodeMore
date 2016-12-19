<%@ page import="com.sun.org.apache.xml.internal.security.utils.Base64" %>
<%@ page import="Core.*" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>CodeMore</title>
  <meta charset="UTF-8">
  <meta name="description" content="">
  <meta name="author" content="">
  <meta name="robots" content="none">
  <!--Stylesheets-->
  <link rel="stylesheet" type="text/css" href="css/bootstrap.css">
  <link rel="stylesheet" type="text/css" href="css/bootstrap-theme.css">
  <link rel="stylesheet" type="text/css" href="css/bootstrap-theme.css.map">
  <link rel="stylesheet" type="text/css" href="css/bootstrap-theme.min.css">
  <link rel="stylesheet" type="text/css" href="css/bootstrap.css.map">
  <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
  <link rel="stylesheet" type="text/css" href="css/theme.css">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <!--Scripts-->
  <script src="js/jquery-1.11.3.min.js"></script>
  <script src="js/jquery.validate.min.js"></script>
  <script src="js/form_validations.js"></script>
  <script src="js/bootstrap.min.js"></script>
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

<div id="modal-remove" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Confirm Remove</h4>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to remove the selected item?</p>
                <p class="text-warning"><small>Any content it may have, will be deleted too</small></p>
            </div>
            <div class="modal-footer">
                <form action="/directory_actions" method="post">
                    <input type="button" class="btn btn-default" data-dismiss="modal" value="Close"/>
                    <input type="hidden" value="" class="hidden-remove" name="hidden-remove"/>
                    <input type="submit" class="btn btn-primary confirm-remove" value="Confirm"/>
                </form>
            </div>
        </div>
    </div>
</div>

<div id="modal-add" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Select what to add</h4>
            </div>
            <form id="form-directory-add" method="post" action="/directory_actions"/>
                <div class="modal-body">
                    <select id="select-add-directory" name="select-add-directory" style="margin-bottom:10px;">
                        <option value="">Please select...</option>
                        <option value="folder">Folder</option>
                        <option value="file">File</option>
                    </select>
                    <div id="add-folder-details" style="display:none;">
                        <input type="text" name="folder-name" placeholder="Folder Name"/>
                    </div>
                    <div id="add-file-details" style="display:none;">
                        <input type="file" name="file-upload"/>
                    </div>
                </div>
                <div class="modal-footer">
                    <input type="button" class="btn btn-default" data-dismiss="modal" value="Close"/>
                    <input type="hidden" value="" class="hidden-add" name="hidden-add"/>
                    <input type="submit" class="btn btn-primary confirm-add" value="Add"/>
                </div>
            </form>
        </div>
    </div>
</div>

<div id="modal-move" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Select Destination</h4>
            </div>
            <form action="/directory_actions" method="post">
                <div class="modal-body">
                    <p>Select a destination folder to move the chosen item</p>
                    <select id="select-move-directory" name="select-move-directory" style="margin-bottom:10px;">
                        <option value="">Please select...</option>
                        <option value="directory-0">&lt;Root&gt;</option>
                        <%
                            Connection conn = null;
                            PreparedStatement stmt = null;
                            ResultSet rs = null;
                            try{
                                InitialContext ctx = new InitialContext();
                                DataSource datasource = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
                                conn = datasource.getConnection();
                                stmt = conn.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=\"CodeMore\"");
                                rs = stmt.executeQuery();
                                String project_table = null;
                                while(rs.next()){
                                    if (rs.getString(1).contains(user.getUsername() + Values.ProjectDS)) {
                                        project_table = rs.getString(1);
                                        break;
                                    }
                                }
                                stmt = conn.prepareStatement("SELECT id, name, type FROM `" + project_table + "` ORDER BY `name` ASC");
                                rs = stmt.executeQuery();
                                while (rs.next()){
                                    if (rs.getInt(3) == Values.Folder) {
                                        out.println("<option value=\"directory-" + rs.getInt(1) + "\">" + rs.getString(2) + "</option>");
                                    }
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }finally{
                                try{conn.close();}catch (Exception e){e.printStackTrace();}
                            }
                        %>
                    </select>
                </div>
                <div class="modal-footer">
                        <input type="button" class="btn btn-default" data-dismiss="modal" value="Close"/>
                        <input type="hidden" value="" class="hidden-move" name="hidden-move"/>
                        <input type="submit" class="btn btn-primary confirm-move" value="Confirm"/>
                </div>
            </form>
        </div>
    </div>
</div>

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
      <span style="text-align:right; cursor:pointer"><a style="text-decoration:none; color:black;" href="home.jsp">Back</a></span>
      <br/><br/>
      <span style="text-align:right; cursor:pointer" id="logout">Log out</span>
    </div>
  </div>
</div>
<div id="main-content" style="height:450px; width:100%; background-color: white; padding-top:50px;">
  <div class="main-content">
      <div class="project-tree-container">
        ${above_project_tree}
        <div class="project-tree">
          ${project}
        </div>
        ${below_project_tree}
      </div>
      <div class="file-viewer-container" style="display:none;">
        <div style="margin-top: 40px; font-size: 18px;"><span id="file-name-viewer"></span></div>
        <form action="/directory_actions" method="post">
          <div class="file-viewer-inner">
            <pre><code class="file-viewer" contenteditable="true"></code></pre>
            <textarea name="file-viewer-hidden" id="file-viewer-hidden" style="display:none"></textarea>
          </div>
          <div id="update-file-container" style="width:200px;">
            <input type="hidden" id="update-file-id" name="update-file-id" value=""/>
            <input id="update-file" name="update-file" value="Update" type="submit" style="width: 200px; height: 40px; font-size: 1em; border-radius: 5px; border-color: whitesmoke; background-color: #26C2F0; margin: 5px -1px;">
          </div>
        </form>
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
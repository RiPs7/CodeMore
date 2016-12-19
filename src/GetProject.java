import Core.*;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Created by periklismaravelias on 10/05/16.
 */
@WebServlet(name = "GetProject")
public class GetProject extends HttpServlet {
    private String project_owner = null, project_name = null;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("user") != null) {
            String username = ((User) request.getSession().getAttribute("user")).getUsername();
            LoadProject(request, response, username);
        } else {
            response.sendRedirect("login.jsp");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession().getAttribute("user") != null) {
            String username = ((User) request.getSession().getAttribute("user")).getUsername();
            LoadProject(request, response, username);
        } else {
            response.sendRedirect("login.jsp");
        }
    }

    private void LoadProject(HttpServletRequest request, HttpServletResponse response, String username) throws ServletException, IOException{
        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Directory> directory_tree = null;
        try{
            InitialContext ctx = new InitialContext();
            DataSource datasource = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
            conn = datasource.getConnection();
            stmt = conn.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=\"CodeMore\"");
            rs = stmt.executeQuery();
            String project_table = null;
            while(rs.next()){
                if (rs.getString(1).contains(username + Values.ProjectDS)) {
                    project_table = rs.getString(1);
                    project_owner = project_table.split(Values.ProjectDS)[0];
                    project_name = project_table.split(Values.ProjectDS)[1];
                    break;
                }
            }
            stmt = conn.prepareStatement("SELECT * FROM `" + project_table + "` ORDER BY `parent_id` ASC, `type` ASC, `name` DESC");
            rs = stmt.executeQuery();
            List<Directory> directories = new ArrayList<>();
            while (rs.next()){
                directories.add(new Directory(rs.getString(1), rs.getInt(2), rs.getBlob(3), rs.getInt(4), rs.getInt(5)));
            }
            directory_tree = PrepareDirectoryTree(directories);
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            String above_project_tree =
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/theme.css\">" +
                    "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css\">" +
                    "<div style=\"margin-bottom: 50px; text-align: center;\"><span style=\"font-size:20px;\"><b>" + project_name + " by " + project_owner + "</b></span><br/>" +
                    "<input class=\"project-add\" id=\"0\" value=\"Add Root Item\" title=\"Add Root Item\" style=\"border-radius: 5px; float: left; height: 30px; margin-top: 10px; margin-left: 10px;\" type=\"button\">" +
                    "</div>";
            request.setAttribute("above_project_tree", above_project_tree);
            request.setAttribute("project", FormatDirectoryStructure(directory_tree));
            String below_project_tree =
                    "<div style=\"width:200px;\"><input type=\"button\" id=\"delete-project\" value=\"Delete project\" style=\"width: 200px; height: 40px; margin-left: 10px; font-size: 1em; border-radius: 5px; border-color: whitesmoke; background-color: rgb(233, 71, 71);\"/></div>" +
                    "<div id=\"modal-delete\" class=\"modal fade\">\n" +
                    "    <div class=\"modal-dialog\">\n" +
                    "        <div class=\"modal-content\">\n" +
                    "            <div class=\"modal-header\">\n" +
                    "                <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n" +
                    "                <h4 class=\"modal-title\">Delete Project</h4>\n" +
                    "            </div>\n" +
                    "            <form id=\"form-project-delete\" method=\"post\" action=\"/directory_actions\"/>\n" +
                    "                <div class=\"modal-body\">\n" +
                    "                   <p>Are you sure you want to delete your whole project?</p>\n" +
                    "                   <p class=\"text-warning\"><small>This action cannot be undone!!!</small></p>\n" +
                    "                </div>\n" +
                    "                <div class=\"modal-footer\">\n" +
                    "                    <input type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\" value=\"Close\"/>\n" +
                    "                    <input type=\"hidden\" value=\"" + project_name +  "\" name=\"project-name\"/>\n" +
                    "                    <input type=\"submit\" class=\"btn btn-primary confirm-delete\" value=\"Confirm\" name=\"project-delete\"/>\n" +
                    "                </div>\n" +
                    "            </form>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</div>";
            request.setAttribute("below_project_tree", below_project_tree);
        }
        catch(com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException see){
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            String new_project =
                    "<input type=\"button\" id=\"new-project\" value=\"Create a new project\" style=\"width: 200px; height: 40px; font-size: 1em; border-radius: 5px; border-color: whitesmoke; background-color: rgb(89, 164, 188);\"/>" +
                    "<div id=\"modal-new\" class=\"modal fade\">\n" +
                    "    <div class=\"modal-dialog\">\n" +
                    "        <div class=\"modal-content\">\n" +
                    "            <div class=\"modal-header\">\n" +
                    "                <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>\n" +
                    "                <h4 class=\"modal-title\">Select Project Name</h4>\n" +
                    "            </div>\n" +
                    "            <form id=\"form-project-create\" method=\"post\" action=\"/directory_actions\"/>\n" +
                    "                <div class=\"modal-body\">\n" +
                    "                    <div id=\"add-project-details\">\n" +
                    "                        <input type=\"text\" name=\"project-name\" placeholder=\"Project Name\"/>\n" +
                    "                    </div>\n" +
                    "                </div>\n" +
                    "                <div class=\"modal-footer\">\n" +
                    "                    <input type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\" value=\"Close\"/>\n" +
                    "                    <input type=\"submit\" class=\"btn btn-primary confirm-create\" value=\"Create\" name=\"project-new\"/>\n" +
                    "                </div>\n" +
                    "            </form>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</div>";
            request.setAttribute("above_project_tree", new_project);
        }
        catch(Exception e){
            e.printStackTrace();
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
            request.getRequestDispatcher("myproject.jsp").forward(request, response);
        }
    }

    private List<Directory> PrepareDirectoryTree(List<Directory> directories){
        Stack<Directory> frontier = new Stack<>();
        List<Directory> result = new ArrayList<>();
        //Step 1 - Add root directory (virtual) to frontier
        Directory root = new Directory("root", Values.Folder, null, -1, 0);
        frontier.add(root);
        root.level = -1;
        //Step 2 - If frontier isn't empty...
        while (!frontier.empty()){
            //Step 3 - Pop current directory from frontier and set it to current directory
            Directory curr = frontier.pop();
            //Step 4 - Check if current directory exists in result set and keep going or repeat
            boolean exists_in_result = false;
            for (int i = 0; i < result.size(); i++){
                if (curr.id == result.get(i).id){
                    exists_in_result = true;
                }
            }
            if (exists_in_result){
                continue;
            }
            //Step 5 - * check for final state *
            //Step 6 - * more final states??   *
            //Step 7 - Add all children to the frontier
            for (int i = 0; i < directories.size(); i++){
                if (directories.get(i).parent_id == curr.id){
                    //Step 8
                    frontier.add(directories.get(i));
                    directories.get(i).level = curr.level + 1;
                }
            }
            //Step 9 - Add current directory to the result
            result.add(curr);
            //Step 10 - Repeat
        }
        //Remove virtual root directory
        result.remove(0);
        return result;
    }

    private String FormatDirectoryStructure(List<Directory> directory_tree){
        String result = "";
        for (int i = 0; i < directory_tree.size(); i++){
            Directory curr = directory_tree.get(i);
            result += "<div class=\"project-row\">";
            result += "<input class=\"project-remove\" id=\"directory-" + curr.id + "\" value=\"x\" title=\"Remove\" style=\"border-radius: 5px; float: right;\" type=\"button\">";
            result += "<input class=\"project-move\" id=\"directory-" + curr.id + "\" value=\"...\" title=\"Move\" style=\"border-radius: 5px; float: right;\" type=\"button\">";
            result += curr.type == Values.Folder ?
/* folder case */                  "<span class=\"folder level" + curr.level + "\">" + curr.name + "</span><input class=\"project-add\" id=\"" + curr.id + "\" value=\"+\" title=\"Add sub-item\" style=\"border-radius: 5px; float: right;\" type=\"button\">" :
/* file case */                    "<span class=\"file level" + curr.level + "\" id=\"file-" + curr.id + "\">" + curr.name + "</span>";
            result += "</div>";
        }
        return result;
    }
}

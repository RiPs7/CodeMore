import Core.Directory;
import Core.User;
import Core.Values;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Stack;

/**
 * Created by periklismaravelias on 13/05/16.
 */
@WebServlet(name = "DirectoryActions")
@MultipartConfig
public class DirectoryActions extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = ((User)request.getSession().getAttribute("user")).getUsername();
        response.setCharacterEncoding("UTF-8");
        if (request.getParameter("mode") != null){
            if (request.getParameter("mode").equals("view-file") && request.getParameter("id") != null) {
                if (request.getParameter("id").substring(0, "owner".length()).equals("owner")){
                    username = request.getParameter("id").substring("owner".length() + 1, request.getParameter("id").indexOf("-file-"));
                }
                String content = LoadFile(Integer.parseInt(request.getParameter("id").substring(request.getParameter("id").indexOf("file-") + "file-".length())), username);
                if (content == null){
                    response.setStatus(500);
                } else {
                    response.setStatus(200);
                }
                response.getWriter().write(content);
            }
            return;
        }
        if (request.getParameter("hidden-remove") != null){
            RemoveDirectoryTree(Integer.parseInt(request.getParameter("hidden-remove").replaceAll("directory-", "")), username);
        }
        if (request.getParameter("hidden-add") != null){
            String dir_type = request.getParameter("select-add-directory");
            String parent_id = request.getParameter("hidden-add");
            if (dir_type != null && parent_id != null) {
                if (dir_type.equals("folder") && request.getParameter("folder-name") != null) {
                    AddSubfolder(Integer.parseInt(parent_id), request.getParameter("folder-name"), username);
                } else if (dir_type.equals("file") && request.getPart("file-upload") != null) {
                    AddFile(Integer.parseInt(parent_id), request.getPart("file-upload"), username);
                }
            }
        }
        if (request.getParameter("hidden-move") != null && request.getParameter("select-move-directory") != null){
            MoveDirectory(Integer.parseInt(request.getParameter("hidden-move").replaceAll("directory-", "")), Integer.parseInt(request.getParameter("select-move-directory").replaceAll("directory-", "")), username);
        }
        if (request.getParameter("project-new") != null && request.getParameter("project-name") != null){
            CreateProject(request.getParameter("project-name"), username);
        }
        if (request.getParameter("project-delete") != null && request.getParameter("project-name") != null){
            DeleteProject(request.getParameter("project-name"), username);
        }
        if (request.getParameter("update-file") != null){
            if (request.getParameter("update-file-id") != null && request.getParameter("file-viewer-hidden") != null) {
                UpdateFile(Integer.parseInt(request.getParameter("update-file-id").replaceAll("file-", "")), request.getParameter("file-viewer-hidden"), username);
            }
        }
        request.getRequestDispatcher("getproject").forward(request, response);
    }

    private String LoadFile(int id, String username){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = null;
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
                    break;
                }
            }
            stmt = conn.prepareStatement("SELECT content FROM `" + project_table + "` WHERE id=?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            Blob b = null;
            while (rs.next()){
                b = rs.getBlob(1);
            }
            if (b == null){
                throw new Exception();
            }
            InputStream in = b.getBinaryStream();
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String read;
            while ((read = br.readLine()) != null){
                sb.append(read);
            }
            br.close();
            result = sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
            return result;
        }
    }

    private boolean UpdateFile(int id, String new_content, String username) {
        boolean result = true;
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
                if (rs.getString(1).contains(username + Values.ProjectDS)) {
                    project_table = rs.getString(1);
                    break;
                }
            }
            stmt = conn.prepareStatement("UPDATE `" + project_table + "` SET content=? WHERE id=?");
            Blob blob = conn.createBlob();
            blob.setBytes(1, new_content.getBytes("UTF-8"));
            stmt.setBlob(1, blob);
            stmt.setInt(2, id);
            stmt.execute();
        }catch(Exception e){
            e.printStackTrace();
            result = false;
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
            return result;
        }
    }

    private boolean CreateProject(String project_name, String username){
        boolean result = true;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            InitialContext ctx = new InitialContext();
            DataSource datasource = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
            conn = datasource.getConnection();
            stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `CodeMore`.`" + username + Values.ProjectDS + project_name + "` (`name` VARCHAR(50) NOT NULL, `type` INT NOT NULL, `content` BLOB NULL, `parent_id` INT NOT NULL, `id` INT  NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`, `parent_id`));");
            stmt.execute();
        }catch(Exception e){
            e.printStackTrace();
            result = false;
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
            return result;
        }
    }

    private boolean DeleteProject(String project_name, String username){
        boolean result = true;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            InitialContext ctx = new InitialContext();
            DataSource datasource = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
            conn = datasource.getConnection();
            stmt = conn.prepareStatement("DROP TABLE IF EXISTS `CodeMore`.`" + username + Values.ProjectDS + project_name + "`");
            stmt.execute();
        }catch(Exception e){
            e.printStackTrace();
            result = false;
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
            return result;
        }
    }

    private boolean RemoveDirectoryTree(int root_id, String username){
        boolean result = true;
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
                if (rs.getString(1).contains(username + Values.ProjectDS)) {
                    project_table = rs.getString(1);
                    break;
                }
            }
            Stack<Integer> parent_ids_to_delete = new Stack<>();
            parent_ids_to_delete.push(root_id);
            while (!parent_ids_to_delete.empty()) {
                int curr_parent_id = parent_ids_to_delete.pop();
                stmt = conn.prepareStatement("SELECT * FROM `" + project_table + "` WHERE parent_id=?");
                stmt.setInt(1, curr_parent_id);
                rs = stmt.executeQuery();
                while (rs.next()){
                    if (rs.getInt(2) == Values.Folder){
                        parent_ids_to_delete.push(rs.getInt(5));
                    }
                }
                stmt = conn.prepareStatement("DELETE FROM `" + project_table + "` WHERE id=?");
                stmt.setInt(1, curr_parent_id);
                stmt.execute();
                stmt = conn.prepareStatement("DELETE FROM `" + project_table + "` WHERE parent_id=?");
                stmt.setInt(1, curr_parent_id);
                stmt.execute();
            }
        }catch(Exception e){
            e.printStackTrace();
            result = false;
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
            return result;
        }
    }

    private boolean AddSubfolder(int parent_id, String folder_name, String username){
        boolean result = true;
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
                if (rs.getString(1).contains(username + Values.ProjectDS)) {
                    project_table = rs.getString(1);
                    break;
                }
            }
            stmt = conn.prepareStatement("INSERT INTO `" + project_table + "` VALUES(?, ?, ?, ?, NULL)");
            stmt.setString(1, folder_name);
            stmt.setInt(2, Values.Folder);
            stmt.setNull(3, Types.BLOB);
            stmt.setInt(4, parent_id);
            stmt.execute();
        }catch(Exception e){
            e.printStackTrace();
            result = false;
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
            return result;
        }
    }

    private boolean AddFile(int parent_id, Part file, String username){
        boolean result = true;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if (file.getSubmittedFileName().equals("")){
            return false;
        }
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
                    break;
                }
            }
            stmt = conn.prepareStatement("INSERT INTO `" + project_table + "` VALUES(?, ?, ?, ?, NULL)");
            stmt.setString(1, file.getSubmittedFileName());
            stmt.setInt(2, Values.File);
            stmt.setBlob(3, file.getInputStream());
            stmt.setInt(4, parent_id);
            stmt.execute();
        }catch(Exception e){
            e.printStackTrace();
            result = false;
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
            return result;
        }
    }

    private boolean MoveDirectory(int id, int new_parent_id, String username){
        boolean result = true;
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
                if (rs.getString(1).contains(username + Values.ProjectDS)) {
                    project_table = rs.getString(1);
                    break;
                }
            }
            stmt = conn.prepareStatement("UPDATE `" + project_table + "` SET parent_id=? WHERE id=?");
            stmt.setInt(1, new_parent_id);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
            result = false;
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
            return result;
        }
    }
}

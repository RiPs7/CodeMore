import Core.Directory;
import Core.User;
import Core.Values;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

/**
 * Created by PeriklisMaravelias on 12/22/15.
 */
@WebServlet(name = "Select")
public class Select extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String param = request.getParameter("chat");
            if (param != null){
                param = new String(Base64.decode(param.getBytes("UTF-8")));
            }
            System.out.println("here");
            if (param.contains(Values.ProjectDS)){
                System.out.println("here1");
                String partner_username = param.split(Values.ProjectDS)[0];
                if (!StartConversation(partner_username, request, response)){
                    throw new Exception();
                }
                String project_tree = null;
                if ((project_tree = LoadProject(param, request, response)) != null){
                    response.setStatus(200);
                    project_tree =
                            "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/theme.css\">" +
                            "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css\">" +
                            project_tree;
                    response.getWriter().write(partner_username + Values.ProjectDS + project_tree);
                } else {
                    throw new Exception();
                }
            } else if (param.contains("You have a new message from ")){ //selected a notification
                System.out.println("here2");
                String partner_username = param.contains("You have a new message from ") ? param.substring(param.indexOf("You have a new message from ") + "You have a new message from ".length()) : param;
                if (StartConversation(partner_username, request, response)){
                    response.setStatus(200);
                    response.getWriter().write(partner_username);
                } else {
                    throw new Exception();
                }
            } else {
                System.out.println("here3");
            }
        }
        catch(Exception e) {
            response.setStatus(500);
            response.getWriter().write(Values.InternalError);
        }

    }

    private String LoadProject(String project_table, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Directory> directory_tree = null;
        String result = "";
        try{
            InitialContext ctx = new InitialContext();
            DataSource datasource = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
            conn = datasource.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM `" + project_table + "` ORDER BY `parent_id` ASC, `type` ASC, `name` DESC");
            rs = stmt.executeQuery();
            List<Directory> directories = new ArrayList<>();
            while (rs.next()){
                directories.add(new Directory(rs.getString(1), rs.getInt(2), rs.getBlob(3), rs.getInt(4), rs.getInt(5)));
            }
            directory_tree = PrepareDirectoryTree(directories);
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            String project_tree = FormatDirectoryStructure(directory_tree, project_table.split(Values.ProjectDS)[0]);
            result = project_tree;
        }
        catch(com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException see){

        }
        catch(Exception e){
            e.printStackTrace();
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
        }
        return result;
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

    private String FormatDirectoryStructure(List<Directory> directory_tree, String owner){
        String result = "";
        for (int i = 0; i < directory_tree.size(); i++){
            Directory curr = directory_tree.get(i);
            result += "<div class=\"project-row\">";
            result += curr.type == Values.Folder ?
/* folder case */                  "<span class=\"folder level" + curr.level + "\">" + curr.name + "</span>" :
/* file case */                    "<span class=\"file level" + curr.level + "\" id=\"owner-" + owner + "-file-" + curr.id + "\">" + curr.name + "</span>";
            result += "</div>";
        }
        return result;
    }

    private PublicKey GetPartnerPublicKey(String partner_username) {
        DirContext context = null;
        try {
            Hashtable<String, String> env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, Values.LDAP_INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, Values.LDAP_PROVIDER_URL);
            env.put(Context.SECURITY_AUTHENTICATION, Values.LDAP_SECURITY_AUTHENTICATION);
            env.put(Context.SECURITY_PRINCIPAL, Values.LDAP_SECURITY_PRINCIPAL);
            env.put(Context.SECURITY_CREDENTIALS, Values.LDAP_SECURITY_CREDENTIALS);

            context = new InitialDirContext(env);

            SearchControls search_controls = new SearchControls();
            search_controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            search_controls.setTimeLimit(30000);

            NamingEnumeration<SearchResult> result = context.search("cn=" + partner_username + ",OU=Registered_Users", "objectClass=*" , search_controls);
            while (result.hasMoreElements())
            {
                SearchResult sr = result.nextElement();
                String keyPair = ((String)sr.getAttributes().get("description").get());
                String s_public_key = keyPair.substring(keyPair.indexOf("{RSAPublicKey}") + "{RSAPublicKey}".length(), keyPair.indexOf("{RSAPrivateKey}"));
                byte[] public_key = Base64.decode(s_public_key.getBytes());
                context.close();
                return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(public_key));
            }
            context.close();
            return null;

        } catch (Exception e) {
            try {context.close();}catch(Exception exc){}
            return null;
        }
    }

    private boolean StartConversation(String partner_username, HttpServletRequest request, HttpServletResponse response){
        try{
            request.getSession().setAttribute("partner_username", partner_username);
            PublicKey partner_public_key = GetPartnerPublicKey(partner_username);
            User user = (User)request.getSession().getAttribute("user");
            if (!GenerateSessionKey(user))
            {
                throw new Exception();
            }
            if (partner_public_key == null) {
                throw new Exception();
            }
            if (!EncrpytSendSessionKey(user, partner_username, partner_public_key))
            {
                throw new Exception();
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    private boolean GenerateSessionKey(User user) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);
            SecretKey key = keygen.generateKey();
            byte[] aesKey = key.getEncoded();
            SecretKey aesKeySpec = new SecretKeySpec(aesKey, "AES");
            user.setSessionKey(aesKeySpec.getEncoded());
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean EncrpytSendSessionKey(User user, String partner_username, PublicKey partner_public_key) {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, partner_public_key);
            user.setEncryptedSessionKeyPartner(c.doFinal(user.getSessionKey()));

            c = Cipher.getInstance("RSA");
            c.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(user.getPublicKey())));
            user.setEncryptedSessionKey(c.doFinal(user.getSessionKey()));

            InitialContext ctx = new InitialContext();
            DataSource datasource = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
            conn = datasource.getConnection();
            String table_name = "`" + user.getUsername() + Values.ChatDS + partner_username + "`";
            stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + table_name + " (`id` INT NOT NULL AUTO_INCREMENT, `from` VARCHAR(50), `to` VARCHAR(50), `message` BLOB, `status` INT, `encr_sess_key1` BLOB, `encr_sess_key2` BLOB, `time_sent` TIMESTAMP, PRIMARY KEY(`id`))");
            stmt.execute();
            stmt = conn.prepareStatement("INSERT INTO " + table_name + " VALUES (null, ?, ?, ?, null, ?, ?, CURRENT_TIMESTAMP())");
            stmt.setString(1, user.getUsername());
            stmt.setString(2, partner_username);
            stmt.setBytes(3, "hello message".getBytes("UTF-8"));
            stmt.setBytes(4, user.getEncryptedSessionKey());
            stmt.setBytes(5, user.getEncryptedSessionKeyPartner());
            stmt.execute();
            //in case of pending messages, update their status to received
            try {
                stmt = conn.prepareStatement("SET SQL_SAFE_UPDATES = 0");
                stmt.execute();
                stmt = conn.prepareStatement("UPDATE " + "`" + partner_username + Values.ChatDS + user.getUsername() + "`" + " SET `status` = ?, `time_sent` = `time_sent` WHERE `status` = ?");
                stmt.setInt(1, Values.Received);
                stmt.setInt(2, Values.Pending);
                stmt.executeUpdate();
                stmt = conn.prepareStatement("SET SQL_SAFE_UPDATES = 1");
                stmt.execute();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {conn.close();} catch(Exception e) {}
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

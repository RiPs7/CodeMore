import Core.Directory;
import Core.User;
import Core.Values;
import com.sun.org.apache.xml.internal.security.utils.Base64;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by PeriklisMaravelias on 12/5/15.
 */
@WebServlet(name = "Search")
public class Search extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //GetUsernames(request, response);
        GetProjects(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private void GetUsernames(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DirContext context = null;
        try
        {
            User user = (User)request.getSession().getAttribute("user");
            String query = request.getParameter("search_username").trim();

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

            String output = "";

            boolean found = false;
            NamingEnumeration<SearchResult> result = context.search("OU=Registered_Users", "(objectClass=person)", search_controls);
            if (query.matches("[\\s\\r\\n\\t]*"))
            {
                throw new javax.naming.NameNotFoundException();
            }
            while (result.hasMoreElements())
            {
                SearchResult sr = result.nextElement();
                String curr_cn = (String) sr.getAttributes().get("cn").get();
                if (curr_cn.toLowerCase().contains(query.toLowerCase()) && !curr_cn.equals(user.getUsername()))
                {
                    found = true;
                    output += "<a class=\"select_searched\" id=\"" + new String(Base64.encode(curr_cn.getBytes("UTF-8"))) + "_2\" style=\"color:black; text-decoration:underline; cursor:pointer;\">" + curr_cn + "</a><br/>";
                }
            }
            if (!found) {
                throw new javax.naming.NameNotFoundException();
            }
            output += "<span><br/>Select one of the above users<br/>to start chatting with...</span>";
            context.close();
            response.setStatus(200);
            response.getWriter().write(output);
        }
        catch (javax.naming.NameNotFoundException nnfe)
        {
            try {context.close();}catch(Exception exc){}
            nnfe.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("<span>your query returned no results.<br/>Boarden your search terms<br/>and try again.</span>");
        }
        catch (Exception e)
        {
            try {context.close();}catch(Exception exc){}
            response.setStatus(500);
            response.getWriter().write("<span style=\"color:red;\">Internal Error.</span>");
        }
    }

    private void GetProjects(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        User user = (User)request.getSession().getAttribute("user");
        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = request.getParameter("search_project").trim();
        String result = "";
        boolean found = false;
        int status = 200;
        try{
            if (query == null){
                throw new Exception();
            }
            if (query.matches("/^\\s+$/")){
                throw new Exception();
            }
            InitialContext ctx = new InitialContext();
            DataSource datasource = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
            conn = datasource.getConnection();
            stmt = conn.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=\"CodeMore\" AND LOWER(TABLE_NAME) LIKE '%" + query.toLowerCase() + "%'");
            rs = stmt.executeQuery();
            while(rs.next()){
                if (!rs.getString(1).contains(user.getUsername() + Values.ProjectDS) && rs.getString(1).contains(Values.ProjectDS)) {
                    found = true;
                    String project_owner = rs.getString(1).split(Values.ProjectDS)[0];
                    String project_name = rs.getString(1).split(Values.ProjectDS)[1];
                    result += "<a class=\"select_searched_project\" id=\"" + new String(Base64.encode((project_owner + Values.ProjectDS + project_name).getBytes("UTF-8"))) + "\" style=\"color:black; text-decoration:underline; cursor:pointer;\">" + project_name + " (by " + project_owner + ")</a><br/>";
                }
            }
            if (!found){
                status = 500;
                result = "<span>your query returned no results.<br/>Boarden your search terms<br/>and try again.</span>";
            }
        }
        catch(Exception e){
            status = 500;
            result = "<span style=\"color:red;\">Internal Error.</span>";
            e.printStackTrace();
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
            response.setStatus(status);
            response.getWriter().write(result);
        }
    }
}

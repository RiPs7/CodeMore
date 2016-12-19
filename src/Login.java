import Core.Admin;
import Core.User;
import Core.Values;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.naming.Context;
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
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Hashtable;

/**
 * Created by PeriklisMaravelias on 11/23/15.
 */
@WebServlet(name = "Login")
public class Login extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = null, password = null, admin_username = null, admin_password = null;
        DirContext context = null;
        try
        {
            username = request.getParameter("username");
            password = request.getParameter("password");
            admin_username = request.getParameter("admin-username");
            admin_password = request.getParameter("admin-password");

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password == null ? (admin_password + Values.Salt).getBytes("UTF-8") : (password + Values.Salt).getBytes("UTF-8"));
            String pass_to_check = "{MD5}" + Base64.encode(md.digest());

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

            NamingEnumeration<SearchResult> result = context.search("cn=" + (username == null ? admin_username : username) + (username == null ? ",OU=Administrators" : ",OU=Registered_Users"), "objectClass=*" , search_controls);
            while (result.hasMoreElements())
            {
                SearchResult sr = result.nextElement();
                String user_pass = new String((byte[])(sr.getAttributes().get("userPassword").get()));
                if (pass_to_check.equals(user_pass))
                {
                    if (username != null) {
                        User user = new User();
                        user.setFirstname((String)sr.getAttributes().get("givenName").get());
                        user.setLastname((String)sr.getAttributes().get("sn").get());
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setMail((String)sr.getAttributes().get("mail").get());
                        String keyPair = ((String) sr.getAttributes().get("description").get());
                        user.setPublicKey(com.sun.org.apache.xml.internal.security.utils.Base64.decode(keyPair.substring(keyPair.indexOf("{RSAPublicKey}") + "{RSAPublicKey}".length(), keyPair.indexOf("{RSAPrivateKey}")).getBytes()));
                        user.setPrivateKey(com.sun.org.apache.xml.internal.security.utils.Base64.decode(keyPair.substring(keyPair.indexOf("{RSAPrivateKey}") + "{RSAPrivateKey}".length()).getBytes()));
                        request.getSession().setAttribute("user", user);
                        response.sendRedirect("home.jsp");
                    } else {
                        Admin admin = new Admin();
                        admin.setUsername(admin_username);
                        admin.setPassword(admin_password);
                        admin.setMail((String)sr.getAttributes().get("mail").get());
                        request.getSession().setAttribute("admin", admin);
                        response.sendRedirect("backend.jsp");
                    }
                }
                else
                {
                    response.sendRedirect((username == null ? "admin.jsp" : "login.jsp") + "?error=" + Values.CredentialCombinationNotFound);
                }
            }
            context.close();
        }
        catch (javax.naming.NameNotFoundException nnfe)
        {
            try {context.close();}catch(Exception exc){}
            nnfe.printStackTrace();
            response.sendRedirect((username == null ? "admin.jsp" : "login.jsp") + "?error=" + Values.CredentialCombinationNotFound);
        }
        catch (Exception e)
        {
            try {context.close();}catch(Exception exc){}
            e.printStackTrace();
            response.sendRedirect((username == null ? "admin.jsp" : "login.jsp") + "?error=" + Values.InternalError);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

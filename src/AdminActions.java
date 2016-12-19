import Core.Values;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Hashtable;

/**
 * Created by PeriklisMaravelias on 1/1/16.
 */
@WebServlet(name = "AdminActions")
public class AdminActions extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action){
            case "add-admin":
                if (AddAdmin(request)){
                    response.getWriter().write("<span>The new administrator was added successfully!</span>");
                    return;
                } else {
                    response.setStatus(500);
                    return;
                }
            case "show-users":
                response.getWriter().write(ShowUsers());
                break;
            case "delete-user":
                if (DeleteUser(request.getParameter("who"))) {
                    response.getWriter().write("<div style=\"background-color:lightgreen; width:40%; height:15px; border-radius:5px; margin-bottom:10px; padding:5px;\">Successfully deleted!</div>");
                } else {
                    response.setStatus(500);
                }
                break;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private boolean AddAdmin(HttpServletRequest request){
        DirContext context = null;
        try {
            String[] formdata = request.getParameter("formdata").split("&");
            if (formdata.length != 6){
                throw new Exception();
            }
            for (int i = 0; i < formdata.length; i++){
                formdata[i] = java.net.URLDecoder.decode(formdata[i], "UTF-8");
            }
            String firstname = formdata[0].substring(formdata[0].indexOf("=") + 1), lastname = formdata[1].substring(formdata[1].indexOf("=") + 1);
            String username = formdata[2].substring(formdata[2].indexOf("=") + 1), password = formdata[3].substring(formdata[3].indexOf("=") + 1), re_password = formdata[4].substring(formdata[4].indexOf("=") + 1);
            String email = formdata[5].substring(formdata[5].indexOf("=") + 1);

            if (!password.equals(re_password)){
                throw new Exception();
            }

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((password + Values.Salt).getBytes("UTF-8"));
            String pass_to_save = "{MD5}" + com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(md.digest());

            Hashtable<String, String> env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, Values.LDAP_INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, Values.LDAP_PROVIDER_URL);
            env.put(Context.SECURITY_AUTHENTICATION, Values.LDAP_SECURITY_AUTHENTICATION);
            env.put(Context.SECURITY_PRINCIPAL, Values.LDAP_SECURITY_PRINCIPAL);
            env.put(Context.SECURITY_CREDENTIALS, Values.LDAP_SECURITY_CREDENTIALS);

            context = new InitialDirContext(env);

            Attributes attr = new BasicAttributes();
            Attribute objectClass = new BasicAttribute("objectClass");
            objectClass.add("top");
            objectClass.add("person");
            attr.put(objectClass);
            attr.put(new BasicAttribute("cn", firstname + " " + lastname));
            attr.put(new BasicAttribute("mail", email));
            attr.put(new BasicAttribute("userPassword", pass_to_save));

            try
            {
                context.createSubcontext("cn=" + username.trim() + ",OU=Administrators", attr);
                context.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                context.close();
                return false;
            }
        } catch (Exception e) {
            try {context.close();}catch(Exception exc){}
            return false;
        }
        return true;
    }

    private String ShowUsers(){
        DirContext context = null;
        String output = "";
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

            NamingEnumeration<SearchResult> result = context.search("OU=Administrators", "(objectClass=top)", search_controls);
            output += "<div id=\"delete-user-result\" style=\"width:40%; height:40px; display:none; background-color:orange; border-radius:5px;\"></div><br/><span style=\"text-decoration:underline\">Administrators</strong></span><br/><div>";
            while (result.hasMoreElements())
            {
                SearchResult sr = result.nextElement();
                output += "<div style=\"width:100%; height:60px;\"><div style=\"float:left\">Username: " + sr.getName().substring(3) + "<br/>Common Name: " + sr.getAttributes().get("cn").get() + "<br/>Email: " + sr.getAttributes().get("mail").get() + "<br/></div><div style=\"float:right\"><input type=\"button\" class=\"delete-btn\" id=\"delete-admin-" + sr.getName().substring(3) + "\" value=\"Delete Admin\" style=\"margin-right:10px;\"></div></div>";
            }
            output += "</div>";
            output += "<br/><span style=\"text-decoration:underline\">Users</strong></span><br/><div>";
            result = context.search("OU=Registered_Users", "(objectClass=person)", search_controls);
            while (result.hasMoreElements())
            {
                SearchResult sr = result.nextElement();
                output += "<div style=\"width:100%; height:75px;\"><div style=\"float:left\">Username: " + sr.getName().substring(3) + "<br/>First Name: " + sr.getAttributes().get("givenName").get() + "<br/>Last Name: " + sr.getAttributes().get("sn").get() + "<br/>Email: " + sr.getAttributes().get("mail").get() + "<br/></div><div style=\"float:right\"><input type=\"button\" class=\"delete-btn\" id=\"delete-user-" + sr.getName().substring(3) + "\" value=\"Delete User\" style=\"margin-right:10px;\"></div></div>";
            }
            output += "</div>";
            context.close();
        } catch (Exception e) {
            try {context.close();}catch(Exception exc){}
            return "An error occured. Please try again later or contact the system administrators.";
        }
        return output;
    }

    private boolean DeleteUser(String who){
        String kind = who.split("-")[1];
        String username = who.split("-")[2];
        DirContext context = null;
        try {
            Hashtable<String, String> env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, Values.LDAP_INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, Values.LDAP_PROVIDER_URL);
            env.put(Context.SECURITY_AUTHENTICATION, Values.LDAP_SECURITY_AUTHENTICATION);
            env.put(Context.SECURITY_PRINCIPAL, Values.LDAP_SECURITY_PRINCIPAL);
            env.put(Context.SECURITY_CREDENTIALS, Values.LDAP_SECURITY_CREDENTIALS);

            context = new InitialDirContext(env);
String s = "cn=" + username + ",OU=" + (kind.equals("admin") ? "Administrators" : "Registered_Users");
            context.destroySubcontext(s);
            context.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {context.close();}catch(Exception exc){}
            return false;
        }
    }
}

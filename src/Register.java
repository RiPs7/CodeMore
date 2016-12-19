/**
 * Created by PeriklisMaravelias on 11/23/15.
 */
import Core.User;
import Core.Values;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.naming.Context;
import javax.naming.directory.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.util.Hashtable;

/**
 * Created by PeriklisMaravelias on 11/23/15.
 */
@WebServlet(name = "Register")
public class Register extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DirContext context = null;
        try {
            String firstname = request.getParameter("firstname"), lastname = request.getParameter("lastname");
            String username = request.getParameter("reg_username"), password = request.getParameter("reg_password"), re_password = request.getParameter("reg_re_password");
            String email = request.getParameter("reg_email");

            if (!password.equals(re_password)){
                throw new IllegalArgumentException();
            }

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((password + Values.Salt).getBytes("UTF-8"));
            String pass_to_save = "{MD5}" + Base64.encode(md.digest());

            Hashtable<String, String> env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, Values.LDAP_INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, Values.LDAP_PROVIDER_URL);
            env.put(Context.SECURITY_AUTHENTICATION, Values.LDAP_SECURITY_AUTHENTICATION);
            env.put(Context.SECURITY_PRINCIPAL, Values.LDAP_SECURITY_PRINCIPAL);
            env.put(Context.SECURITY_CREDENTIALS, Values.LDAP_SECURITY_CREDENTIALS);

            context = new InitialDirContext(env);

            Attributes attr = new BasicAttributes();
            attr.put(new BasicAttribute("objectClass", "person"));
            attr.put(new BasicAttribute("cn", username));
            attr.put(new BasicAttribute("givenName", firstname));
            attr.put(new BasicAttribute("sn", lastname));
            attr.put(new BasicAttribute("mail", email));
            attr.put(new BasicAttribute("userPassword", pass_to_save));

            try
            {
                BasicAttribute keypair = CreateKeyPair();
                if (keypair.equals(null))
                {
                    throw new Exception();
                }
                attr.put(keypair);
                context.createSubcontext("cn=" + username.trim() + ",OU=Registered_Users", attr);

                User user = new User();
                user.setFirstname(firstname);
                user.setLastname(lastname);
                user.setUsername(username);
                user.setPassword(password);
                user.setMail(email);
                String keyPair = (String)keypair.get();
                String publicKey = keyPair.substring(keyPair.indexOf("{RSAPublicKey}") + "{RSAPublicKey}".length(), keyPair.indexOf("{RSAPrivateKey}"));
                String privateKey = keyPair.substring(keyPair.indexOf("{RSAPrivateKey}") + "{RSAPrivateKey}".length());
                user.setPublicKey(publicKey.getBytes());
                user.setPrivateKey(privateKey.getBytes());
                request.getSession().setAttribute("user", user);
                response.sendRedirect("home.jsp");
                context.close();
            }
            catch (Exception e)
            {
                try {context.close();}catch(Exception exc){}
                e.printStackTrace();
                response.sendRedirect("login.jsp?error=" + Values.InternalError);
            }
        }
        catch (IllegalArgumentException iae){
            try {context.close();}catch(Exception exc){}
            iae.printStackTrace();
            response.sendRedirect("login.jsp?error=" + Values.PasswordsNotMatch);
        }
        catch (Exception e)
        {
            try {context.close();}catch(Exception exc){}
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=" + Values.InternalError);
        }

    }

    private BasicAttribute CreateKeyPair()
    {
        KeyPairGenerator kpg;
        try
        {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            String publicKey = Base64.encode(kp.getPublic().getEncoded());
            String privateKey = Base64.encode(kp.getPrivate().getEncoded());
            return new BasicAttribute("description", "{RSAPublicKey}" + publicKey + "{RSAPrivateKey}" + privateKey);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

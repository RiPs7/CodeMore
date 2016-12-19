import Core.User;
import Core.Values;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by PeriklisMaravelias on 12/26/15.
 */
@WebServlet(name = "Send")
public class Send extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String message = request.getParameter("message");
            String partner_username = (String) request.getSession().getAttribute("partner_username");
            User user = (User) request.getSession().getAttribute("user");
            if (!SendMessage(user, partner_username, message)) {
                throw new Exception();
            }
            response.getWriter().write("ok");
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private boolean SendMessage(User user, String partner_username, String message) {
        Connection conn = null;
        try {
            byte[] encrypted_message = EncryptMessage(user, message);
            if (encrypted_message == null) {
                throw new Exception();
            }
            InitialContext ctx = new InitialContext();
            DataSource datasource = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
            conn = datasource.getConnection();
            String table_name = "`" + user.getUsername() + Values.ChatDS + partner_username + "`";
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + table_name + " VALUES (null, ?, ?, ?, 1, ?, ?, CURRENT_TIMESTAMP())");
            stmt.setString(1, user.getUsername());
            stmt.setString(2, partner_username);
            stmt.setBytes(3, encrypted_message);
            stmt.setBytes(4, user.getEncryptedSessionKey());
            stmt.setBytes(5, user.getEncryptedSessionKeyPartner());
            stmt.execute();
            conn.close();
            return true;
        } catch (Exception e) {
            try {conn.close();} catch(Exception ex) {ex.printStackTrace();};
            e.printStackTrace();
            return false;
        }
    }

    private byte[] EncryptMessage(User user, String message) {
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(user.getSessionKey(), 0, user.getSessionKey().length, "AES"));
            return c.doFinal(message.getBytes("UTF-8"));
        } catch (Exception e) {
            return null;
        }
    }
}

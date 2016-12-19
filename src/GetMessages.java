import Core.Message;
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
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by PeriklisMaravelias on 12/27/15.
 */
@WebServlet(name = "GetMessages")
public class GetMessages extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User)request.getSession().getAttribute("user");
        String partner_username = (String)request.getSession().getAttribute("partner_username");
        String table1 = user.getUsername() + Values.ChatDS + partner_username;
        String table2 = partner_username + Values.ChatDS + user.getUsername();
        Connection conn = null;
        List<Message> messages1 = new ArrayList<>(), messages2 = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        byte[] curr_encr_session_key = null;
        byte[] curr_session_key = null;
        try{
            InitialContext ctx = new InitialContext();
            DataSource datasource = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
            conn = datasource.getConnection();
            stmt = conn.prepareStatement("SELECT `from`, `to`, `message`, `encr_sess_key1`, `time_sent` FROM " + table1 + " WHERE status = " + Values.Received + " OR status =" + Values.Pending + " ORDER BY time_sent ASC");
            rs = stmt.executeQuery();
            while(rs.next()){
                if (!Arrays.equals(curr_encr_session_key, rs.getBytes(4))){
                    curr_encr_session_key = rs.getBytes(4);
                    curr_session_key = DecryptSessionKey(user, rs.getBytes(4));
                }
                messages1.add(new Message(DecryptMessage(rs.getBytes(3), curr_session_key), rs.getString(1), rs.getString(2), rs.getTimestamp(5)));
            }
            stmt = conn.prepareStatement("SELECT `from`, `to`, `message`, `encr_sess_key2`, `time_sent` FROM " + table2 + " WHERE status = " + Values.Received + " OR status =" + Values.Pending + " ORDER BY time_sent ASC");
            rs = stmt.executeQuery();
            while(rs.next()){
                if (!Arrays.equals(curr_session_key, rs.getBytes(4))){
                    curr_session_key = DecryptSessionKey(user, rs.getBytes(4));
                }
                messages2.add(new Message(DecryptMessage(rs.getBytes(3), curr_session_key), rs.getString(1), rs.getString(2), rs.getTimestamp(5)));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{conn.close();}catch (Exception e){e.printStackTrace();}
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            if (messages1.size() == 0 && messages2.size() == 0){
                response.getWriter().write("");
            }
            else if (messages1.size() == 0){
                response.getWriter().write(FormatResponse(messages2, user));
            }
            else if (messages2.size() == 0){
                response.getWriter().write(FormatResponse(messages1, user));
            }else{
                response.getWriter().write(FormatResponse(Interleave(messages1, messages2), user));
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private byte[] DecryptSessionKey(User user, byte[] encr_sess_key){
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(user.getPrivateKey())));
            return c.doFinal(encr_sess_key);
        }catch(Exception e){
            return null;
        }
    }

    private String DecryptMessage(byte[] message, byte[] sess_key){
        try {
            if (Arrays.equals("hello message".getBytes("UTF-8"), message))
            {
                return null;
            }
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sess_key, 0, sess_key.length, "AES"));
            return new String(c.doFinal(message), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    private List<Message> Interleave(List<Message> messages1, List<Message> messages2){
        List<Message> result = new ArrayList<>();
        int n = messages1.size(), m = messages2.size();
        int i = 0, j = 0;
        while (i < n && j < m){
            if (messages1.get(i).getTime_sent().getTime() < messages2.get(j).getTime_sent().getTime()){
                result.add(messages1.get(i));
                i++;
            }else{
                result.add(messages2.get(j));
                j++;
            }
        }
        while (i < n){
            result.add(messages1.get(i));
            i++;
        }
        while (j < m){
            result.add(messages2.get(j));
            j++;
        }
        return result;
    }

    private String FormatResponse(List<Message> messages, User user){
        int n = messages.size(), i;
        String result = "";
        for (i = 0; i < n ; i++){
            if (messages.get(i).getContent() == null) {continue;}
            result += "<div style=\"width:100%;\"><div class=\"" + (messages.get(i).getFrom().equals(user.getUsername()) ? "message" : "partner_message") + "\">" + messages.get(i).getContent() + "</div></div>";
        }
        return result;
    }
}

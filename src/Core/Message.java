package Core;

import java.sql.Timestamp;

/**
 * Created by PeriklisMaravelias on 12/28/15.
 */
public class Message {
    String content, from, to;
    Timestamp time_sent;

    public Message(String content, String from, String to, Timestamp time_sent){
        this.content = content;
        this.from = from;
        this.to = to;
        this.time_sent = time_sent;
    }

    public String getContent() {
        return content;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Timestamp getTime_sent() {
        return time_sent;
    }
}

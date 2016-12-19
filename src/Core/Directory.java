package Core;

import java.sql.Blob;

/**
 * Created by periklismaravelias on 10/05/16.
 */
public class Directory {
    public String name;
    public int type, parent_id, id;
    public Blob content;
    public int level;

    public Directory(String name, int type, Blob content, int parent_id, int id){
        this.name = name;
        this.type = type;
        this.parent_id = parent_id;
        this.id = id;
        this.content = content;
    }
}

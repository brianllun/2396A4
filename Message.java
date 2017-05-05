import java.util.*;
import java.io.*;

@SuppressWarnings("serial")
public class Message implements Serializable{
    public String sender;
    public String receiver;
    public String command;
    public String data_iname;
    public String data_blockno;
    public Object data_content;
    public HashMap<String, String> peer_list = new HashMap<String, String>();
    public Message(){
        sender = null;
        receiver = null;
        command = null;
        data_iname = null;
        data_blockno = null;
        data_content = null;
    }
}

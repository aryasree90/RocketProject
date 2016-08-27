package rocket.club.com.rocketpoker.classes;

/**
 * Created by Admin on 8/27/2016.
 */
public class ChatListClass {

    private String senderMob;
    private String msg;
    private long time;
    private String location;

    public String getSenderMob() {
        return senderMob;
    }

    public void setSenderMob(String senderMob) {
        this.senderMob = senderMob;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

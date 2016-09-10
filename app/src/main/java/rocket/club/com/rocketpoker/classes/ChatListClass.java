package rocket.club.com.rocketpoker.classes;

import com.google.gson.JsonObject;

/**
 * Created by Admin on 8/27/2016.
 */
public class ChatListClass {

    private String senderMob;
    private String msg;
    private long time;
    private LocationClass location;
    private String msgId;

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

    public LocationClass getLocation() {
        return location;
    }

    public void setLocation(LocationClass location) {
        this.location = location;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}

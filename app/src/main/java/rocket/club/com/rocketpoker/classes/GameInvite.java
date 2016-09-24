package rocket.club.com.rocketpoker.classes;

/**
 * Created by Admin on 9/19/2016.
 */
public class GameInvite {

    private String senderMob;
    private String inviteList;
    private String game;
    private String schedule;
    private String timeStamp;
    private int count;
    private int status = -1;

    public GameInvite(String senderMob, String game, String schedule) {
        this.senderMob = senderMob;
        this.game = game;
        this.schedule = schedule;
    }

    public GameInvite(String senderMob, String inviteList, String game, String schedule, String timeStamp, int count) {
        this.senderMob = senderMob;
        this.inviteList = inviteList;
        this.game = game;
        this.schedule = schedule;
        this.timeStamp = timeStamp;
        this.count = count;
    }

    public GameInvite(String senderMob, String inviteList, String game, String schedule, String timeStamp, int count, int status) {
        this.senderMob = senderMob;
        this.inviteList = inviteList;
        this.game = game;
        this.schedule = schedule;
        this.timeStamp = timeStamp;
        this.count = count;
        this.status = status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSenderMob() {
        return senderMob;
    }

    public String getGame() {
        return game;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getInviteList() {
        return inviteList;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getCount() {
        return count;
    }

    public int getStatus() {
        return status;
    }
}

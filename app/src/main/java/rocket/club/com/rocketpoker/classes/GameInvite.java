package rocket.club.com.rocketpoker.classes;

/**
 * Created by Admin on 9/19/2016.
 */
public class GameInvite {

    private String senderMob;
    private String game;
    private String schedule;
    private int status = -1;

    public GameInvite(String senderMob, String game, String schedule) {
        this.senderMob = senderMob;
        this.game = game;
        this.schedule = schedule;
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

    public int getStatus() {
        return status;
    }
}

package rocket.club.com.rocketpoker.classes;

/**
 * Created by Admin on 11/6/2016.
 */
public class LiveUpdateDetails {

    private int updateType;
    private String updateHeader;
    private String updateText1;
    private String updateText2;
    private String updateText3;
    private String updateComments;
    private String updateTimeStamp;

    public LiveUpdateDetails(int type, String header, String text1, String text2, String text3,
                              String comments, String timeStamp) {
        this.updateType = type;
        this.updateHeader = header;
        this.updateText1 = text1;
        this.updateText2 = text2;
        this.updateText3 = text3;
        this.updateComments = comments;
        this.updateTimeStamp = timeStamp;
    }

    public int getUpdateType() {
        return updateType;
    }

    public String getUpdateHeader() {
        return updateHeader;
    }

    public String getUpdateText1() {
        return updateText1;
    }

    public String getUpdateText2() {
        return updateText2;
    }

    public String getUpdateText3() {
        return updateText3;
    }

    public String getUpdateComments() {
        return updateComments;
    }

    public String getUpdateTimeStamp() {
        return updateTimeStamp;
    }
}

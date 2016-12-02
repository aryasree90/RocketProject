package rocket.club.com.rocketpoker.classes;

/**
 * Created by Admin on 11/6/2016.
 */
public class LiveUpdateDetails {

    private String type;
    private String header;
    private String text1;
    private String text2;
    private String text3;
    private String comments;
    private String timeStamp;

    public LiveUpdateDetails(String type, String header, String text1, String text2, String text3,
                              String comments, String timeStamp) {
        this.type = type;
        this.header = header;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.comments = comments;
        this.timeStamp = timeStamp;
    }

    public String getUpdateType() {
        return type;
    }

    public String getUpdateHeader() {
        return header;
    }

    public String getUpdateText1() {
        return text1;
    }

    public String getUpdateText2() {
        return text2;
    }

    public String getUpdateText3() {
        return text3;
    }

    public String getUpdateComments() {
        return comments;
    }

    public String getUpdateTimeStamp() {
        return timeStamp;
    }
}

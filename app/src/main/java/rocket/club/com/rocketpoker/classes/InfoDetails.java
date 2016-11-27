package rocket.club.com.rocketpoker.classes;

/**
 * Created by Admin on 10/12/2016.
 */
public class InfoDetails {

    private String id;
    private String infoImage;
    private String infoTitle;
    private String infoSubTitle;
    private String infoLikeStatus;
    private String infoEditor;
    private String infoTimeStamp;
    private String infoMsgType;

    public InfoDetails(String id, String infoImage, String infoTitle, String infoSubTitle,
                            String infoLikeStatus, String infoEditor, String infoTimeStamp,
                            String infoMsgType) {

        this.id = id;
        this.infoImage = infoImage;
        this.infoTitle = infoTitle;
        this.infoSubTitle = infoSubTitle;
        this.infoLikeStatus = infoLikeStatus;
        this.infoEditor = infoEditor;
        this.infoTimeStamp = infoTimeStamp;
        this.infoMsgType = infoMsgType;
    }

    public void setInfoLikeStatus(String infoLikeStatus) {
        this.infoLikeStatus = infoLikeStatus;
    }

    public String getInfoImage() {
        return infoImage;
    }

    public String getInfoTitle() {
        return infoTitle;
    }

    public String getInfoSubTitle() {
        return infoSubTitle;
    }

    public String getInfoLikeStatus() {
        return infoLikeStatus;
    }

    public String getInfoEditor() {
        return infoEditor;
    }

    public String getInfoTimeStamp() {
        return infoTimeStamp;
    }

    public String getInfoMsgType() {
        return infoMsgType;
    }

    public String getId() {
        return id;
    }
}

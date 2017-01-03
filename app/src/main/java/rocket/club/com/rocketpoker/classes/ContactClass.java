package rocket.club.com.rocketpoker.classes;

public class ContactClass {

    private String contactName;
    private String phoneNumber;
    private String nickName;
    private String userImage;
    private int status;

    //    getter functions

    public String getContactName() {
        return contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public int getStatus() {
        return status;
    }

    public String getUserImage() {
        return userImage;
    }

    //    setter functions

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}

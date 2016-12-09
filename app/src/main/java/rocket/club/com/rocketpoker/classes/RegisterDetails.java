package rocket.club.com.rocketpoker.classes;

/**
 * Created by Admin on 12/4/2016.
 */
public class RegisterDetails {

    private String id;
    private String gcm_regid;
    private String reg_mob;
    private String name;
    private String dob;
    private String gender;
    private String email;
    private String nickname;
    private String gametype;
    private String user_pic;
    private String created_at;
    private String userId;
    private String verified;
    private String location;
    private String user_type;

    public String getId() {
        return id;
    }

    public String getGcm_regid() {
        return gcm_regid;
    }

    public String getReg_mob() {
        return reg_mob;
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGametype() {
        return gametype;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUserId() {
        return userId;
    }

    public String getVerified() {
        return verified;
    }

    public String getLocation() {
        return location;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
}

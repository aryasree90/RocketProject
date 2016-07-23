package rocket.club.com.rocketpoker.classes;

/**
 * Created by Admin on 7/22/2016.
 */
public class FriendsListClass {

    private int id;
    private String name;
    private String mobile;
    private String image;

    public FriendsListClass(int id, String name, String mobile, String image) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.image = image;
    }

    public FriendsListClass(String name, String mobile, String image) {
        this.name = name;
        this.mobile = mobile;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getImage() {
        return image;
    }
}

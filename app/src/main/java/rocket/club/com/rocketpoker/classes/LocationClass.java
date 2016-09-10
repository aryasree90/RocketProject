package rocket.club.com.rocketpoker.classes;

/**
 * Created by Admin on 9/5/2016.
 */
public class LocationClass {

    private String lat;
    private String lng;
    private String loc_name;
    private long timeStamp;

    public void setLocation(String lat, String lng, String loc_name, long timeStamp) {
        this.lat = lat;
        this.lng = lng;
        this.loc_name = loc_name;
        this.timeStamp = timeStamp;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getLoc_name() {
        return loc_name;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}

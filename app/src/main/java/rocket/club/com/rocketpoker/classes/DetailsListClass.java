package rocket.club.com.rocketpoker.classes;

/**
 * Created by Admin on 1/22/2017.
 */
public class DetailsListClass {

    private String item1;
    private String item2;
    private String item3;
    private String item4;
    private String item5;
    private String item6;

    public void setItems(String item1, String item2, String item3) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
    }

    public void setExtraItems(String item4, String item5, String item6) {
        this.item4 = item4;
        this.item5 = item5;
        this.item6 = item6;
    }

    public String getItem1() {
        return item1;
    }

    public String getItem2() {
        return item2;
    }

    public String getItem3() {
        return item3;
    }

    public String getItem4() {
        return item4;
    }

    public String getItem5() {
        return item5;
    }

    public String getItem6() {
        return item6;
    }
}

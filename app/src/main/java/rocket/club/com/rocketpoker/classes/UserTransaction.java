package rocket.club.com.rocketpoker.classes;

/**
 * Created by Admin on 12/25/2016.
 */
public class UserTransaction {

    private String id;
    private String reg_mob;
    private String trans_type;
    private String amount;
    private String timeStamp;
    private String avail_credit;
    private String bonus;
    private String cashier_mob;

    public String getId() {
        return id;
    }

    public String getReg_mob() {
        return reg_mob;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public String getAmount() {
        return amount;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getAvail_credit() {
        return avail_credit;
    }

    public String getBonus() {
        return bonus;
    }

    public String getCashier_mob() {
        return cashier_mob;
    }
}

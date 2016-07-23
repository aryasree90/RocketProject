package rocket.club.com.rocketpoker.classes;

import android.database.Cursor;

import java.util.ArrayList;

public class ContactHelper {

    private int curPos;
    private int total;
    private boolean atLast = false;
    private boolean isFirst = false;
    private ArrayList<ContactClass> contactList;
    private Cursor cursor;

    public int getCurPos() {
        return curPos;
    }

    public int getTotal() {
        return total;
    }

    public ArrayList<ContactClass> getContactList() {
        return contactList;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public boolean isAtLast() {
        return atLast;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setCurPos(int curPos) {
        this.curPos = curPos;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setContactList(ArrayList<ContactClass> contactList) {
        this.contactList = contactList;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public void setAtLast(boolean atLast) {
        this.atLast = atLast;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }
}

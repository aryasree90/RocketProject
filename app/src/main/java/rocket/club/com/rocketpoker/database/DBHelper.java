package rocket.club.com.rocketpoker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import rocket.club.com.rocketpoker.classes.ChatListClass;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "rocketPoker.db";
    public static final int DB_VERSION = 1;
    private static final String TAG = "DBHelper";

    public static final String friendsTable = "friendsTable";
    public static final String messageTable = "messageTable";

    public static final String _id = "id";
    public static final String mobile = "frndMobile";
    public static final String userName = "frndName";
    public static final String nickName = "frndNickName";
    public static final String status = "status";

    public static final String msgId = "msgId";
    public static final String senderMob = "senderMob";
    public static final String message = "message";
    public static final String time = "time";
    public static final String location = "location";

    public static final String CREATE_TABLE = "CREATE TABLE " + friendsTable + "(" + _id +
            " integer primary key autoincrement not null, " + mobile + " text, " + userName +
            " text, " + nickName + " text, " + status + " integer)";

    public static final String CREATE_MSG_TABLE = "CREATE TABLE " + messageTable + "(" + _id +
            " integer primary key autoincrement not null, " + msgId + " integer, " + senderMob +
            " text, " + message + " text, " + time + " text, " + location + " integer)";

    public static final String SELECT_ALL_FRIENDS = "SELECT * FROM " + friendsTable;
    public static final String SELECT_PENDING = "SELECT * FROM " + friendsTable + " WHERE " +
            status + "=0";
    public static final String SELECT_FRIENDS = "SELECT * FROM " + friendsTable + " WHERE " +
            status + "=1";

    public static final String SELECT_FRIENDS_USING_MOB = "SELECT * FROM " + friendsTable + " WHERE " +
            mobile + "=?";

    public static final String SELECT_ALL_MESSAGES = "SELECT * FROM " + messageTable;

    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_MSG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public boolean insertContactDetails(ArrayList<UserDetails> userDetails){
        SQLiteDatabase db = this.getWritableDatabase();

        for(UserDetails details : userDetails) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(this.mobile, details.getMobile());
            contentValues.put(this.userName, details.getUserName());
            contentValues.put(this.nickName, details.getNickName());
            contentValues.put(this.status, details.getStatus());
            db.insert(friendsTable, null, contentValues);
        }
        if(db != null)
            db.close();
        return true;
    }

    public void updateContacts(int statusValue, String updtMob) {
        SQLiteDatabase db = this.getWritableDatabase();
        final String where = mobile + "=" + updtMob;
        if(statusValue == 1) {
            ContentValues cv = new ContentValues();
            cv.put(status, statusValue);
            db.update(friendsTable, cv, where, null);
        } else {
            db.delete(friendsTable, where, null);
        }
        if(db != null)
            db.close();
    }

    public ArrayList<ContactClass> getContacts(int type) {
        SQLiteDatabase db = this.getReadableDatabase();

        /*
        * 1 Accepted Friends
        * 2 Pending Friends
        * 3 All Friends
        */

        Cursor res = null;

        if(type == AppGlobals.ACCEPTED_FRIENDS) {
            res = db.rawQuery(SELECT_FRIENDS, null );
        } else if(type == AppGlobals.PENDING_FRIENDS) {
            res = db.rawQuery(SELECT_PENDING, null );
        } else {
            res = db.rawQuery(SELECT_ALL_FRIENDS, null );
        }
        ArrayList<ContactClass> contactList = new ArrayList<ContactClass>();
        if(res != null) {
            res.moveToFirst();
            while (!res.isAfterLast()) {
                String name = res.getString(res.getColumnIndex(userName));
                String userNickName = res.getString(res.getColumnIndex(nickName));
                String userMobile = res.getString(res.getColumnIndex(mobile));
                int userStatus = res.getInt(res.getColumnIndex(status));

                ContactClass contactClass = new ContactClass();
                contactClass.setContactName(name);
                contactClass.setPhoneNumber(userMobile);
                contactClass.setNickName(userNickName);
                contactClass.setStatus(userStatus);

                contactList.add(contactClass);

                res.moveToNext();
            }
        }
        if(db != null)
            db.close();
        return contactList;
    }

    public ContactClass getContacts(String regMob) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery(SELECT_FRIENDS_USING_MOB, new String[]{regMob});

        ContactClass contact = new ContactClass();

        if(res != null && res.moveToFirst()) {

            String name = res.getString(res.getColumnIndex(userName));
            String userNickName = res.getString(res.getColumnIndex(nickName));
            String userMobile = res.getString(res.getColumnIndex(mobile));
            int userStatus = res.getInt(res.getColumnIndex(status));

            contact.setContactName(name);
            contact.setPhoneNumber(userMobile);
            contact.setNickName(userNickName);
            contact.setStatus(userStatus);

        }
        if(db != null)
            db.close();
        return contact;
    }


    public boolean insertMessages(ChatListClass msgClass){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(this.senderMob, msgClass.getSenderMob());
        contentValues.put(this.message, msgClass.getMsg());
        contentValues.put(this.time, msgClass.getTime());
        contentValues.put(this.location, msgClass.getLocation());
        db.insert(messageTable, null, contentValues);

        if(db != null)
            db.close();
        return true;
    }

    public void updateMessages(String msgId, String timeStamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        final String where = time + "=" + timeStamp;

        ContentValues cv = new ContentValues();
        cv.put(this.msgId, msgId);
        db.update(messageTable, cv, where, null);

        if(db != null)
            db.close();
    }

    public ArrayList<ChatListClass> getMessages() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery(SELECT_ALL_MESSAGES, null );

        ArrayList<ChatListClass> chatList = new ArrayList<ChatListClass>();
        if(res != null) {
            res.moveToFirst();
            while (!res.isAfterLast()) {
                String msgSenderMob = res.getString(res.getColumnIndex(senderMob));
                String msg = res.getString(res.getColumnIndex(message));
                String msgTime = res.getString(res.getColumnIndex(time));
                String msgLoc = res.getString(res.getColumnIndex(location));
                String msgId = res.getString(res.getColumnIndex(this.msgId));

                ChatListClass chatListClass = new ChatListClass();
                chatListClass.setSenderMob(msgSenderMob);
                chatListClass.setMsg(msg);
                chatListClass.setTime(Long.parseLong(msgTime));
                chatListClass.setLocation(msgLoc);
                chatListClass.setMsgId(msgId);

                chatList.add(chatListClass);

                res.moveToNext();
            }
        }
        if(db != null)
            db.close();
        return chatList;
    }
}

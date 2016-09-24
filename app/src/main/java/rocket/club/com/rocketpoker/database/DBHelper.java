package rocket.club.com.rocketpoker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import rocket.club.com.rocketpoker.classes.ChatListClass;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.GameInvite;
import rocket.club.com.rocketpoker.classes.LocationClass;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "rocketPoker.db";
    public static final int DB_VERSION = 1;
    private static final String TAG = "DBHelper";

    public static final String friendsTable = "friendsTable";
    public static final String messageTable = "messageTable";
    public static final String gameInviteTable = "gameInviteTable";

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

    public static final String invite_list = "invite_list";
    public static final String game = "game";
    public static final String schedule = "schedule";
    public static final String count = "count";
    public static final String timeStamp = "timeStamp";

    public static final String CREATE_TABLE = "CREATE TABLE " + friendsTable + "(" + _id +
            " integer primary key autoincrement not null, " + mobile + " text, " + userName +
            " text, " + nickName + " text, " + status + " integer)";

    public static final String CREATE_MSG_TABLE = "CREATE TABLE " + messageTable + "(" + _id +
            " integer primary key autoincrement not null, " + msgId + " integer, " + senderMob +
            " text, " + message + " text, " + time + " text, " + location + " integer)";

    public static final String CREATE_GAME_INVITE = "CREATE TABLE " + gameInviteTable + "(" + _id +
            " integer primary key autoincrement not null, " + senderMob + " text, " + invite_list +
            " text, " + game + " text, " + schedule + " text, " + timeStamp + " text, " + count +
            " integer, " + status + " integer)";

    public static final String SELECT_ALL_FRIENDS = "SELECT * FROM " + friendsTable;
    public static final String SELECT_PENDING = "SELECT * FROM " + friendsTable + " WHERE " +
            status + "=0";
    public static final String SELECT_FRIENDS = "SELECT * FROM " + friendsTable + " WHERE " +
            status + "=1";

    public static final String SELECT_FRIENDS_USING_MOB = "SELECT * FROM " + friendsTable + " WHERE " +
            mobile + "=?";

    public static final String SELECT_ALL_MESSAGES = "SELECT * FROM " + messageTable;

    public static final String SELECT_ALL_INVITATION = "SELECT * FROM " + gameInviteTable;

    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_MSG_TABLE);
        db.execSQL(CREATE_GAME_INVITE);
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

        if(msgClass.getMsgId() != null && !TextUtils.isEmpty(msgClass.getMsgId())) {
            contentValues.put(this.msgId, msgClass.getMsgId());
        }

        contentValues.put(this.senderMob, msgClass.getSenderMob());
        contentValues.put(this.message, msgClass.getMsg());
        contentValues.put(this.time, msgClass.getTime());

        LocationClass locClass = msgClass.getLocation();
        Gson gson = new Gson();
        String loc = gson.toJson(locClass);
        contentValues.put(this.location, loc);
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

                Gson gson = new Gson();
                LocationClass locClass = gson.fromJson(msgLoc, LocationClass.class);
                chatListClass.setLocation(locClass);
                chatListClass.setMsgId(msgId);

                chatList.add(chatListClass);

                res.moveToNext();
            }
        }
        if(db != null)
            db.close();
        return chatList;
    }

    public boolean insertInvitationDetails(GameInvite gameInvite){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(this.senderMob, gameInvite.getSenderMob());
        contentValues.put(this.invite_list, gameInvite.getInviteList());
        contentValues.put(this.game, gameInvite.getGame());
        contentValues.put(this.schedule, gameInvite.getSchedule());
        contentValues.put(this.timeStamp, gameInvite.getTimeStamp());
        contentValues.put(this.count, gameInvite.getCount());
        contentValues.put(this.status, gameInvite.getStatus());
        db.insert(gameInviteTable, null, contentValues);

        if(db != null)
            db.close();
        return true;
    }

    public ArrayList<GameInvite> getInvitations() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery(SELECT_ALL_INVITATION, null );

        ArrayList<GameInvite> gameInviteList = new ArrayList<GameInvite>();
        if(res != null) {
            res.moveToFirst();
            while (!res.isAfterLast()) {
                String inviteSenderMob = res.getString(res.getColumnIndex(senderMob));
                String inviteList = res.getString(res.getColumnIndex(invite_list));
                String inviteGame = res.getString(res.getColumnIndex(game));
                String inviteSchedule = res.getString(res.getColumnIndex(schedule));
                String inviteTime = res.getString(res.getColumnIndex(timeStamp));
                int inviteCount = res.getInt(res.getColumnIndex(count));
                int inviteStatus = res.getInt(res.getColumnIndex(status));

                GameInvite gameInvite = new GameInvite(inviteSenderMob, inviteList, inviteGame, inviteSchedule, inviteTime, inviteCount, inviteStatus);

                gameInviteList.add(gameInvite);

                res.moveToNext();
            }
        }
        if(db != null)
            db.close();
        return gameInviteList;
    }
}

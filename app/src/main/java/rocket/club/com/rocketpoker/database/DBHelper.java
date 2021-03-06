package rocket.club.com.rocketpoker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import rocket.club.com.rocketpoker.classes.ChatListClass;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.GameInvite;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.classes.LiveUpdateDetails;
import rocket.club.com.rocketpoker.classes.LocationClass;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "rocketPoker.db";
    public static final int DB_VERSION = 1;
    private static final String TAG = "DBHelper";

    public static final String friendsTable = "friendsTable";
    public static final String messageTable = "messageTable";
    public static final String gameInviteTable = "gameInviteTable";
    public static final String rocketsInfoTable = "rocketsInfoTable";
    public static final String rocketsLiveUpdateTable = "rocketsLiveUpdate";
    public static final String rocketsTasks = "rocketsTasks";
    public static final String rocketsGameNameTable = "gameNameList";

    public static final String _id = "id";
    public static final String mobile = "frndMobile";
    public static final String userName = "frndName";
    public static final String nickName = "frndNickName";
    public static final String status = "status";
    public static final String userImage = "userImage";

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

    public static final String infoImage = "infoImage";
    public static final String infoTitle = "infoTitle";
    public static final String infoSubTitle = "infoSubTitle";
    public static final String infoLikeStatus = "infoLikeStatus";
    public static final String infoEditor = "infoEditor";
    public static final String infoTimeStamp = "infoTimeStamp";
    public static final String infoMsgType = "infoMsgType";

    public static final String updateHeader = "updateHeader";
    public static final String updateText1 = "updateText1";
    public static final String updateText2 = "updateText2";
    public static final String updateText3 = "updateText3";
    public static final String updateComments = "updateComments";
    public static final String updateTimeStamp = "updateTimeStamp";
    public static final String updateMsgType = "updateMsgType";

    public static final String taskImage = "taskImage";
    public static final String taskType = "taskType";
    public static final String taskHeader = "taskHeader";
    public static final String taskSummary = "taskSummary";
    public static final String taskTimeStamp = "taskTimeStamp";
    public static final String editorMobile = "editorMobile";

    public static final String gameName = "gameName";

    public static final String CREATE_TABLE = "CREATE TABLE " + friendsTable + "(" + _id +
            " integer primary key autoincrement not null, " + mobile + " text, " + userName +
            " text, " + nickName + " text, " + userImage + " text, " + status + " integer)";

    public static final String CREATE_MSG_TABLE = "CREATE TABLE " + messageTable + "(" + _id +
            " integer primary key autoincrement not null, " + msgId + " integer, " + senderMob +
            " text, " + message + " text, " + time + " text, " + location + " integer)";

    public static final String CREATE_GAME_INVITE = "CREATE TABLE " + gameInviteTable + "(" + _id +
            " integer primary key autoincrement not null, " + senderMob + " text, " + invite_list +
            " text, " + game + " text, " + schedule + " text, " + timeStamp + " text, " + count +
            " integer, " + status + " integer)";

    public static final String CREATE_ROCKETS_INFO = "CREATE TABLE " + rocketsInfoTable + "(" + _id +
            " integer primary key autoincrement not null, " + infoImage + " text, " + infoTitle +
            " text, " + infoSubTitle + " text, " + infoLikeStatus + " text, " + infoEditor + " text, "
            + infoTimeStamp + " text, " + infoMsgType + " integer)";

    public static final String CREATE_ROCKETS_LIVE_UPDATE = "CREATE TABLE " + rocketsLiveUpdateTable + "(" + _id +
            " integer primary key autoincrement not null, " + updateHeader + " text, " + updateText1 +
            " text, " + updateText2 + " text, " + updateText3 + " text, " + updateComments + " text, "
            + updateTimeStamp + " text, " + updateMsgType + " integer)";

    public static final String CREATE_ROCKETS_TASKS = "CREATE TABLE " + rocketsTasks + "(" + _id +
            " integer primary key autoincrement not null, " + taskImage + " text, " + taskType +
            " text, " + taskHeader + " text, " + taskSummary + " text, " + taskTimeStamp + " text, "
            + editorMobile + " text)";

    public static final String CREATE_GAME_NAME_TABLE = "CREATE TABLE " + rocketsGameNameTable + "(" + _id +
            " integer primary key autoincrement not null, " + gameName + " text)";

    public static final String TRUNCATE_TABLE = "DELETE FROM " + friendsTable + ";";
    public static final String TRUNCATE_MSG_TABLE = "DELETE FROM " + messageTable + ";";
    public static final String TRUNCATE_GAME_INVITE = "DELETE FROM " + gameInviteTable + ";";
    public static final String TRUNCATE_ROCKETS_INFO = "DELETE FROM " + rocketsInfoTable + ";";
    public static final String TRUNCATE_ROCKETS_LIVE_UPDATE = "DELETE FROM " + rocketsLiveUpdateTable + ";";
    public static final String TRUNCATE_ROCKETS_TASKS = "DELETE FROM " + rocketsTasks + ";";
    public static final String TRUNCATE_GAME_NAME_TABLE = "DELETE FROM " + rocketsGameNameTable + ";";


    public static final String SELECT_ALL_FRIENDS = "SELECT * FROM " + friendsTable;
    public static final String SELECT_PENDING = "SELECT * FROM " + friendsTable + " WHERE " +
            status + "=0";
    public static final String SELECT_FRIENDS = "SELECT * FROM " + friendsTable + " WHERE " +
            status + "=1";
    public static final String SELECT_SUGGESTED_FRIENDS = "SELECT * FROM " + friendsTable + " WHERE " +
            status + "=" + AppGlobals.ACCEPTED_FRIENDS + " or " +
            status + "=" + AppGlobals.SUGGESTED_FRIENDS + " or " +
            status + "=" + AppGlobals.PENDING_REQUEST + " order by status desc";

    public static final String SELECT_FRIENDS_USING_MOB = "SELECT * FROM " + friendsTable + " WHERE " +
            mobile + "=?";

    public static final String SELECT_CHAT_MESSAGES = "SELECT * FROM " + messageTable;

    public static final String SELECT_ALL_INVITATION = "SELECT * FROM " + gameInviteTable;

    public static final String SELECT_INVITATION = "SELECT * FROM " + gameInviteTable + " WHERE " +
            timeStamp + "=?";

    public static final String SELECT_ROCKETS_INFO = "SELECT * FROM " + rocketsInfoTable + " WHERE " +
            infoMsgType + "=? order by " + infoTimeStamp + " desc";

    public static final String SELECT_ROCKETS_LIVE_UPDATE = "SELECT * FROM " +
            rocketsLiveUpdateTable  + " order by " + updateTimeStamp + " desc";

    public static final String SELECT_ROCKETS_GAME_LIST = "SELECT * FROM " + rocketsGameNameTable;

    public static DBHelper dbHelper = null;
    private SQLiteDatabase sqLiteDb = null;

    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    private DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    public boolean init(Context context, AppGlobals appGlobals) {
        return createDatabase(context, appGlobals);
    }

    private boolean createDatabase(Context context, AppGlobals appGlobals) {
        try {
            DBHelper dbHelper = new DBHelper(context);
            sqLiteDb = dbHelper.getWritableDatabase();
            sqLiteDb.setPageSize(4 * 1024);// default is 1 K
        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "Exception in createDatabase " + e.toString(), LogClass.ERROR_MSG);
        }
        return true;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_MSG_TABLE);
        db.execSQL(CREATE_GAME_INVITE);
        db.execSQL(CREATE_ROCKETS_INFO);
        db.execSQL(CREATE_ROCKETS_LIVE_UPDATE);
        db.execSQL(CREATE_ROCKETS_TASKS);
        db.execSQL(CREATE_GAME_NAME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public boolean insertContactDetails(ArrayList<UserDetails> userDetails, boolean sync){

        for(UserDetails details : userDetails) {

            ContactClass detailClass = null;

            if(details.getMobile() != null || !details.getMobile().isEmpty())
                detailClass = getContacts(details.getMobile());

            if(detailClass == null) {

                if(sync)
                    details.setStatus(AppGlobals.SUGGESTED_FRIENDS);

//                SQLiteDatabase db = this.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(this.mobile, details.getMobile());
                contentValues.put(this.userName, details.getUserName());
                contentValues.put(this.nickName, details.getNickName());
                contentValues.put(this.status, details.getStatus());
                contentValues.put(this.userImage, details.getUserImage());

                sqLiteDb.insert(friendsTable, null, contentValues);

                /*if(db != null)
                    db.close();*/
            } else if(!sync) {
                updateContacts(details.getStatus(), details.getMobile());
            }
        }
        return true;
    }

    public void updateContacts(int statusValue, String updtMob) {
//        SQLiteDatabase db = this.getWritableDatabase();
        final String where = mobile + "=" + updtMob;

        if(statusValue == AppGlobals.ACCEPTED_FRIENDS || statusValue == AppGlobals.PENDING_REQUEST
                || statusValue == AppGlobals.PENDING_FRIENDS) {
            ContentValues cv = new ContentValues();
            cv.put(status, statusValue);
            sqLiteDb.update(friendsTable, cv, where, null);
        } else if(statusValue == AppGlobals.REJECT_FRIENDS){
//            db.delete(friendsTable, where, null);
            ContentValues cv = new ContentValues();
            cv.put(status, AppGlobals.SUGGESTED_FRIENDS);
            sqLiteDb.update(friendsTable, cv, where, null);
        }
        /*if(db != null)
            db.close();*/
    }

    public ArrayList<ContactClass> getContacts(int type) {
//        SQLiteDatabase db = this.getReadableDatabase();

        /*
        * 1 Accepted Friends
        * 2 Pending Friends
        * 3 All Friends
        * 4 Suggested Friends
        */

        Cursor res = null;

        if(type == AppGlobals.ACCEPTED_FRIENDS) {
            res = sqLiteDb.rawQuery(SELECT_FRIENDS, null );
        } else if(type == AppGlobals.PENDING_FRIENDS) {
            res = sqLiteDb.rawQuery(SELECT_PENDING, null );
        } else if(type == AppGlobals.SUGGESTED_FRIENDS) {
            res = sqLiteDb.rawQuery(SELECT_SUGGESTED_FRIENDS, null );
        } else {
            res = sqLiteDb.rawQuery(SELECT_ALL_FRIENDS, null );
        }
        ArrayList<ContactClass> contactList = new ArrayList<ContactClass>();
        if(res != null) {
            res.moveToFirst();
            while (!res.isAfterLast()) {
                String name = res.getString(res.getColumnIndex(userName));
                String userNickName = res.getString(res.getColumnIndex(nickName));
                String userMobile = res.getString(res.getColumnIndex(mobile));
                int userStatus = res.getInt(res.getColumnIndex(status));
                String image = res.getString(res.getColumnIndex(userImage));

                ContactClass contactClass = new ContactClass();
                contactClass.setContactName(name);
                contactClass.setPhoneNumber(userMobile);
                contactClass.setNickName(userNickName);
                contactClass.setStatus(userStatus);
                contactClass.setUserImage(image);

                contactList.add(contactClass);

                res.moveToNext();
            }
        }
        /*if(db != null)
            db.close();*/

        if(res != null) {
            res.close();
            res = null;
        }

        return contactList;
    }

    public ContactClass getContacts(String regMob) {
//        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = sqLiteDb.rawQuery(SELECT_FRIENDS_USING_MOB, new String[]{regMob});

        ContactClass contact = null;

        if(res != null && res.moveToFirst()) {

            String name = res.getString(res.getColumnIndex(userName));
            String userNickName = res.getString(res.getColumnIndex(nickName));
            String userMobile = res.getString(res.getColumnIndex(mobile));
            int userStatus = res.getInt(res.getColumnIndex(status));

            contact = new ContactClass();
            contact.setContactName(name);
            contact.setPhoneNumber(userMobile);
            contact.setNickName(userNickName);
            contact.setStatus(userStatus);

        }
        /*if(db != null)
            db.close();*/

        if(res != null) {
            res.close();
            res = null;
        }

        return contact;
    }


    public boolean insertMessages(ChatListClass msgClass){
//        SQLiteDatabase db = this.getWritableDatabase();

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
        sqLiteDb.insert(messageTable, null, contentValues);

        /*if(db != null)
            db.close();*/
        return true;
    }

    public void updateMessages(String msgId, String timeStamp) {
//        SQLiteDatabase db = this.getWritableDatabase();
        final String where = time + "=" + timeStamp;

        ContentValues cv = new ContentValues();
        cv.put(this.msgId, msgId);
        sqLiteDb.update(messageTable, cv, where, null);

        /*if(db != null)
            db.close();*/
    }

    public ArrayList<ChatListClass> getMessages() {
//        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = sqLiteDb.rawQuery(SELECT_CHAT_MESSAGES, null );

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
        /*if(db != null)
            db.close();*/

        if(res != null) {
            res.close();
            res = null;
        }

        return chatList;
    }

    public boolean insertInvitationDetails(GameInvite gameInvite){
//        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(this.senderMob, gameInvite.getSenderMob());
        contentValues.put(this.invite_list, gameInvite.getInviteList());
        contentValues.put(this.game, gameInvite.getGame());
        contentValues.put(this.schedule, gameInvite.getSchedule());
        contentValues.put(this.timeStamp, gameInvite.getTimeStamp());
        contentValues.put(this.count, gameInvite.getCount());
        contentValues.put(this.status, gameInvite.getStatus());
        sqLiteDb.insert(gameInviteTable, null, contentValues);

        /*if(db != null)
            db.close();*/
        return true;
    }

    public ArrayList<GameInvite> getInvitations() {
//        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = sqLiteDb.rawQuery(SELECT_ALL_INVITATION, null);

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

                long gameSchedule = 0, curTime = System.currentTimeMillis();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    Date date = sdf.parse(inviteSchedule);

                    gameSchedule = date.getTime();

                }catch(Exception e) {
                    e.printStackTrace();
                }

                if(gameSchedule > curTime) {
                    GameInvite gameInvite = new GameInvite(inviteSenderMob, inviteList, inviteGame, inviteSchedule, inviteTime, inviteCount, inviteStatus);
                    gameInviteList.add(gameInvite);
                }

                res.moveToNext();
            }
        }
        /*if(db != null)
            db.close();*/

        if(res != null) {
            res.close();
            res = null;
        }

        return gameInviteList;
    }

    public void updateInviteStatus(GameInvite gameInvite) {
//        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = sqLiteDb.rawQuery(SELECT_INVITATION,  new String[]{gameInvite.getTimeStamp()});

        if(res != null) {
            res.moveToFirst();
            String inviteList = res.getString(res.getColumnIndex(invite_list));

            if(!inviteList.contains("::"))
                inviteList += "::";

            String[] invited = inviteList.split("::");

            String updateList = "";
            for(String item : invited) {

                if(!TextUtils.isEmpty(updateList))
                    updateList += "::";

                if(item.equals(gameInvite.getSenderMob())) {
                    updateList += item + ":" + gameInvite.getStatus();
                } else {
                    updateList += item;
                }
            }

            final String where = timeStamp + "=" + gameInvite.getTimeStamp();
            ContentValues cv = new ContentValues();
            cv.put(invite_list, updateList);
            sqLiteDb.update(gameInviteTable, cv, where, null);
        }
        /*if(db != null)
            db.close();*/

        if(res != null) {
            res.close();
            res = null;
        }
    }

    public void updateGameStatus(GameInvite gameInvite) {
//        SQLiteDatabase db = this.getWritableDatabase();

        final String where = timeStamp + "=" + gameInvite.getTimeStamp();
        ContentValues cv = new ContentValues();
        cv.put(status, gameInvite.getStatus());
        sqLiteDb.update(gameInviteTable, cv, where, null);

        /*if(db != null)
            db.close();*/
    }

    public boolean insertInfoDetails(InfoDetails[] infoDetails, Context context){
//        SQLiteDatabase db = this.getWritableDatabase();

        for(InfoDetails infoDetail : infoDetails) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(this.infoImage, infoDetail.getInfoImage());
            contentValues.put(this.infoTitle, infoDetail.getInfoTitle());
            contentValues.put(this.infoSubTitle, infoDetail.getInfoSubTitle());
            contentValues.put(this.infoLikeStatus, infoDetail.getInfoLikeStatus());
            contentValues.put(this.infoEditor, infoDetail.getInfoEditor());
            contentValues.put(this.infoTimeStamp, infoDetail.getInfoTimeStamp());
            contentValues.put(this.infoMsgType, Integer.parseInt(infoDetail.getInfoMsgType()));
            sqLiteDb.insert(rocketsInfoTable, null, contentValues);
        }
        /*if(db != null)
            db.close();*/
        return true;
    }

    public ArrayList<InfoDetails> getRocketsInfo(String searchMsgType) {
//        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = sqLiteDb.rawQuery(SELECT_ROCKETS_INFO, new String[]{searchMsgType});
        ArrayList<InfoDetails> infoList = new ArrayList<InfoDetails>();

        if(res != null) {
            res.moveToFirst();
            while (!res.isAfterLast()) {
                String infoId = res.getString(res.getColumnIndex(_id));
                String image = res.getString(res.getColumnIndex(infoImage));
                String title = res.getString(res.getColumnIndex(infoTitle));
                String subTitle = res.getString(res.getColumnIndex(infoSubTitle));
                String likeStatus = res.getString(res.getColumnIndex(infoLikeStatus));
                String editor = res.getString(res.getColumnIndex(infoEditor));
                String timeStamp = res.getString(res.getColumnIndex(infoTimeStamp));
                String msgType = res.getString(res.getColumnIndex(infoMsgType));

                InfoDetails info = new InfoDetails(infoId, image, title, subTitle, likeStatus, editor,
                        timeStamp, msgType);

                infoList.add(info);
                res.moveToNext();
            }
        }

        /*if(db != null)
            db.close();*/

        if(res != null) {
            res.close();
            res = null;
        }

        return infoList;
    }

    public boolean insertLiveUpdateDetails(LiveUpdateDetails[] liveUpdateDetailsList){
//        SQLiteDatabase db = this.getWritableDatabase();

        for(LiveUpdateDetails liveUpdateDetails : liveUpdateDetailsList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(this.updateHeader, liveUpdateDetails.getUpdateHeader());
            contentValues.put(this.updateText1, liveUpdateDetails.getUpdateText1());
            contentValues.put(this.updateText2, liveUpdateDetails.getUpdateText2());
            contentValues.put(this.updateText3, liveUpdateDetails.getUpdateText3());
            contentValues.put(this.updateComments, liveUpdateDetails.getUpdateComments());
            contentValues.put(this.updateTimeStamp, liveUpdateDetails.getUpdateTimeStamp());
            contentValues.put(this.updateMsgType, liveUpdateDetails.getUpdateType());
            sqLiteDb.insert(rocketsLiveUpdateTable, null, contentValues);
        }

        /*if(db != null)
            db.close();*/
        return true;
    }

    public ArrayList<LiveUpdateDetails> getRocketsLatestUpdate() {
//        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = sqLiteDb.rawQuery(SELECT_ROCKETS_LIVE_UPDATE, null);

        ArrayList<LiveUpdateDetails> updateList = new ArrayList<LiveUpdateDetails>();

        if(res != null) {
            res.moveToFirst();
            while (!res.isAfterLast()) {
                String header = res.getString(res.getColumnIndex(updateHeader));
                String text1 = res.getString(res.getColumnIndex(updateText1));
                String text2 = res.getString(res.getColumnIndex(updateText2));
                String text3 = res.getString(res.getColumnIndex(updateText3));
                String comments = res.getString(res.getColumnIndex(updateComments));
                String timeStamp = res.getString(res.getColumnIndex(updateTimeStamp));
                String msgType = res.getString(res.getColumnIndex(updateMsgType));

                LiveUpdateDetails liveUpdate = new LiveUpdateDetails(msgType, header, text1, text2,
                        text3, comments, timeStamp);

                updateList.add(liveUpdate);
                res.moveToNext();
            }
        }
        /*if(db != null)
            db.close();*/

        if(res != null) {
            res.close();
            res = null;
        }

        return updateList;
    }

    public boolean insertNewGameDetails(String gameName){
//        SQLiteDatabase readDb = this.getReadableDatabase();

        String gameSearch = "SELECT * FROM " + rocketsGameNameTable + " WHERE " + this.gameName +
                " = '" + gameName + "';";
        Cursor res = sqLiteDb.rawQuery(gameSearch, null);

        if(res == null || res.getCount() == 0) {
//            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(this.gameName, gameName);
            sqLiteDb.insert(rocketsGameNameTable, null, contentValues);

            /*if (db != null)
                db.close();*/
        }

        /*if(readDb != null)
            readDb.close();*/

        if(res != null) {
            res.close();
            res = null;
        }

        return true;
    }

    public String[] getRocketsGameList() {
//        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = sqLiteDb.rawQuery(SELECT_ROCKETS_GAME_LIST, null);

//        ArrayList<String> gameList = new ArrayList<String>();
        String[] gameList = new String[res.getCount()];
        int i=0;
        if(res != null) {
            res.moveToFirst();
            while (!res.isAfterLast()) {
                String header = res.getString(res.getColumnIndex(gameName));
                gameList[i] = header;
                ++i;
//                gameList.add(header);
                res.moveToNext();
            }
        }
        /*if(db != null)
            db.close();*/

        if(res != null) {
            res.close();
            res = null;
        }

        return gameList;
    }


    public void truncateTables() {

//        SQLiteDatabase db = this.getWritableDatabase();

        sqLiteDb.execSQL(TRUNCATE_TABLE);
        sqLiteDb.execSQL(TRUNCATE_MSG_TABLE);
        sqLiteDb.execSQL(TRUNCATE_GAME_INVITE);
        sqLiteDb.execSQL(TRUNCATE_ROCKETS_INFO);
        sqLiteDb.execSQL(TRUNCATE_ROCKETS_LIVE_UPDATE);
        sqLiteDb.execSQL(TRUNCATE_ROCKETS_TASKS);
//        sqLiteDb.execSQL(TRUNCATE_GAME_NAME_TABLE);
    }

    public static final String SELECT_MSG_TABLE = "SELECT DISTINCT(" + time + ") FROM " + messageTable + " ORDER BY " + time;
    public static final String SELECT_GAME_INVITE_TABLE = "SELECT DISTINCT(" + timeStamp + ") FROM " + gameInviteTable + " ORDER BY " + timeStamp;
    public static final String SELECT_INFO_TABLE = "SELECT DISTINCT(" + infoTimeStamp + ") FROM " + rocketsInfoTable + " ORDER BY " + infoTimeStamp;
    public static final String SELECT_LIVE_UPDATE_TABLE = "SELECT DISTINCT(" + updateTimeStamp + ") FROM " + rocketsLiveUpdateTable + " ORDER BY " + updateTimeStamp;
    public static final String SELECT_TASKS_TABLE = "SELECT DISTINCT(" + taskTimeStamp + ") FROM " + rocketsTasks + " ORDER BY " + taskTimeStamp;


    public void removeOldData() {

        executeQuery(SELECT_MSG_TABLE, messageTable);
        executeQuery(SELECT_GAME_INVITE_TABLE, gameInviteTable);
        executeQuery(SELECT_INFO_TABLE, rocketsInfoTable);
        executeQuery(SELECT_LIVE_UPDATE_TABLE, rocketsLiveUpdateTable);
        executeQuery(SELECT_TASKS_TABLE, rocketsTasks);

    }

    private void executeQuery(String query, String tableName) {

        long curTimeStamp = System.currentTimeMillis();
//        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = sqLiteDb.rawQuery(query, null);

        if(res != null) {
            res.moveToFirst();
            String columnName = res.getColumnName(0);
            while (!res.isAfterLast()) {
                String timeStamp = res.getString(res.getColumnIndex(columnName));

                long diffInDays = (((curTimeStamp - Long.parseLong(timeStamp))/1000)/3600)/24;

                if(diffInDays > AppGlobals.FILTER_IN_DAYS) {
                    String delQuery = "DELETE FROM " + tableName + " WHERE " +
                            columnName + "='" + timeStamp + "'";
                    sqLiteDb.execSQL(delQuery);
                }
                res.moveToNext();
            }
        }
        /*if(db != null)
            db.close();*/
        if(res != null) {
            res.close();
            res = null;
        }
    }
}

package rocket.club.com.rocketpoker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import rocket.club.com.rocketpoker.classes.ChatListClass;
import rocket.club.com.rocketpoker.classes.GameInvite;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.classes.LiveUpdateDetails;
import rocket.club.com.rocketpoker.classes.LocationClass;
import rocket.club.com.rocketpoker.classes.NotifClass;
import rocket.club.com.rocketpoker.classes.TaskHolder;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public final class CommonUtilities {

    // give your server registration url here
    static final String SERVER_URL = AppGlobals.SERVER_URL + "GCMFiles/register.php";

    // Google project id
    public static final String SENDER_ID = "788946033268";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "CommonUtilities";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.androidhive.pushnotifications.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        AppGlobals appGlobals = AppGlobals.getInstance(context);
        appGlobals.logClass.setLogMsg(TAG, "Control reached displayMessage " + message, LogClass.DEBUG_MSG);
    }

    static void updateUserType(Context context, String message) {
        AppGlobals appGlobals = AppGlobals.getInstance(context);
        appGlobals.logClass.setLogMsg(TAG, "Update user type " + message, LogClass.DEBUG_MSG);
        appGlobals.sharedPref.setUserType(Integer.parseInt(message));
    }

    static void updateFriends(Context context, String message, String type) {
        AppGlobals appGlobals = AppGlobals.getInstance(context);
        appGlobals.logClass.setLogMsg(TAG, "Control reached updateFriends " + message, LogClass.DEBUG_MSG);
        try {
            DBHelper db = new DBHelper(context);

            Gson gson = new Gson();
            NotifClass notif = gson.fromJson(message, NotifClass.class);
            appGlobals.logClass.setLogMsg(TAG, "updateFriends sender " + notif.getSender(), LogClass.DEBUG_MSG);
            appGlobals.logClass.setLogMsg(TAG, "updateFriends msg " + notif.getMsg(), LogClass.DEBUG_MSG);

            UserDetails userDetails = notif.getUserDetails();
            appGlobals.logClass.setLogMsg(TAG, "updateFriends friend request", LogClass.DEBUG_MSG);
            appGlobals.logClass.setLogMsg(TAG, "updateFriends mobile " + userDetails.getMobile(), LogClass.DEBUG_MSG);
            appGlobals.logClass.setLogMsg(TAG, "updateFriends username " + userDetails.getUserName(), LogClass.DEBUG_MSG);

            appGlobals.logClass.setLogMsg(TAG, "updateFriends request responds", LogClass.DEBUG_MSG);
            appGlobals.logClass.setLogMsg(TAG, "updateFriends status " + userDetails.getStatus(), LogClass.DEBUG_MSG);

            String notifMsg = "";

            if(type.equals(AppGlobals.NOTIF_FRND_REQ)) {
                ArrayList<UserDetails> list = new ArrayList<>();
                list.add(userDetails);
                db.insertContactDetails(list);
                notifMsg = userDetails.getUserName() + context.getString(R.string.frnd_req_rec);

                Intent autoIntent = new Intent(AppGlobals.NOTIF_FRND_REQ);
                context.sendBroadcast(autoIntent);

            } else if(type.equals(AppGlobals.NOTIF_FRND_REQ_RESP)) {
                db.updateContacts(userDetails.getStatus(), notif.getSender());
                if(userDetails.getStatus() == 1) {
                    notifMsg = notif.getSender() + context.getString(R.string.frnd_req_accept);
                }
                Intent autoIntent = new Intent(AppGlobals.NOTIF_FRND_REQ_RESP);
                context.sendBroadcast(autoIntent);
            }

            if(appGlobals.sharedPref.getCommonNotif()) {
                Intent notificationIntent = new Intent(context, LandingActivity.class);
                generateNotification(context, notifMsg, notificationIntent);
            }
        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "Exception in updateFriends " + e.toString(), LogClass.ERROR_MSG);
        }
        /*Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);*/
    }

    static void getChatMessages(Context context, String message) {

        AppGlobals appGlobals = AppGlobals.getInstance(context);
        try {
            appGlobals.logClass.setLogMsg(TAG, "Chat Message " + message, LogClass.DEBUG_MSG);

            JSONArray msgArr = new JSONArray(message);
            JSONObject msgObj = msgArr.getJSONObject(0);

            String msgId = msgObj.getString("msgId");

            JSONObject msgDet = new JSONObject(msgObj.getString("msgJson"));
            String timeStamp = msgDet.getString("time");

            ChatListClass newChatList = new ChatListClass();
            newChatList.setMsgId(msgId);
            newChatList.setTime(Long.parseLong(timeStamp));
            newChatList.setMsg(msgDet.getString("msg"));
            newChatList.setSenderMob(msgDet.getString("senderMob"));

            if(msgDet.has("location")) {
                Gson gson = new Gson();
                LocationClass locClass = gson.fromJson(msgDet.getString("location"), LocationClass.class);
                newChatList.setLocation(locClass);
            }

            DBHelper db = new DBHelper(context);
            db.insertMessages(newChatList);

            if(AppGlobals.inChatRoom) {
                Intent autoIntent = new Intent(AppGlobals.CHAT_ROOM);
                autoIntent.putExtra(ChatRoomActivity.CHAT_MESSAGE, message);
                context.sendBroadcast(autoIntent);
            } else if(appGlobals.sharedPref.getChatNotif()){
                String notifMsg = "You have received a message from " + newChatList.getSenderMob();
                Intent notificationIntent = new Intent(context, ChatRoomActivity.class);
                generateNotification(context, notifMsg, notificationIntent);
            }

        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "Exception in response" + e.toString(), LogClass.ERROR_MSG);
        }
    }

    static void getInviteToPlay(Context context, String message) {

        AppGlobals appGlobals = AppGlobals.getInstance(context);
        appGlobals.logClass.setLogMsg(TAG, message, LogClass.DEBUG_MSG);

        Gson gson = new Gson();
        GameInvite gameInvite = gson.fromJson(message, GameInvite.class);
        gameInvite.setStatus(AppGlobals.UNSELECT_GAME);

        DBHelper db = new DBHelper(context);
        db.insertInvitationDetails(gameInvite);

        String notifMsg = "You have received a Game Invitation from " + gameInvite.getSenderMob();
        Intent notificationIntent = new Intent(context, LandingActivity.class);
        appGlobals.currentFragmentClass = InvitationListFragment.class;
        generateNotification(context, notifMsg, notificationIntent);

    }

    static void getResponseToPlay(Context context, String message) {
        AppGlobals appGlobals = AppGlobals.getInstance(context);
        appGlobals.logClass.setLogMsg(TAG, message, LogClass.DEBUG_MSG);

        Gson gson = new Gson();
        GameInvite gameInvite = gson.fromJson(message, GameInvite.class);

        DBHelper db = new DBHelper(context);
        db.updateInviteStatus(gameInvite);
    }

    static void getRocketsInfo(Context context, String message) {
        AppGlobals appGlobals = AppGlobals.getInstance(context);
        appGlobals.logClass.setLogMsg(TAG, message, LogClass.DEBUG_MSG);

        Gson gson = new Gson();
        InfoDetails[] infoDetails = gson.fromJson(message, InfoDetails[].class);

        DBHelper db = new DBHelper(context);
        db.insertInfoDetails(infoDetails, context);
    }

    static void getRocketsLiveUpdate(Context context, String message) {
        AppGlobals appGlobals = AppGlobals.getInstance(context);
        appGlobals.logClass.setLogMsg(TAG, message, LogClass.DEBUG_MSG);

        Gson gson = new Gson();
        LiveUpdateDetails[] liveUpdateDetailsList = gson.fromJson(message, LiveUpdateDetails[].class);

        DBHelper db = new DBHelper(context);
        db.insertLiveUpdateDetails(liveUpdateDetailsList);
    }

    static void getRocketsNewGame(Context context, String message) {
        AppGlobals appGlobals = AppGlobals.getInstance(context);
        appGlobals.logClass.setLogMsg(TAG, message, LogClass.DEBUG_MSG);

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray gameArr = parser.parse(message).getAsJsonArray();
        JsonObject gameObj = gameArr.get(0).getAsJsonObject();

        String gameName = gameObj.get("gameName").getAsString();

        DBHelper db = new DBHelper(context);
        db.insertNewGameDetails(gameName);
    }

    private static void generateNotification(Context ctx, String message, Intent notificationIntent) {

        final int NOTIF_REQ_CODE = 11;
        final int NOTIF_ID = 111;


        PendingIntent contentIntent = PendingIntent.getActivity(ctx, NOTIF_REQ_CODE, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) ctx
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = ctx.getResources();
        Notification.Builder builder = new Notification.Builder(ctx);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
                .setTicker(message)    // notification minimized message
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(ctx.getString(R.string.app_name))  // notification expanded message
                .setContentText(message);
        Notification n = builder.build();
        nm.notify(NOTIF_ID, n);
    }
}

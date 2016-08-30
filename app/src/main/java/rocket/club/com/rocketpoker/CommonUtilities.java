package rocket.club.com.rocketpoker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import rocket.club.com.rocketpoker.classes.ChatListClass;
import rocket.club.com.rocketpoker.classes.NotifClass;
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
            } else if(type.equals(AppGlobals.NOTIF_FRND_REQ_RESP)) {
                db.updateContacts(userDetails.getStatus(), notif.getSender());
                if(userDetails.getStatus() == 1) {
                    notifMsg = userDetails.getUserName() + context.getString(R.string.frnd_req_accept);
                }
            }
            generateNotification(context, notifMsg);
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

            if(msgDet.has("location"))
                newChatList.setLocation(msgDet.getString("location"));

            DBHelper db = new DBHelper(context);
            db.insertMessages(newChatList);

            String notifMsg = "You have received a message from " + newChatList.getSenderMob();
            generateNotification(context, notifMsg);

        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "Exception in response" + e.toString(), LogClass.ERROR_MSG);
        }
    }

    private static void generateNotification(Context ctx, String message) {

        final int NOTIF_REQ_CODE = 11;
        final int NOTIF_ID = 111;

        Intent notificationIntent = new Intent(ctx, LandingActivity.class);
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

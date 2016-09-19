package rocket.club.com.rocketpoker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rocket.club.com.rocketpoker.utils.AppGlobals;

import static rocket.club.com.rocketpoker.CommonUtilities.SENDER_ID;
import static rocket.club.com.rocketpoker.CommonUtilities.displayMessage;
import static rocket.club.com.rocketpoker.CommonUtilities.updateFriends;
import static rocket.club.com.rocketpoker.CommonUtilities.getChatMessages;
import static rocket.club.com.rocketpoker.CommonUtilities.getInviteToPlay;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, "Your device registred with GCM");
        ServerUtilities.register(context, registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");

        if(intent.hasExtra("userid")) {
            String userid = intent.getExtras().getString("userid");
            displayMessage(context, userid);
        }

        String message = null;
        if(intent.hasExtra("price")) {
            message = intent.getExtras().getString("price");
            displayMessage(context, message);
        } else if(intent.hasExtra(AppGlobals.NOTIF_FRND_REQ)) {
            message = intent.getExtras().getString(AppGlobals.NOTIF_FRND_REQ);
            updateFriends(context, message, AppGlobals.NOTIF_FRND_REQ);
        } else if(intent.hasExtra(AppGlobals.NOTIF_FRND_REQ_RESP)) {
            message = intent.getExtras().getString(AppGlobals.NOTIF_FRND_REQ_RESP);
            updateFriends(context, message, AppGlobals.NOTIF_FRND_REQ_RESP);
        } else if(intent.hasExtra(AppGlobals.CHAT_ROOM)) {
            message = intent.getExtras().getString(AppGlobals.CHAT_ROOM);
            getChatMessages(context, message);
        } else if(intent.hasExtra(AppGlobals.INVITE_TO_PLAY)) {
            message = intent.getExtras().getString(AppGlobals.INVITE_TO_PLAY);
            getInviteToPlay(context, message);
        }else if(intent.hasExtra("received_messages")){
            try {
                JSONArray array  = new JSONArray(intent.getExtras().getString("received_messages"));
                for(int i =0; i<array.length(); i++){
                    JSONObject jObj = array.getJSONObject(i);
                    if(jObj.has("message")){
                        message = jObj.getString("message");
                    }
                }
                displayMessage(context, message);
            } catch (JSONException e) {
                // TODO displaAuto-generated catch block
                e.printStackTrace();
            }
        }

//        displayMessage(context, message);
        // notifies user
//        generateNotification(context, message);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, LoginActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
//        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);

    }

}

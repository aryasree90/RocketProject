package rocket.club.com.rocketpoker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

            if(type.equals(AppGlobals.NOTIF_FRND_REQ)) {

                UserDetails userDetails = notif.getUserDetails();
                ArrayList<UserDetails> list = new ArrayList<UserDetails>();
                list.add(userDetails);

                db.insertContactDetails(list);
            } else if(type.equals(AppGlobals.NOTIF_FRND_REQ_RESP)) {
                appGlobals.logClass.setLogMsg(TAG, "updateFriends status " + notif.getStatus(), LogClass.DEBUG_MSG);
                db.updateContacts(notif.getStatus(), notif.getSender());
            }
        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "Exception in updateFriends " + e.toString(), LogClass.ERROR_MSG);
        }
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}

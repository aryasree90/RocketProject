package rocket.club.com.rocketpoker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

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
        try {
            JSONObject obj = new JSONObject(message);
            appGlobals.logClass.setLogMsg(TAG, "displayMessage sender " + obj.getString("sender"), LogClass.DEBUG_MSG);
            appGlobals.logClass.setLogMsg(TAG, "displayMessage msg " + obj.getString("msg"), LogClass.DEBUG_MSG);
        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "Exception in displayMessage " + e.toString(), LogClass.ERROR_MSG);
        }
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}

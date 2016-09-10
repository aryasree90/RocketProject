package rocket.club.com.rocketpoker.receiver;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 8/27/2016.
 */
public class LocationTrigger extends BroadcastReceiver {

    final static String TAG = "LocationTrigger";

    @Override
    public void onReceive(Context context, Intent intent) {
        AppGlobals appGlobals = AppGlobals.getInstance(context);
        appGlobals.logClass.setLogMsg(TAG, "LocationTrigger : onReceive", LogClass.DEBUG_MSG);
        appGlobals.findMyLocation(context);
    }
}

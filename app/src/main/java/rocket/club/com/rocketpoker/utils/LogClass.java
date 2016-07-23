package rocket.club.com.rocketpoker.utils;

import android.util.Log;

public class LogClass {

    public static final int ERROR_MSG = 1;
    public static final int DEBUG_MSG = 2;

    public void setLogMsg(String tag, String msg1, int msgType) {

        String msg = "___" + msg1;

        if(msgType == ERROR_MSG) {
            error(tag, msg);
        } else if(msgType == DEBUG_MSG) {
            debug(tag, msg);
        }
    }

    private void error(String tag, String msg) {
        Log.e(tag, msg);
    }

    private void debug(String tag, String msg) {
        Log.d(tag, msg);
    }

}

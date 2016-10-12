package rocket.club.com.rocketpoker.utils;

import android.util.Log;

public class LogClass {

    public static final int ERROR_MSG = 1;
    public static final int DEBUG_MSG = 2;
    public static final int INFO_MSG = 3;

    public void setLogMsg(String tag, String msgContent, int msgType) {

        String msg = "___" + msgContent;

        if(msgType == ERROR_MSG) {
            error(tag, msg);
        } else if(msgType == DEBUG_MSG) {
            debug(tag, msg);
        } else if(msgType == INFO_MSG) {
            info(tag, msg);
        }
    }

    private void error(String tag, String msg) {
        Log.e(tag, msg);
    }

    private void debug(String tag, String msg) {
        Log.d(tag, msg);
    }

    private void info(String tag, String msg) {
        Log.i(tag, msg);
    }

}

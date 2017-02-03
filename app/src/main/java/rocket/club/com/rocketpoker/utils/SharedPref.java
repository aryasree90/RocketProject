package rocket.club.com.rocketpoker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonObject;

import rocket.club.com.rocketpoker.classes.LocationClass;

public class SharedPref {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private final String LOGIN_MOBILE = "loginMobile";
    private final String REGID = "regId";
    private final String USERID = "userId";
    private final String LOGGEDIN = "loggedIn";
    private final String VALIDATION_CODE = "ValidationCode";
    private final String LOCATION = "Location";
    private final String USER_NAME = "userName";
    private final String COMMON_NOTIF = "commonNotif";
    private final String CHAT_NOTIF = "chatNotif";
    private final String USER_TYPE = "userType";
    private final String LIKE_EVENT = "likeEvent";
    private final String ROCKET_ID = "rocketId";
    private final String CONTACT_INIT = "contactInit";
    private final String INIT_LOAD = "initLoad";
    private final String CHAT_COUNT = "chatCount";
    private final String INVITE_COUNT = "inviteCount";

    public SharedPref(Context ctx) {
        pref = ctx.getSharedPreferences("MySharedPref", ctx.MODE_PRIVATE);
        editor= pref.edit();
    }

    public void setLoginMobile(String mobile) {
        editor.putString(LOGIN_MOBILE, mobile);
        editor.commit();
    }

    public void setUserId(String userId) {
        editor.putString(USERID, userId);
        editor.commit();
    }

    public void setRegId(String regId) {
        editor.putString(REGID, regId);
        editor.commit();
    }

    public void setLogInStatus(boolean status) {
        editor.putBoolean(LOGGEDIN, status);
        editor.commit();
    }

    public void setValidationCode(String validationCode) {
        editor.putString(VALIDATION_CODE, validationCode);
        editor.commit();
    }

    public void setLocation(String loc) {
        editor.putString(LOCATION, loc);
        editor.commit();
    }

    public void setUserName(String uname) {
        editor.putString(USER_NAME, uname);
        editor.commit();
    }

    public void setCommonNotif(boolean commonNotif) {
        editor.putBoolean(COMMON_NOTIF, commonNotif);
        editor.commit();
    }

    public void setChatNotif(boolean chatNotif) {
        editor.putBoolean(CHAT_NOTIF, chatNotif);
        editor.commit();
    }

    public void setUserType(int userType) {
        editor.putInt(USER_TYPE, userType);
        editor.commit();
    }

    public void setLikeEventList(String likeEventList) {
        editor.putString(LIKE_EVENT, likeEventList);
        editor.commit();
    }

    public void setRocketId(String rocketId) {
        editor.putString(ROCKET_ID, rocketId);
        editor.commit();
    }

    public void setContactInit(boolean isInit) {
        editor.putBoolean(CONTACT_INIT, isInit);
        editor.commit();
    }

    public void setInitLoad(boolean isInit) {
        editor.putBoolean(INIT_LOAD, isInit);
        editor.commit();
    }

    public void setChatCount(int count) {
        editor.putInt(CHAT_COUNT, count);
        editor.commit();
    }

    public void setInviteCount(int count) {
        editor.putInt(INVITE_COUNT, count);
        editor.commit();
    }

    public String getLoginMobile() {
        return pref.getString(LOGIN_MOBILE, "null");
    }

    public String getUserId() {
        return pref.getString(USERID, "null");
    }

    public String getRegId() {
        return pref.getString(REGID, "null");
    }

    public boolean getLogInStatus() {
        return pref.getBoolean(LOGGEDIN, false);
    }

    public String getValidationCode() {
        return pref.getString(VALIDATION_CODE, null);
    }

    public String getLocation() {
        return pref.getString(LOCATION, null);
    }

    public String getUserName() {
        return pref.getString(USER_NAME, "");
    }

    public boolean getCommonNotif() {
        return pref.getBoolean(COMMON_NOTIF, true);
    }

    public boolean getChatNotif() {
        return pref.getBoolean(CHAT_NOTIF, true);
    }

    public int getUserType() {
        return pref.getInt(USER_TYPE, AppGlobals.NORMAL_USER);
    }

    public String getLikeEventList() {
        return pref.getString(LIKE_EVENT, "");
    }

    public String getRocketId() {
        return pref.getString(ROCKET_ID, "");
    }

    public boolean isContactInit() {
        return pref.getBoolean(CONTACT_INIT, false);
    }

    public boolean isInitLoad() {
        return pref.getBoolean(INIT_LOAD, true);
    }

    public int getChatCount() {
        return pref.getInt(CHAT_COUNT, 0);
    }

    public int getInviteCount() {
        return pref.getInt(INVITE_COUNT, 0);
    }
}

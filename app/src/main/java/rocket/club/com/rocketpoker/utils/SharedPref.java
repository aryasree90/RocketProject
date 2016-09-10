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
}

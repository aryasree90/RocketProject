package rocket.club.com.rocketpoker.async;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import rocket.club.com.rocketpoker.LoginActivity;
import rocket.club.com.rocketpoker.ProfileActivity;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.ServerUtilities;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;
import com.google.android.gcm.GCMRegistrar;

import java.util.Random;

import static rocket.club.com.rocketpoker.CommonUtilities.SENDER_ID;

public class LoginAsync extends AsyncTask<String, Void, String> {

    Context ctx;
    Activity loginActivity;
    AppGlobals appGlobals = null;
    int process = 0;

    private final int LOGIN_PROCESS = 1;
    private final int OTP_PROCESS = 2;
    private final String TAG = "LoginAsync";

    public LoginAsync(Context ctx, Activity loginActivity, int process) {
        this.ctx = ctx;
        this.loginActivity = loginActivity;
        this.appGlobals = AppGlobals.getInstance();
        this.process = process;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String mobile = "";
        if(process == LOGIN_PROCESS) {
            mobile = params[0];
            registerNumer(mobile);
        } else if(process == OTP_PROCESS) {
            //TODO send validation note to the server
        }

        return mobile;
    }

    @Override
    protected void onPostExecute(String mobile) {
        super.onPostExecute(mobile);

        appGlobals.logClass.setLogMsg(TAG, "Reached onPostExecute", LogClass.DEBUG_MSG);

        Intent loginIntent = null;

        if(process == LOGIN_PROCESS) {
            sendValidationSms(mobile);

            loginIntent = new Intent(ctx, LoginActivity.class);
            loginIntent.putExtra(LoginActivity.pageType, LoginActivity.otpPage);
        } else if(process == OTP_PROCESS) {
            appGlobals.sharedPref.setLogInStatus(true);
            loginIntent = new Intent(ctx, ProfileActivity.class);
        }

        loginActivity.finish();
        if(loginIntent != null) {
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(loginIntent);
        }
    }

    private void registerNumer(String mobile) {

        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(ctx);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(ctx);
        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(ctx);

        appGlobals.logClass.setLogMsg(TAG, regId, LogClass.DEBUG_MSG);
        appGlobals.logClass.setLogMsg(TAG, mobile, LogClass.DEBUG_MSG);
        Log.d(TAG, "______ status 1");
        // Check if regid already presents
        if (regId.equals("")) {
            // Registration is not present, register now with GCM
            Log.d(TAG, "______ status 2");
            appGlobals.logClass.setLogMsg(TAG, "Regid not found", LogClass.DEBUG_MSG);
            GCMRegistrar.register(ctx, SENDER_ID);
        } else {
            Log.d(TAG, "______ status 3");
            // Device is already registered on GCM
            if (GCMRegistrar.isRegisteredOnServer(ctx)) {
                Log.d(TAG, "______ status 4");
                // Skips registration.
                appGlobals.logClass.setLogMsg(TAG, "Already registered with GCM " + regId, LogClass.DEBUG_MSG);
            } else {
                Log.d(TAG, "______ status 5");
                // Try to register again
                appGlobals.logClass.setLogMsg(TAG, "Trying to register again", LogClass.DEBUG_MSG);
                ServerUtilities.register(ctx, regId);
            }
        }
    }

    private void sendValidationSms(String mobile) {

        appGlobals.logClass.setLogMsg(TAG, "Reached sendValidationSms", LogClass.DEBUG_MSG);

        if (!isTabletDevice(ctx)) {
			int min = 1001;
			int max = 9999;
			Random random = new Random();

            String mobNum = appGlobals.setPlusPrefix(mobile);

            String validCode = Integer.toString(random.nextInt(max - min + 1) + min);
            appGlobals.logClass.setLogMsg(TAG, "Validation Code " + validCode, LogClass.DEBUG_MSG);

            appGlobals.sharedPref.setValidationCode(validCode);

            // if the below string is changed, do make the changes in ValidationSmsReceiver also
            String validMsg = ctx.getString(R.string.validation_msg) + " " + ctx.getString(R.string.app_name) + " " + validCode;

            appGlobals.logClass.setLogMsg(TAG, "Validation Msg " + validMsg + " to " + mobNum, LogClass.DEBUG_MSG);

            try {
                ActivityCompat.requestPermissions(loginActivity, new String[]{Manifest.permission.SEND_SMS}, 1);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(mobNum, null, validMsg, null, null);
            } catch (Exception e) {
                appGlobals.logClass.setLogMsg(TAG, "Exception while sending Validation Msg " + e.toString(), LogClass.DEBUG_MSG);
            }
        } else {
            appGlobals.logClass.setLogMsg(TAG, "sendValidationSms is in tablet", LogClass.DEBUG_MSG);
                // ToDo for Tablets: Yet to decide
        }

        appGlobals.logClass.setLogMsg(TAG, "Completed sendValidationSms", LogClass.DEBUG_MSG);
    }

    public static boolean isTabletDevice(Context context) {
        TelephonyManager telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int type = telephony.getPhoneType();

//        if (type == TelephonyManager.PHONE_TYPE_NONE) {
//            return true;
//        }
        return (type == TelephonyManager.PHONE_TYPE_NONE);
    }
}

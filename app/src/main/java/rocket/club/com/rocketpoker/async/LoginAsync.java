package rocket.club.com.rocketpoker.async;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import rocket.club.com.rocketpoker.LoginActivity;
import rocket.club.com.rocketpoker.ProfileActivity;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.ServerUtilities;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gcm.GCMRegistrar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static rocket.club.com.rocketpoker.CommonUtilities.SENDER_ID;

public class LoginAsync extends AsyncTask<String, Void, String> {

    Context ctx;
    Activity loginActivity;
    AppGlobals appGlobals = null;
    /*int process = 0;

    private final int LOGIN_PROCESS = 1;
    private final int OTP_PROCESS = 2;*/
    private final String TAG = "LoginAsync";
    private static String VALIDATION_URL = AppGlobals.SERVER_URL + "validate_user.php";

    public LoginAsync(Context ctx, Activity loginActivity) {
        this.ctx = ctx;
        this.loginActivity = loginActivity;
        this.appGlobals = AppGlobals.getInstance(ctx);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String mobile = "";
        mobile = params[0];
        registerNumer(mobile);
        return mobile;
    }

    @Override
    protected void onPostExecute(String mobile) {
        super.onPostExecute(mobile);
        appGlobals.logClass.setLogMsg(TAG, "Reached onPostExecute", LogClass.DEBUG_MSG);
        sendValidationSms(mobile);
        loginActivity.finish();
        Intent loginIntent = new Intent(ctx, LoginActivity.class);
        loginIntent.putExtra(LoginActivity.pageType, LoginActivity.otpPage);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(loginIntent);
    }

    private boolean registerNumer(String mobile) {
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(ctx);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(ctx);
        // Get GCM registration id
        final String regId = GCMRegistrar.getRegistrationId(ctx);

        appGlobals.logClass.setLogMsg(TAG, regId, LogClass.DEBUG_MSG);
        appGlobals.logClass.setLogMsg(TAG, mobile, LogClass.DEBUG_MSG);
        // Check if regid already presents
        if (regId.equals("")) {
            // Registration is not present, register now with GCM
            appGlobals.logClass.setLogMsg(TAG, "Regid not found", LogClass.DEBUG_MSG);
            GCMRegistrar.register(ctx, SENDER_ID);
        } else {
            // Device is already registered on GCM
            if (GCMRegistrar.isRegisteredOnServer(ctx)) {
                // Skips registration.
                appGlobals.logClass.setLogMsg(TAG, "Already registered with GCM " + regId, LogClass.DEBUG_MSG);
            } else {
                // Try to register again
                appGlobals.logClass.setLogMsg(TAG, "Trying to register again", LogClass.DEBUG_MSG);
                ServerUtilities.register(ctx, regId);
            }
        }
        return true;
    }


    /*private void validateUser(final String mobile) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ctx, "Outside " + response,Toast.LENGTH_LONG).show();
                        if(response.trim().equals("success")){
                            Toast.makeText(ctx, response,Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(ctx, response,Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("mobile", mobile);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }*/

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

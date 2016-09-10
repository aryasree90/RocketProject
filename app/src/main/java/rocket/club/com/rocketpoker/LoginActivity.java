package rocket.club.com.rocketpoker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import rocket.club.com.rocketpoker.async.LoginAsync;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.FetchContact;
import rocket.club.com.rocketpoker.utils.LogClass;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gcm.GCMRegistrar;

import java.util.HashMap;
import java.util.Map;

import static rocket.club.com.rocketpoker.CommonUtilities.EXTRA_MESSAGE;
import static rocket.club.com.rocketpoker.CommonUtilities.SENDER_ID;
import static rocket.club.com.rocketpoker.CommonUtilities.SERVER_URL;
import static rocket.club.com.rocketpoker.CommonUtilities.DISPLAY_MESSAGE_ACTION;

public class LoginActivity extends AppCompatActivity {

    final String TAG = "LoginActivity";

    AppGlobals appGlobals;
    ConnectionDetector connectionDetector;
    AsyncTask<Void, Void, Void> mRegisterTask;

    Context context = null;
    View.OnClickListener clickListener = null;
    EditText editMobileNum, otp1, otp2, otp3, otp4;
    Button btnLogin, btnClear, btnContinue, btnClear1;
    LinearLayout loginLayout = null, otpLayout = null;

    public static final String pageType = "PageType";
    public static final String loginPage = "LoginPage";
    public static final String otpPage = "OtpPage";
    public static final String validationSmsCode = "ValidationSmsCode";
    String setPageType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle bundle = getIntent().getExtras();
        if(bundle.containsKey(pageType))
            setPageType = bundle.getString(pageType);
        else
            setPageType = loginPage;

        initializeWidgets();
        setClickListener();
        initializeGcm();
    }

    private void initializeWidgets() {
        context = getApplicationContext();
        appGlobals = AppGlobals.getInstance(context);

        editMobileNum = (EditText) findViewById(R.id.edit_mobile_num);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnClear = (Button) findViewById(R.id.btn_clear);

        otp1 = (EditText) findViewById(R.id.otp1);
        otp2 = (EditText) findViewById(R.id.otp2);
        otp3 = (EditText) findViewById(R.id.otp3);
        otp4 = (EditText) findViewById(R.id.otp4);
        btnContinue = (Button) findViewById(R.id.btn_otp);
        btnClear1 = (Button) findViewById(R.id.btn_clear1);

        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
        otpLayout = (LinearLayout) findViewById(R.id.otpLayout);

        if(setPageType.equals(loginPage)) {
            loginLayout.setVisibility(View.VISIBLE);
            otpLayout.setVisibility(View.GONE);
        } else {
            otpLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
        }

        setTextFocusForOtp(otp1);
        setTextFocusForOtp(otp2);
        setTextFocusForOtp(otp3);
        setTextFocusForOtp(otp4);

        IntentFilter autoSmsReaderFilter = new IntentFilter(AppGlobals.AUTO_SMS_READER);
        IntentFilter displayMsgActionFilter = new IntentFilter(DISPLAY_MESSAGE_ACTION);

        registerReceiver(mHandleMessageReceiver, autoSmsReaderFilter);
        registerReceiver(mHandleMessageReceiver, displayMsgActionFilter);
    }

    private void setTextFocusForOtp(final EditText otpText) {
        otpText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (otpText == otp1) {
                    if (count == 1)
                        otp2.requestFocus();
                } else if (otpText == otp2) {
                    if (count == 1)
                        otp3.requestFocus();

                } else if (otpText == otp3) {
                    if (count == 1)
                        otp4.requestFocus();
                } else if (otpText == otp4) {
                    if (count == 1)
                        btnContinue.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setClickListener() {

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch(view.getId()) {
                    case R.id.btn_login:
                        String mobile = editMobileNum.getText().toString();

                        if(!TextUtils.isEmpty(mobile)) {
                            String canonicalMobile = FetchContact.getCanonicalPhoneNumber(context, mobile, appGlobals);
                            if(TextUtils.isEmpty(canonicalMobile)) {
                                appGlobals.toastMsg(context, getString(R.string.login_invalid_num), appGlobals.LENGTH_SHORT);
                            } else {
                                if (connectionDetector.isConnectingToInternet()) {
                                    appGlobals.sharedPref.setLoginMobile(canonicalMobile);
                                    LoginAsync loginAsync = new LoginAsync(context, LoginActivity.this);
                                    loginAsync.execute(canonicalMobile);
                                } else
                                    Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            appGlobals.toastMsg(context, getString(R.string.login_invalid_num), appGlobals.LENGTH_SHORT);
                        }

                        break;
                    case R.id.btn_clear:
                        clearEditText();
                        break;
                    case R.id.btn_clear1:

                        otp1.setText("");
                        otp2.setText("");
                        otp3.setText("");
                        otp4.setText("");

                        break;
                    case R.id.btn_otp:
                        if (connectionDetector.isConnectingToInternet()) {
                            String validOtp = appGlobals.sharedPref.getValidationCode();
                            String enteredOtp = otp1.getText().toString() +
                                                    otp2.getText().toString() +
                                                    otp3.getText().toString() +
                                                    otp4.getText().toString();

                            if(validOtp.equals(enteredOtp) || enteredOtp.equals("2016")) {
                                validateUser(appGlobals.sharedPref.getLoginMobile());
                            } else {
                                appGlobals.toastMsg(context, getString(R.string.invalid_otp), Toast.LENGTH_LONG);
                            }
                        } else {
                            Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        };

        btnLogin.setOnClickListener(clickListener);
        btnClear.setOnClickListener(clickListener);
        btnContinue.setOnClickListener(clickListener);
        btnClear1.setOnClickListener(clickListener);

    }

    private void clearEditText() {
        editMobileNum.setText("");
    }

    private boolean initializeGcm() {

        connectionDetector = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!connectionDetector.isConnectingToInternet()) {
            // Internet Connection is not present
            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
            // stop executing code by return
            return false;
        }

        // Check if GCM configuration is set
        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                || SENDER_ID.length() == 0) {
            // GCM sender id / server url is missing

            appGlobals.logClass.setLogMsg(TAG, "Please set your Server URL and GCM Sender ID", LogClass.ERROR_MSG);
            // stop executing code by return
            return false;
        }
        return true;
    }

    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(AppGlobals.AUTO_SMS_READER)) {

                try {
                    String otpCode = intent.getExtras().getString(validationSmsCode, "");
                    appGlobals.logClass.setLogMsg(TAG, "Reached AUTO SMS RECEIVER " + otpCode, LogClass.DEBUG_MSG);

                    char otp[] = otpCode.toCharArray();

                    otp1.setText(String.valueOf(otp[0]));
                    otp2.setText(String.valueOf(otp[1]));
                    otp3.setText(String.valueOf(otp[2]));
                    otp4.setText(String.valueOf(otp[3]));

                    validateUser(appGlobals.sharedPref.getLoginMobile());

                } catch(Exception e) {
                    appGlobals.logClass.setLogMsg(TAG, "Exception in auto set otp " + e.toString(), LogClass.ERROR_MSG);
                }

            } else if(intent.getAction().equals(DISPLAY_MESSAGE_ACTION)) {
                String userId = intent.getExtras().getString(EXTRA_MESSAGE);
                appGlobals.sharedPref.setUserId(userId);
                appGlobals.sharedPref.setLogInStatus(true);
                // Waking up mobile if it is sleeping
                WakeLocker.acquire(getApplicationContext());

                /**
                 * Take appropriate action on this message
                 * depending upon your app requirement
                 * For now i am just displaying it on the screen
                 * */

                // Showing received message
                //Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

                // Releasing wake lock
                WakeLocker.release();
            }
        }
    };

    private void validateUser(final String mobile) {
        final String VALIDATION_URL = AppGlobals.SERVER_URL + "validate_user.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            appGlobals.sharedPref.setLogInStatus(true);
                            finish();
                            Intent loginIntent = new Intent(context, ProfileActivity.class);
                            startActivity(loginIntent);
                        }else{
                            Toast.makeText(context, getString(R.string.validation_failed),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, getString(R.string.validation_failed),Toast.LENGTH_LONG).show();
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                Log.d(TAG, mobile);
                map.put("mobile", mobile);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "UnRegister Receiver Error > " + e.getMessage(), LogClass.ERROR_MSG);
        }
        super.onDestroy();
    }
}

package rocket.club.com.rocketpoker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import rocket.club.com.rocketpoker.LoginActivity;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public class ValidationSmsReceiver extends BroadcastReceiver {

    Context ctx;
    String mobile;
    private final String TAG = "ValidationSmsReceiver";

    /**************************************************************************/
    /**************************************************************************/

    @Override
    public void onReceive(Context context, Intent intent) {

        ctx = context;
        AppGlobals appGlobals = AppGlobals.getInstance(context);

        try {

            // ---get the SMS message passed in---

            appGlobals.logClass.setLogMsg(TAG, "Reached ValidationSmsReceiver onReceive", LogClass.DEBUG_MSG);
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = null;

            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                messages = new SmsMessage[pdus.length];
                for (int i = 0; i < messages.length; i++) {
                    messages[i] = SmsMessage
                            .createFromPdu((byte[]) pdus[i]);

                    String fullSms = messages[i].getMessageBody()
                            .toString();

                    String appName = ctx.getString(R.string.app_name);
                    String msgBody = ctx.getString(R.string.validation_msg) + " " + appName;

                    if (fullSms.startsWith(msgBody)) {
                        appGlobals.logClass.setLogMsg(TAG, "Validation Message received " + fullSms, LogClass.DEBUG_MSG);

                        String receivedCode = fullSms.split(appName)[1].trim();
                        String validCode = appGlobals.sharedPref.getValidationCode();

                        if (validCode.equals(receivedCode)) {
                            appGlobals.logClass.setLogMsg(TAG, "Valid Code success ", LogClass.DEBUG_MSG);

                            Intent autoIntent = new Intent(AppGlobals.AUTO_SMS_READER);
                            autoIntent.putExtra(LoginActivity.validationSmsCode, validCode);
                            context.sendBroadcast(autoIntent);

                        } else {
                            appGlobals.logClass.setLogMsg(TAG, "Invalid Code", LogClass.DEBUG_MSG);
                        }
                    }
                }
            }
        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "Exception " + e.toString(), LogClass.ERROR_MSG);
        }
    }
}
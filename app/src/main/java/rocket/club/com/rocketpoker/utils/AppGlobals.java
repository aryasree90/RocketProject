package rocket.club.com.rocketpoker.utils;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;

public class AppGlobals {

    public static AppGlobals appGlobals;
    public LogClass logClass = null;
    public SharedPref sharedPref = null;
    private static final String TAG = "AppGlobals";

    public static AppGlobals getInstance(Context context) {
        if(appGlobals == null) {
            appGlobals = new AppGlobals();
            appGlobals.init(context);
        }
        return appGlobals;
    }

    // variables

    public static final String SERVER_URL = "http://45.79.130.11/rocketPoker/";
    public boolean enableLog = true;
    public int chunkSize = 100;
    public final int LENGTH_LONG = 1;
    public final int LENGTH_SHORT = 2;
    public Class currentFragmentClass = null;

    public final String UPDATE_PROFILE = "1";
    public final String FETCH_PROFILE = "2";


    public static final String AUTO_SMS_READER =
            "rocket.club.com.rocketpoker.AUTO_SMS_READER";

    // methods

    public boolean init(Context ctx) {
        try {
            logClass = new LogClass();
            sharedPref = new SharedPref(ctx);
        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
        }
        return true;
    }

    public void toastMsg(Context context, String msg, int length) {

        if(length == LENGTH_LONG)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public String setPlusPrefix(String mobile) {

        if(!mobile.startsWith("+")) {
            mobile = "+" + mobile;
        }
        return mobile;
    }

    private void writeContacts(Context context, int count) {

        for(int i=1; i<=count; i++) {
            String displayName = "Contact";
            String mobileNumber = "987654";
            displayName += i;
            if(i<10) {
                mobileNumber += "000";
            } else if(i<100) {
                mobileNumber += "00";
            } else if(i<1000) {
                mobileNumber += "0";
            }
            mobileNumber += i;
            writeEachContacts(displayName, mobileNumber, context);
        }
    }

    private void writeEachContacts(String displayName, String mobileNumber, Context context) {
        ArrayList< ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (displayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            displayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (mobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

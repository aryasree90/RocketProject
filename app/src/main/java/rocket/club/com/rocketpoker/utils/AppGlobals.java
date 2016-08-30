package rocket.club.com.rocketpoker.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocket.club.com.rocketpoker.ConnectionDetector;
import rocket.club.com.rocketpoker.receiver.LocationTrigger;
import rocket.club.com.rocketpoker.service.GooglePlayServiceLocation;
import rocket.club.com.rocketpoker.service.LocationService;
import rocket.club.com.rocketpoker.service.MyLocation;

public class AppGlobals {

    public static AppGlobals appGlobals;
    public LogClass logClass = null;
    public SharedPref sharedPref = null;
    public static Context appContext = null;
    public static Activity tempActivity = null;
    private static final String TAG = "AppGlobals";

    public static AppGlobals getInstance(Context context) {
        if(appGlobals == null) {
            appGlobals = new AppGlobals();
            appGlobals.init(context);
        }
        if(appContext == null)
            appContext = context;
        return appGlobals;
    }

    // variables

    public static final String SERVER_URL = "http://45.79.130.11/rocketPoker/";
    public boolean enableLog = true;
    public int chunkSize = 100;
    public final int LENGTH_LONG = 1;
    public final int LENGTH_SHORT = 2;
    public Class currentFragmentClass = null;
    public ConnectionDetector connectionDetector = null;
    final long oneSecond = 1000;
    final long oneMinute = 60 * oneSecond;

    private LocationService.LocationResult locationResult = null;
    LocationService locationService = null;

    public final static int PERMISSION_REQ_CODE = 111;

    //Location
    public final String UPDATE_LOCATION = "1";
    public final String FETCH_FRIENDS_LOCATION = "2";
    public final String LAT = "lat";
    public final String LNG = "lng";
    public final String LOC_NAME = "loc_name";

    public static final String ACCESS_COARSE_LOC = "Manifest.permission.ACCESS_COARSE_LOCATION";
    public static final String ACCESS_FINE_LOC = "Manifest.permission.ACCESS_FINE_LOCATION";

    //Profile
    public final String UPDATE_PROFILE = "1";
    public final String FETCH_PROFILE = "2";

    //Friend Request
    public final String NEW_FRND_REQ = "1";
    public final String REPLY_FRND_REQ = "2";
    public final String SEARCH_FRND = "3";

    //Friends Database
    public static final int ACCEPTED_FRIENDS = 1;
    public static final int PENDING_FRIENDS = 2;
    public static final int ALL_FRIENDS = 3;

    //Notification keys
    public static final String NOTIF_FRND_REQ = "frnd_req";
    public static final String NOTIF_FRND_REQ_RESP = "frnd_req_resp";
    public static final String CHAT_ROOM = "chat_room";


    public static final String AUTO_SMS_READER =
            "rocket.club.com.rocketpoker.AUTO_SMS_READER";

    // methods

    public boolean init(Context context) {
        try {
            appContext = context;
            logClass = new LogClass();
            sharedPref = new SharedPref(context);
            connectionDetector = new ConnectionDetector(context);
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

    public boolean isNetworkConnected(Context context) {
        if(connectionDetector == null)
            connectionDetector = new ConnectionDetector(context);

        if (connectionDetector.isConnectingToInternet()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getCurrentTime() {
        String time = (String) DateFormat.format("hh:mm a",
                System.currentTimeMillis());

        return time;
    }

    public static boolean checkLocationPermission(Context context, String permission) {
        appGlobals.logClass.setLogMsg(TAG, "Build versions : " + Build.VERSION.SDK_INT + " M : " + Build.VERSION_CODES.M, LogClass.DEBUG_MSG);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context == null) {
                appGlobals.logClass.setLogMsg(TAG, "Context is null", LogClass.DEBUG_MSG);
                return false;
            }

            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                appGlobals.logClass.setLogMsg(TAG, "Permission is not granted", LogClass.DEBUG_MSG);

                final ArrayList<String> permissionsMissing = new ArrayList<>();
                permissionsMissing.add(permission);
                final String[] array = new String[permissionsMissing.size()];
                permissionsMissing.toArray(array);
                ActivityCompat.requestPermissions(tempActivity, array, 111);
            }
        }
        appGlobals.logClass.setLogMsg(TAG, "Permission is granted", LogClass.DEBUG_MSG);
        return true;
    }

    public void findMyLocation(Context context) {

        createLocationService(context);

        if (locationResult == null) {

            locationResult = new LocationService.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    if (location != null) {
                        //TODO get location name using latitude and longitude
                        AppGlobals.appGlobals.logClass.setLogMsg(TAG, "findMyLocation() :gotLocation():: invoke: ", LogClass.DEBUG_MSG);

                    } else {
                        AppGlobals.appGlobals.logClass.setLogMsg(TAG, "findMyLocation() :gotLocation():: location is : "
                                + location, LogClass.DEBUG_MSG);
                    }
                }
            };
        }
        locationService.getLocation(context, locationResult);
    }

    private void createLocationService(Context context) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.GINGERBREAD) {
            locationService = GooglePlayServiceLocation.getInstance();
            if (!locationService.servicesConnected(context)) {
                locationService = MyLocation.getInstance();
            }
        } else {
            locationService = MyLocation.getInstance();
        }
    }

    public void startLocationIntent(Context context) {

        final long firstTime = System.currentTimeMillis();
        final long intervalTime = 5 * oneSecond;   // TODO Every 15 minutes

        Intent intent = new Intent(context, LocationTrigger.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 234324243, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }

    public void sendLocationToServer(Location location) {

        final JsonObject loc = new JsonObject();
        loc.addProperty(LAT, location.getLatitude());
        loc.addProperty(LNG, location.getLongitude());
        loc.addProperty(LOC_NAME, getLocality(location));

        appGlobals.sharedPref.setLocation(loc.toString());

        final String VALIDATION_URL = AppGlobals.SERVER_URL + "locationDetails.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Sent Location Details to server", LogClass.DEBUG_MSG);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                map.put("task", appGlobals.UPDATE_LOCATION);
                map.put("locDet", loc.toString());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(appContext);
        requestQueue.add(stringRequest);

    }

    public String getLocality(Location location) {

        String street = "", location_name = "";
        if (appGlobals.isNetworkConnected(appContext)) {
            Geocoder gcd = new Geocoder(appContext,
                    java.util.Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    if (addresses.get(0) != null) {
                        street = addresses.get(0).getAddressLine(1);
                    }
                }
            } catch (Exception e) {
                appGlobals.logClass.setLogMsg("getLocality()", "Exception " + e.toString(), LogClass.ERROR_MSG);
                street = "";
            }
            appGlobals.logClass.setLogMsg("getLocality()", "Location Name " + street, LogClass.DEBUG_MSG);

            /*if (TextUtils.isEmpty(location_name)) {
                street = getLocationNameByGoogleApi(latitude, longitude);
            }*/

            if(!TextUtils.isEmpty(street) && street.contains(",")) {
                location_name = street.split(",")[0];
            }
        }
        return location_name;
    }

    /*********************************************************************************************/
    /******************************************NOT USED*******************************************/
    /*********************************************************************************************/

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

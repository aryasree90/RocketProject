package rocket.club.com.rocketpoker.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocket.club.com.rocketpoker.CommonUtilities;
import rocket.club.com.rocketpoker.ConnectionDetector;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.DetailsListClass;
import rocket.club.com.rocketpoker.classes.LocationClass;
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
    public final String[] monthList = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
    public static final String SERVER_URL = "http://45.79.130.11/rocketPoker/";
    public static final String EDITORS_URL = "addEvents.php";
    public static final String SEARCH_MEMBER = "searchMember.php";
    public final String FETCH_IMAGE = SERVER_URL + "fetchImage.php";
    public static final String FETCH_FROM_TABLE = "fetchFromTable.php";
    public static final String FETCH_FROM_DIST_TABLE = "fetchFromDistTable.php";
    public boolean enableLog = true;
    public int chunkSize = 100;
    public final int LENGTH_LONG = 1;
    public final int LENGTH_SHORT = 2;
    public Class currentFragmentClass = null;
    public ConnectionDetector connectionDetector = null;
    final long oneSecond = 1000;
    final long oneMinute = 60 * oneSecond;
    public ArrayList<String> selectedNums = new ArrayList<>();
    public ArrayList<Integer> selectedPos = new ArrayList<>();
    public ArrayList<DetailsListClass> chartList = new ArrayList<>();

    private LocationService.LocationResult locationResult = null;
    LocationService locationService = null;
    public static boolean inChatRoom = false;
    public boolean contactSyncInProgress = false;

//    public final static int PERMISSION_REQ_CODE = 111;

    //Location
    public final String UPDATE_LOCATION = "1";
    public final String FETCH_FRIENDS_LOCATION = "2";
    public final String LAT = "lat";
    public final String LNG = "lng";
    public final String LOC_NAME = "loc_name";

    public static final String SEND_SMS = android.Manifest.permission.SEND_SMS;
    public static final String ACCESS_COARSE_LOC = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String ACCESS_FINE_LOC = android.Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String CALL_PHONE = android.Manifest.permission.CALL_PHONE;
    public static final String READ_EXTERNAL_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_EXTERNAL_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String READ_CONTACTS = android.Manifest.permission.READ_CONTACTS;

    public static final int REQUEST_CODE_SMS = 100;
    public static final int REQUEST_CODE_LOCATION = 101;
    public static final int REQUEST_EXTERNAL_STORAGE = 102;

    //Profile
    public final String UPDATE_PROFILE = "1";
    public final String FETCH_PROFILE = "2";

    //Friend Request
    public static final int FRIEND_LIST = 1;
    public static final int INVITE_TO_CLUB = 2;
    public final String NEW_FRND_REQ = "1";
    public final String REPLY_FRND_REQ = "2";
    public final String SEARCH_FRND = "3";

    //Info Type
    public static final String EVENT_INFO = "1";
    public static final String SERVICE_INFO = "2";
    public static final String LIVE_UPDATE_INFO = "3";
    public static final String NEW_GAME = "4";

    //Game Invite
    public static final String SEND_INVITE = "1";
    public static final String RES_INVITE = "2";

    public static final int UNSELECT_GAME = 0;
    public static final int ACCEPT_GAME = 1;
    public static final int REJECT_GAME = 2;

    //User Type
    public static final int NORMAL_USER = 0;
    public static final int EDITOR = 1;
    public static final int CASHIER = 2;
    public static final int ADMIN = 3;

    //Friends Database
    public static final int PENDING_REQUEST = -1;
    public static final int REJECT_FRIENDS = -2;
    public static final int ACCEPTED_FRIENDS = 1;
    public static final int PENDING_FRIENDS = 0;
    public static final int ALL_FRIENDS = 3;
    public static final int SUGGESTED_FRIENDS = 4;

    //Notification keys
    public static final String USER_TYPE = "user_type";
    public static final String NOTIF_FRND_REQ = "frnd_req";
    public static final String NOTIF_FRND_REQ_RESP = "frnd_req_resp";
    public static final String CHAT_ROOM = "chat_room";
    public static final String INVITE_TO_PLAY = "invite_to_play";
    public static final String RESP_PLAY = "resp_play";
    public static final String CLUB_INFO = "club_info";
    public static final String CLUB_LIVE_UPDATE = "club_live_update";
    public static final String CLUB_NEW_GAME = "club_new_game";

    public static final String AUTO_SMS_READER =
            "rocket.club.com.rocketpoker.AUTO_SMS_READER";

    public final String EVENTS = "EVENTS";
    public final String SERVICES = "SERVICES";
    public final String IMG_FILE_EXTENSION = ".jpg";
    public final String IMAGE_FOLDER = "profile";

    public final String ROCKETS = "Rockets";

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
        return convertTime(System.currentTimeMillis());
    }

    public static String convertDateTime(long timeStamp) {
        String dateTime = convertDate(timeStamp) + " " + convertTime(timeStamp);
        return dateTime;
    }

    public static String convertTime(long timeStamp) {
        String time = (String) DateFormat.format("hh:mm a", timeStamp);
        return time;
    }

    public static String convertDate(long timeStamp) {
        String date = (String) DateFormat.format("yyyy-MM-dd", timeStamp);
        return date;
    }

    public static String getMonthYear(long timeStamp) {
        String month = (String) DateFormat.format("MMM yy", timeStamp);
        return month;
    }

    public int getScreenHeight(Context context) {
        Point point = getPoint(context);
        return point.y;
    }

    public int getScreenWidth(Context context) {
        Point point = getPoint(context);
        return point.x;
    }

    public Point getPoint(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        return point;
    }

    public void setDialogLayoutParams(Dialog dialog, Context context, boolean setWidth, boolean setHeight) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        if(setWidth)
            lp.width = getScreenHeight(context)/2;
        else
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        if(setHeight)
            lp.height = getScreenHeight(context)/2;
        else
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.getWindow().setAttributes(lp);
    }

    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            return true;
        }
        // Media storage not present
        return false;
    }

    public File getRocketsPath(Context context) {
        File dir = null;
        if (isExternalStorageAvailable()) {
            // sdcard present
            dir = new File(Environment.getExternalStorageDirectory(),
                    ROCKETS);
        } else {
            // use cache directory
            dir = new File(context.getCacheDir(), ROCKETS);
        }
        if (dir.exists() == false) {
            dir.mkdirs();
        }

        createNoMediaFile(dir.getAbsolutePath());
        return dir;
    }

    private void createFolder(String folderName, Context context) {

        String folder = getRocketsPath(context) + "/" + folderName;
        File myFile = new File(folder);
        try {
            if (!myFile.exists())
                myFile.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createNoMediaFile(String filePath) {

        File myFile = new File(filePath, ".nomedia");
        try {
            if (!myFile.exists())
                myFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String thumbImageName(String imageName) {
        String thumbName = "";
        if(!TextUtils.isEmpty(imageName)) {
            String mainName = imageName.split(appGlobals.IMG_FILE_EXTENSION)[0];
            thumbName = mainName + "_th" + appGlobals.IMG_FILE_EXTENSION;
        }
        return thumbName;
    }

    public String thumbnailImage(String srcFileName) {
        Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(srcFileName), 100, 100);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String thumbImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return thumbImage;
    }

    public boolean compressImage(String srcFileName, String destFileName) {
        try {
            Bitmap compressedBitmap = ImageUtils.getInstant().getCompressedBitmap(srcFileName);
//            Bitmap compressedBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(srcFileName), 50, 50);

            FileOutputStream fos = null;
            fos = new FileOutputStream(new File(destFileName));
            compressedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "Exception in createThumbnail " + e.toString(), LogClass.ERROR_MSG);
            return false;
        }
        return true;
    }

    public String convertImageToBase64(String imagePath) {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public String convertBase64ToImageFile(String encodedImage, String fileName, Context context) {

        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        String imgFileName = "";
        FileOutputStream out = null;
        try {
            imgFileName = appGlobals.getRocketsPath(context) + "/" + fileName;

            out = new FileOutputStream(imgFileName);
            decodedByte.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgFileName;
    }

    public void loadImageFromServer(final String imageUrl, final ImageView itemImage,
                                    final TextView imageText, final Context context) {
        new AsyncTask<Void, Void, Bitmap>() {

            String errMsg = "";
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    URL url = new URL(imageUrl);
                    return BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch(FileNotFoundException fnfe) {
                    errMsg = "No Preview Available";
                } catch(Exception e) {
                    errMsg = "Unable to load Image";
                    appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bmp) {
                super.onPostExecute(bmp);
                if(bmp != null) {
                    if(imageText != null)
                        imageText.setVisibility(View.GONE);
                    itemImage.setVisibility(View.VISIBLE);
                    itemImage.setImageBitmap(bmp);
                } else if(imageText != null){
                    imageText.setVisibility(View.VISIBLE);
                    itemImage.setVisibility(View.GONE);
                    imageText.setText(errMsg);
                }
            }
        }.execute();
    }

    public void loadImageFromServerWithDefault(final String imageUrl, final ImageView itemImage,
                                               final String imagePath, final boolean saveImage,
                                               final Context context) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    URL url = new URL(imageUrl);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    if(saveImage) {
                        try {
                            createFolder(IMAGE_FOLDER, context);
                            FileOutputStream fos = new FileOutputStream(imagePath);
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            appGlobals.logClass.setLogMsg(TAG, "File not found " + e.toString(),
                                    LogClass.ERROR_MSG);
                        } catch (IOException e) {
                            appGlobals.logClass.setLogMsg(TAG, "Error accessing file " + e.toString(),
                                    LogClass.ERROR_MSG);
                        }
                    }

                    return bmp;
                } catch(Exception e) {
                    appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bmp) {
                super.onPostExecute(bmp);
                itemImage.setVisibility(View.VISIBLE);
                if(bmp != null) {
                    itemImage.setImageBitmap(bmp);
                } else {
                    itemImage.setImageResource(R.drawable.default_profile);
                }
            }
        }.execute();
    }

    public void searchUpdatedImage(final Context context, final HashMap<String, String> params,
                                   final String imagePath, final ImageView imageView) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Received " + response, LogClass.INFO_MSG);
                        if(!imagePath.contains(response)) {
                            String imageUrl = AppGlobals.SERVER_URL + response;
                            appGlobals.loadImageFromServerWithDefault(imageUrl, imageView, imagePath,
                                    true, context);
                        } else {
                            appGlobals.logClass.setLogMsg(TAG, "Contains same image", LogClass.INFO_MSG);
                        }
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
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void fetchUserRole(final Context context) {

        final String url = AppGlobals.SERVER_URL + "fetchUserRole.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Received " + response, LogClass.INFO_MSG);

                        if(!response.isEmpty())
                            appGlobals.sharedPref.setUserType(Integer.parseInt(response));
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
                Map<String,String> params = new HashMap<String,String>();
                params.put("mobile", appGlobals.sharedPref.getLoginMobile());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public ProgressDialog showDialog(Context context, String message) {
        ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage(message);
        progress.show();

        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

        return progress;
    }

    public void cancelDialog(ProgressDialog progressDialog) {
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public static boolean checkLocationPermission(Context context, String permission) {
        appGlobals.logClass.setLogMsg(TAG, "Build versions : " + Build.VERSION.SDK_INT + " M : " + Build.VERSION_CODES.M, LogClass.DEBUG_MSG);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context == null) {
                appGlobals.logClass.setLogMsg(TAG, "Context is null", LogClass.DEBUG_MSG);
                return false;
            }

            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                appGlobals.logClass.setLogMsg(TAG, "Permission is not granted for " + permission, LogClass.DEBUG_MSG);
                return false;
            }
        }
        appGlobals.logClass.setLogMsg(TAG, "Permission is granted for " + permission, LogClass.DEBUG_MSG);
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

    public void createLocationService(Context context) {
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

        Intent intent = new Intent(context, LocationTrigger.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 234324243, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }

    public void sendLocationToServer(Location location) {

        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
        String loc_name = getLocality(location);

        LocationClass locClass = new LocationClass();
        locClass.setLocation(lat, lng, loc_name, System.currentTimeMillis());

        Gson gson = new Gson();
        final String loc = gson.toJson(locClass);

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
                map.put("locDet", loc);
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
            logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
        }
    }
}

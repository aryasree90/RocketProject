package rocket.club.com.rocketpoker.async;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import rocket.club.com.rocketpoker.ProfileActivity;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.ContactHelper;
import rocket.club.com.rocketpoker.classes.GameInvite;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.classes.LiveUpdateDetails;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.FetchContact;
import rocket.club.com.rocketpoker.utils.LogClass;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ContactAsync extends AsyncTask<Void, ArrayList<ContactClass>, Void> {

    Context context = null;
    AppGlobals appGlobals = null;
    private static final String TAG = "ContactAsync";
    private static String FETCH_CONTACT_URL = AppGlobals.SERVER_URL + "fetchContacts.php";
    final String INIT_URL = AppGlobals.SERVER_URL + "fetchFromDb.php";

    public ContactAsync(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        appGlobals = AppGlobals.getInstance(context);
        appGlobals.contactSyncInProgress = true;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ContactHelper contactHelper = FetchContact.fetchContacts(context, appGlobals);
        contactLoop(contactHelper);

        return null;
    }

    @Override
    protected void onProgressUpdate(ArrayList<ContactClass>... values) {
        super.onProgressUpdate(values);

        //if(!activity.isFinishing() || !activity.isDestroyed())

    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        appGlobals.sharedPref.setContactSyncTime(System.currentTimeMillis());
        appGlobals.contactSyncInProgress = false;
    }

    private void contactLoop(ContactHelper contactHelper) {
        ArrayList<ContactClass> contactList = contactHelper.getContactList();
        if(contactList.size() > 0) {
            sendContactsToServer(contactList, contactHelper);
            publishProgress(contactList);
        }

        if(contactHelper.getCurPos() < contactHelper.getTotal()) {
            FetchContact.contactHandler(context, contactHelper);
            contactLoop(contactHelper);
        }
    }

    private void sendContactsToServer(ArrayList<ContactClass> contactList, ContactHelper contactHelper) {

        JsonArray contactJsonArray = new JsonArray();
        for(ContactClass contactClass : contactList) {
            JsonObject contactJson = new JsonObject();
            contactJson.addProperty(contactClass.getContactName(), contactClass.getPhoneNumber());
            contactJsonArray.add(contactJson);
        }

        Map<String,String> map = new HashMap<String,String>();
        map.put("mobile", appGlobals.sharedPref.getLoginMobile());
        map.put("contact", contactJsonArray.toString());
        map.put("isLast", "" + contactHelper.isAtLast());
        map.put("isFirst", "" + contactHelper.isFirst());

        serverCall(map, contactHelper.isAtLast(), "0", FETCH_CONTACT_URL);
    }

    // To fetch old data from server while installing
    private void fetchInitDataFromServer() {

        String mobile = appGlobals.sharedPref.getLoginMobile();
        String timeStamp = System.currentTimeMillis() + "";

    /*
     *  1   friends
     *  2   events, services
     *  3   live udpates
     *  4   game invites
     */
        initDb(mobile, "1", timeStamp);
        initDb(mobile, "2", timeStamp);
        initDb(mobile, "3", timeStamp);
        initDb(mobile, "4", timeStamp);
    }

    private void initDb(final String mobile, final String type, final String timeStamp) {
        Map<String,String> params = new HashMap<String,String>();
        params.put("mobile", mobile);
        params.put("timeStamp", timeStamp);
        params.put("type", type);

        serverCall(params, false, type, INIT_URL);
    }

    private void sendToDb(String response, String type) {
        Gson gson = new Gson();
        DBHelper db = new DBHelper(context);

        appGlobals.logClass.setLogMsg(TAG, "Reached sendToDb", LogClass.DEBUG_MSG);
        appGlobals.logClass.setLogMsg(TAG, "Type " + type, LogClass.DEBUG_MSG);
        appGlobals.logClass.setLogMsg(TAG, response, LogClass.DEBUG_MSG);

        if(type.equals("1")) {
            UserDetails[] details = gson.fromJson(response, UserDetails[].class);
            ArrayList<UserDetails> userDetails = new ArrayList<UserDetails>(Arrays.asList(details));
            if(userDetails.size() > 0)
                db.insertContactDetails(userDetails, false);
        } else if(type.equals("2")) {
            InfoDetails[] infoDetails = gson.fromJson(response, InfoDetails[].class);
            if(infoDetails.length > 0)
                db.insertInfoDetails(infoDetails, context);
        } else if(type.equals("3")) {
            LiveUpdateDetails[] liveUpdateDetailsList = gson.fromJson(response, LiveUpdateDetails[].class);
            if(liveUpdateDetailsList.length > 0)
                db.insertLiveUpdateDetails(liveUpdateDetailsList);
        } else if(type.equals("4")) {
            GameInvite[] gameInvites = gson.fromJson(response, GameInvite[].class);
            if(gameInvites.length > 0)
                for(GameInvite gameInvite : gameInvites)
                    db.insertInvitationDetails(gameInvite);
        }
    }

    private void serverCall(final Map<String,String> params, final boolean isAtLast, final String type, final String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, response, LogClass.DEBUG_MSG);
                        appGlobals.logClass.setLogMsg(TAG, "Type " + type, LogClass.DEBUG_MSG);

                        if(type.equals("0")) {
                            if (!response.isEmpty()) {
                                Gson gson = new Gson();

                                UserDetails[] details = gson.fromJson(response, UserDetails[].class);

                                if (details.length > 0) {
                                    ArrayList<UserDetails> userDetails = new ArrayList<UserDetails>(Arrays.asList(details));
                                    DBHelper db = new DBHelper(context);
                                    db.insertContactDetails(userDetails, true);
                                }
                            }

                            if(isAtLast && !appGlobals.sharedPref.isContactInit()) {
                                appGlobals.sharedPref.setContactInit(true);

                                fetchInitDataFromServer();
                            }
                        } else {
                            sendToDb(response, type);
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
}

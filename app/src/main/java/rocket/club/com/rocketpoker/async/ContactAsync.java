package rocket.club.com.rocketpoker.async;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.classes.ChatListClass;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.ContactHelper;
import rocket.club.com.rocketpoker.classes.GameInvite;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.classes.LiveUpdateDetails;
import rocket.club.com.rocketpoker.classes.LocationClass;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.FetchContact;
import rocket.club.com.rocketpoker.utils.LogClass;

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
     *  5   chat msg
     */
        initDb(mobile, "1", timeStamp);
        initDb(mobile, "2", timeStamp);
        initDb(mobile, "3", timeStamp);
        initDb(mobile, "4", timeStamp);
        initDb(mobile, "5", timeStamp);
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

        appGlobals.logClass.setLogMsg(TAG, "Reached sendToDb", LogClass.DEBUG_MSG);
        appGlobals.logClass.setLogMsg(TAG, "Type " + type, LogClass.DEBUG_MSG);
        appGlobals.logClass.setLogMsg(TAG, response, LogClass.DEBUG_MSG);

        if(type.equals("1")) {
            UserDetails[] details = gson.fromJson(response, UserDetails[].class);
            ArrayList<UserDetails> userDetails = new ArrayList<UserDetails>(Arrays.asList(details));
            if(userDetails.size() > 0)
                appGlobals.sqLiteDb.insertContactDetails(userDetails, false);
        } else if(type.equals("2")) {
            InfoDetails[] infoDetails = gson.fromJson(response, InfoDetails[].class);
            if(infoDetails.length > 0)
                appGlobals.sqLiteDb.insertInfoDetails(infoDetails, context);
        } else if(type.equals("3")) {
            LiveUpdateDetails[] liveUpdateDetailsList = gson.fromJson(response, LiveUpdateDetails[].class);
            if(liveUpdateDetailsList.length > 0)
                appGlobals.sqLiteDb.insertLiveUpdateDetails(liveUpdateDetailsList);
        } else if(type.equals("4")) {
            GameInvite[] gameInvites = gson.fromJson(response, GameInvite[].class);
            if(gameInvites.length > 0)
                for(GameInvite gameInvite : gameInvites)
                    appGlobals.sqLiteDb.insertInvitationDetails(gameInvite);
        } else if(type.equals("5")) {

            try {
                JSONArray msgArr = new JSONArray(response);
                int size = msgArr.length();

                for(int i=size-1; i>=0; i--) {
                    JSONObject msgObj = msgArr.getJSONObject(i);

                    String msgId = msgObj.getString("msgId");

                    JSONObject msgDet = new JSONObject(msgObj.getString("msgJson"));
                    String timeStamp = msgDet.getString("time");

                    ChatListClass newChatList = new ChatListClass();
                    newChatList.setMsgId(msgId);
                    newChatList.setTime(Long.parseLong(timeStamp));
                    newChatList.setMsg(msgDet.getString("msg"));
                    newChatList.setSenderMob(msgDet.getString("senderMob"));

                    if (msgDet.has("location")) {
                        LocationClass locClass = gson.fromJson(msgDet.getString("location"), LocationClass.class);
                        newChatList.setLocation(locClass);
                    }

                    appGlobals.sqLiteDb.insertMessages(newChatList);
                }
            } catch(Exception e) {
                e.printStackTrace();
                appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
            }
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
                                    appGlobals.sqLiteDb.insertContactDetails(userDetails, true);
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

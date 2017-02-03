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
    private ProfileActivity activity;
    private static final String TAG = "ContactAsync";
    private static String FETCH_CONTACT_URL1 = AppGlobals.SERVER_URL + "/fetch_contacts.php";
    private static String FETCH_CONTACT_URL = AppGlobals.SERVER_URL + "/fetchContacts.php";

    public ContactAsync(ProfileActivity act){
        this.activity = act;
        this.context = act.getApplicationContext();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        appGlobals = AppGlobals.getInstance(context);
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

        serverCall(map);
    }

    private void serverCall(final Map<String,String> params) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_CONTACT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, response, LogClass.DEBUG_MSG);

                        if(!response.isEmpty()) {
                            Gson gson = new Gson();

                            UserDetails[] details = gson.fromJson(response, UserDetails[].class);

                            if(details.length > 0) {
                                ArrayList<UserDetails> userDetails = new ArrayList<UserDetails>(Arrays.asList(details));
                                DBHelper db = new DBHelper(context);
                                db.insertContactDetails(userDetails, true);
                            }
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

    private void sendContactsToServer1(ArrayList<ContactClass> contactList, ContactHelper contactHelper) {

        String resultstr = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(FETCH_CONTACT_URL);

            JSONArray contactJsonArray = new JSONArray();
            for(ContactClass contactClass : contactList) {
                JSONObject contactJson = new JSONObject();
                contactJson.put(contactClass.getContactName(), contactClass.getPhoneNumber());
                contactJsonArray.put(contactJson);
            }

            appGlobals.logClass.setLogMsg(TAG, "Reached " + contactHelper.isFirst(), LogClass.DEBUG_MSG);

            JSONObject jsonData = new JSONObject();
            jsonData.put("ownId", appGlobals.sharedPref.getLoginMobile());
            jsonData.put("contact", contactJsonArray.toString());
            jsonData.put("isLast", contactHelper.isAtLast());
            jsonData.put("isFirst", contactHelper.isFirst());

            httppost.setEntity(new StringEntity(jsonData.toString(), "UTF-8"));

            httppost.setHeader("Content-type", "application/json");
            httppost.setHeader("Accept-Encoding", "application/json");
            httppost.setHeader("Accept-Language", "en-US");

            HttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            resultstr = sb.toString();

            appGlobals.logClass.setLogMsg(TAG, "Result " + resultstr, LogClass.DEBUG_MSG);

            if(resultstr.contains("','")) {
                ArrayList<UserDetails> userDetailList = new ArrayList<UserDetails>();
                UserDetails userDetails = new UserDetails();
                String list[] = resultstr.split("','");
                int len = list.length - 1;
                for(int i=0; i<len; i++) {
                    String item[] = list[i].split("':'");
                    appGlobals.logClass.setLogMsg(TAG, "Contacts " + item[0] + " " + item[1], LogClass.DEBUG_MSG);
//                    userDetails.setUserId(item[0]);
                    userDetails.setMobile(item[1]);
                    userDetails.setUserName(item[2]);

                    userDetailList.add(userDetails);
                }
                /*DBHelper db = new DBHelper(context);
                db.insertContactDetails(userDetailList);*/
            }
        } catch (Exception e) {
            appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
        }
    }
}

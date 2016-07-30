package rocket.club.com.rocketpoker.async;

import android.content.Context;
import android.os.AsyncTask;

import rocket.club.com.rocketpoker.ProfileActivity;
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

public class ContactAsync extends AsyncTask<Void, ArrayList<ContactClass>, Void> {

    Context context = null;
    AppGlobals appGlobals = null;
    private ProfileActivity activity;
    private static final String TAG = "ContactAsync";
    private static String FETCH_CONTACT_URL = AppGlobals.SERVER_URL + "/fetch_contacts.php";

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
            jsonData.put("ownId", appGlobals.sharedPref.getUserId());
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
                    appGlobals.logClass.setLogMsg(TAG, item[0] + " " + item[1], LogClass.DEBUG_MSG);
                    userDetails.setUserId(item[0]);
                    userDetails.setMobile(item[1]);
                    userDetails.setUserName(item[2]);

                    userDetailList.add(userDetails);
                }
                DBHelper db = new DBHelper(context);
                db.insertContactDetils(userDetailList);
            }
        } catch (Exception e) {
            appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
        }
    }
}

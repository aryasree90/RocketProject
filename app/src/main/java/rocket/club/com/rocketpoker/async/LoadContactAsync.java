package rocket.club.com.rocketpoker.async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import rocket.club.com.rocketpoker.LandingActivity;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.utils.AppGlobals;

public class LoadContactAsync extends AsyncTask<Void, Void, ArrayList<ContactClass>> {

    private Context context;
    private LandingActivity activity;
    private AppGlobals appGlobals = null;

    public LoadContactAsync(LandingActivity act){
        this.activity = act;
        this.context = act.getApplicationContext();
        appGlobals = AppGlobals.getInstance(context);
    }
    @Override
    protected ArrayList<ContactClass> doInBackground(Void... params) {

        ArrayList<ContactClass> contactList = appGlobals.sqLiteDb.getContacts(AppGlobals.ACCEPTED_FRIENDS);

        return contactList;
    }

    @Override
    protected void onPostExecute(ArrayList<ContactClass> contactClasses) {
        super.onPostExecute(contactClasses);

        /*if(contactClasses.size() > 0) {
            activity.setContactList(contactClasses);
        }*/
    }
}

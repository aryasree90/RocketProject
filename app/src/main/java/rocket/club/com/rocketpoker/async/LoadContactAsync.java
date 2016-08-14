package rocket.club.com.rocketpoker.async;

import android.content.Context;
import android.os.AsyncTask;

import rocket.club.com.rocketpoker.LandingActivity;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;

import java.util.ArrayList;

public class LoadContactAsync extends AsyncTask<Void, Void, ArrayList<ContactClass>> {

    private Context context;
    private LandingActivity activity;

    public LoadContactAsync(LandingActivity act){
        this.activity = act;
        this.context = act.getApplicationContext();
    }
    @Override
    protected ArrayList<ContactClass> doInBackground(Void... params) {

        DBHelper db = new DBHelper(context);
        ArrayList<ContactClass> contactList = db.getContacts(AppGlobals.ACCEPTED_FRIENDS);

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

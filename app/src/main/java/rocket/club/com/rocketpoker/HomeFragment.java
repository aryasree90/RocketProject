package rocket.club.com.rocketpoker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import rocket.club.com.rocketpoker.adapter.EventListAdapter;
import rocket.club.com.rocketpoker.adapter.NewFriendListAdapter;
import rocket.club.com.rocketpoker.adapter.ServiceListAdapter;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;

public class HomeFragment extends Fragment {

    Context context = null;
    EventListAdapter eventAdapter = null;
    ServiceListAdapter serviceAdapter = null;
    NewFriendListAdapter newFriendAdapter = null;
    ViewPager eventList = null, serviceList = null, newFriendsList = null;
    TextView emptyFriend = null, emptyEvent = null, emptyService = null;
    int[] mResources = {
            R.drawable.event1,
            R.drawable.event2,
            R.drawable.event3,
            R.drawable.event4,
            R.drawable.event5,
    };

    View.OnClickListener clickListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeWidgets(view);

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();

        DBHelper db = new DBHelper(context);

        eventList = (ViewPager) view.findViewById(R.id.eventList);
        emptyEvent = (TextView) view.findViewById(R.id.emptyEventItem);

        if(mResources.length != 0) {
            eventList.setVisibility(View.VISIBLE);
            emptyEvent.setVisibility(View.GONE);
            eventAdapter = new EventListAdapter(context, mResources);
            eventList.setAdapter(eventAdapter);
        } else {
            eventList.setVisibility(View.GONE);
            emptyEvent.setVisibility(View.VISIBLE);
        }

        serviceList = (ViewPager) view.findViewById(R.id.serviceList);
        emptyService = (TextView) view.findViewById(R.id.emptyServiceItem);

        if(mResources.length != 0) {
            serviceList.setVisibility(View.VISIBLE);
            emptyService.setVisibility(View.GONE);
            serviceAdapter = new ServiceListAdapter(context, mResources, clickListener);
            serviceList.setAdapter(serviceAdapter);
        } else {
            serviceList.setVisibility(View.GONE);
            emptyService.setVisibility(View.VISIBLE);
        }

        ArrayList<ContactClass> userList = db.getContacts(AppGlobals.PENDING_FRIENDS);
        newFriendsList = (ViewPager) view.findViewById(R.id.newFriendsList);
        emptyFriend = (TextView) view.findViewById(R.id.emptyFriendItem);

        if(userList != null && userList.size() > 0) {
            newFriendsList.setVisibility(View.VISIBLE);
            emptyFriend.setVisibility(View.GONE);
            newFriendAdapter = new NewFriendListAdapter(context, userList, clickListener);
            newFriendsList.setAdapter(newFriendAdapter);
        } else{
            newFriendsList.setVisibility(View.GONE);
            emptyFriend.setVisibility(View.VISIBLE);
        }
    }
}

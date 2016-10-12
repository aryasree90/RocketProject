package rocket.club.com.rocketpoker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import rocket.club.com.rocketpoker.adapter.EventListAdapter;
import rocket.club.com.rocketpoker.adapter.NewFriendListAdapter;
import rocket.club.com.rocketpoker.adapter.ServiceListAdapter;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public class HomeFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;
    EventListAdapter eventAdapter = null;
    ServiceListAdapter serviceAdapter = null;
    NewFriendListAdapter newFriendAdapter = null;
    ViewPager eventList = null, serviceList = null, newFriendsList = null;
    TextView emptyFriend = null, emptyEvent = null, emptyService = null;
    DBHelper db = null;
    Button addnewfriend;
    LinearLayout emptyFriendlnr;

    ArrayList<ContactClass> userList = null;

    private static final String TAG = "Home Fragment";
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
        setClickListener();
        return view;
    }


    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_addnewfriend:
                        Class setClass = FriendsFragment.class;
                        Fragment fragment = null;
                        try {
                            fragment = (Fragment) setClass.newInstance();
                            Bundle args = new Bundle();
                            args.putInt("type", AppGlobals.FRIEND_LIST);
                            fragment.setArguments(args);
                            appGlobals.currentFragmentClass = setClass;
                        } catch (Exception e) {
                            appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
                        }

                        // Insert the fragment by replacing any existing fragment
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        break;

                }
            }
        };
        addnewfriend.setOnClickListener(clickListener);
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);

        db = new DBHelper(context);

        eventList = (ViewPager) view.findViewById(R.id.eventList);
        emptyEvent = (TextView) view.findViewById(R.id.emptyEventItem);
        addnewfriend=(Button)view.findViewById(R.id.btn_addnewfriend);

        ArrayList<InfoDetails> infoList = db.getRocketsInfo(AppGlobals.EVENT_INFO);

        if(!infoList.isEmpty()) {
            eventList.setVisibility(View.VISIBLE);
            emptyEvent.setVisibility(View.GONE);
            eventAdapter = new EventListAdapter(context, infoList);
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

        newFriendsList = (ViewPager) view.findViewById(R.id.newFriendsList);
        emptyFriend = (TextView) view.findViewById(R.id.emptyFriendItem);
        emptyFriendlnr = (LinearLayout) view.findViewById(R.id.linr_emptyfriendname);

        refreshFriendReqList();
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(AppGlobals.NOTIF_FRND_REQ)) {
                refreshFriendReqList();
            }
        }
    };

    private void refreshFriendReqList() {
        try {
            userList = db.getContacts(AppGlobals.PENDING_FRIENDS);
            if(userList != null && userList.size() > 0) {
                newFriendsList.setVisibility(View.VISIBLE);
                emptyFriendlnr.setVisibility(View.GONE);
                newFriendAdapter = new NewFriendListAdapter(context, userList, clickListener);
                newFriendsList.setAdapter(newFriendAdapter);
            } else{
                newFriendsList.setVisibility(View.GONE);
                emptyFriendlnr.setVisibility(View.VISIBLE);
            }
        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "Exception in broadcast receiver " + e.toString(), LogClass.ERROR_MSG);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter frndReqMsgFilter = new IntentFilter(AppGlobals.NOTIF_FRND_REQ);
        context.registerReceiver(mHandleMessageReceiver, frndReqMsgFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        try {
            context.unregisterReceiver(mHandleMessageReceiver);
        } catch (Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "UnRegister Receiver Error > " + e.getMessage(), LogClass.ERROR_MSG);
        }
    }
}

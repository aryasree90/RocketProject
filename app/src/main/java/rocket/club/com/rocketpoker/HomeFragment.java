package rocket.club.com.rocketpoker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rocket.club.com.rocketpoker.adapter.EventListAdapter;
import rocket.club.com.rocketpoker.adapter.NewFriendListAdapter;
import rocket.club.com.rocketpoker.adapter.ServiceListAdapter;

public class HomeFragment extends Fragment {

    Context context = null;
    EventListAdapter eventAdapter = null;
    ServiceListAdapter serviceAdapter = null;
    NewFriendListAdapter newFriendAdapter = null;
    ViewPager eventList = null, serviceList = null, newFriendsList = null;
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

        eventAdapter = new EventListAdapter(context, mResources);
        eventList = (ViewPager) view.findViewById(R.id.eventList);
        eventList.setAdapter(eventAdapter);

        serviceAdapter = new ServiceListAdapter(context, mResources, clickListener);
        serviceList = (ViewPager) view.findViewById(R.id.serviceList);
        serviceList.setAdapter(serviceAdapter);

        newFriendAdapter = new NewFriendListAdapter(context, mResources, clickListener);
        newFriendsList = (ViewPager) view.findViewById(R.id.newFriendsList);
        newFriendsList.setAdapter(newFriendAdapter);
    }
}

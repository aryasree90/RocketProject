package rocket.club.com.rocketpoker;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rocket.club.com.rocketpoker.adapter.FriendListAdapter;
import rocket.club.com.rocketpoker.classes.FriendsListClass;

public class FriendsFragment extends Fragment {

    Context context = null;
    RecyclerView friendsListView = null;
    FriendListAdapter mAdapter = null;
    ImageButton addNewFriend = null;

    View.OnClickListener clickListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        initializeWidgets(view);
        setClickListener();

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        friendsListView = (RecyclerView) view.findViewById(R.id.friends_recycler_view);
        addNewFriend = (ImageButton) view.findViewById(R.id.add_friend_activity);

        FriendsListClass listClass1 = new FriendsListClass("AAAAA", "11111", "image");
        FriendsListClass listClass2 = new FriendsListClass("BBBBB", "22222", "image");
        FriendsListClass listClass3 = new FriendsListClass("CCCCC", "33333", "image");
        FriendsListClass listClass4 = new FriendsListClass("DDDDD", "44444", "image");
        FriendsListClass listClass5 = new FriendsListClass("EEEEE", "55555", "image");

        List<FriendsListClass> friendsList = new ArrayList<FriendsListClass>();
        friendsList.add(listClass1);
        friendsList.add(listClass2);
        friendsList.add(listClass3);
        friendsList.add(listClass4);
        friendsList.add(listClass5);


        mAdapter = new FriendListAdapter(friendsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        friendsListView.setLayoutManager(mLayoutManager);
        friendsListView.setItemAnimator(new DefaultItemAnimator());
        friendsListView.setAdapter(mAdapter);
    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.add_friend_activity:
                        Toast.makeText(context, "Add friend clicked", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        addNewFriend.setOnClickListener(clickListener);
    }

}

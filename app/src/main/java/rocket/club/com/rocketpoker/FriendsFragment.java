package rocket.club.com.rocketpoker;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rocket.club.com.rocketpoker.adapter.FriendListAdapter;
import rocket.club.com.rocketpoker.classes.FriendsListClass;
import rocket.club.com.rocketpoker.utils.AppGlobals;

public class FriendsFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;

    RecyclerView friendsListView = null;
    FriendListAdapter mAdapter = null;
    Button addNewFriend = null;
    EditText searchFriend = null;

    Dialog dialog = null;
    Button searchBtn = null;
    EditText searchMobile = null;
    LinearLayout showFriendDetails = null;

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
        appGlobals = AppGlobals.getInstance(context);

        friendsListView = (RecyclerView) view.findViewById(R.id.friends_recycler_view);
        addNewFriend = (Button) view.findViewById(R.id.add_new_friend);
        searchFriend = (EditText) view.findViewById(R.id.search_friend);

        searchFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                    case R.id.add_new_friend:
                        createDialog();
                        break;
                    case R.id.acceptFriend:
                        resetDialog();
                        Toast.makeText(context, "Friend request sent", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.rejectFriend:
                        resetDialog();
                        break;
                    case R.id.searchBtn:
                        showFriendDetails.setVisibility(View.VISIBLE);
                        break;
                }
            }
        };

        addNewFriend.setOnClickListener(clickListener);
    }

    private void createDialog()
    {
        dialog=new Dialog(context);
        dialog.setContentView(R.layout.activity_add_new_friend);

        searchBtn = (Button) dialog.findViewById(R.id.searchBtn);
        searchFriend = (EditText) dialog.findViewById(R.id.searchText);
        TextView acceptBtn= (TextView) dialog.findViewById(R.id.acceptFriend);
        TextView rejectBtn= (TextView) dialog.findViewById(R.id.rejectFriend);
        showFriendDetails = (LinearLayout) dialog.findViewById(R.id.show_friend_details);

        acceptBtn.setText("Send Request");
        rejectBtn.setText("Clear");

        acceptBtn.setOnClickListener(clickListener);
        rejectBtn.setOnClickListener(clickListener);
        searchBtn.setOnClickListener(clickListener);

        dialog.show();
    }

    private void resetDialog() {
        showFriendDetails.setVisibility(View.INVISIBLE);
        searchFriend.setText("");
    }

}

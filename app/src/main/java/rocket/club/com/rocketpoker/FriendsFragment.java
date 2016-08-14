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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocket.club.com.rocketpoker.adapter.FriendListAdapter;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.FriendsListClass;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public class FriendsFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;

    RecyclerView friendsListView = null;
    FriendListAdapter mAdapter = null;
    Button addNewFriend = null;
    EditText searchFriend = null;

    Dialog dialog = null;
    Button searchBtn = null;
    LinearLayout showFriendDetails = null;
    TextView friendName, friendMobile;
    ArrayList<UserDetails> list = null;

    View.OnClickListener clickListener = null;
    private final String TAG = "Friends Fragment";
    final String FRIEND_REQ_URL = AppGlobals.SERVER_URL + "frndReq.php";
    final String FRIEND_SEARCH_URL = AppGlobals.SERVER_URL + "searchFriend.php";

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

        List<FriendsListClass> friendsList = new ArrayList<FriendsListClass>();
        try {
            DBHelper db = new DBHelper(context);
            ArrayList<ContactClass> contactList = db.getContacts(AppGlobals.ALL_FRIENDS);
            for(ContactClass contactClass : contactList) {
                String name = contactClass.getContactName() + "-" + contactClass.getStatus();
                friendsList.add(new FriendsListClass(name, contactList.get(0).getPhoneNumber(), "image"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

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
                        if(appGlobals.isNetworkConnected(context)) {

                            String frnd_mob = searchFriend.getText().toString();
                            Map<String,String> map = new HashMap<String,String>();
                            map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                            map.put("frnd_mobile", frnd_mob);
                            map.put("task", appGlobals.NEW_FRND_REQ);

                            serverCall(map, FRIEND_REQ_URL);
                            resetDialog();
                            appGlobals.toastMsg(context, getString(R.string.req_sent), appGlobals.LENGTH_LONG);
                        } else {
                            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                        }
                        break;
                    case R.id.rejectFriend:
                        resetDialog();
                        break;
                    case R.id.searchBtn:
                        showFriendDetails.setVisibility(View.VISIBLE);
                        String frnd_mob = searchFriend.getText().toString();

                        Map<String,String> map = new HashMap<String,String>();
                        map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                        map.put("frnd_mobile", frnd_mob);
                        serverCall(map, FRIEND_SEARCH_URL);
                        break;
                }
            }
        };

        addNewFriend.setOnClickListener(clickListener);
    }

    private void serverCall(final Map<String, String> map, final String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(URL.equals(FRIEND_REQ_URL)) {
                            if(response.equals("Success")) {
                                if (list != null && list.size() == 1) {
                                    DBHelper db = new DBHelper(context);
                                    db.insertContactDetails(list);
                                }
                            }
                        } else if(URL.equals(FRIEND_SEARCH_URL)){
                            Gson gson = new Gson();
                            UserDetails userDetails = gson.fromJson(response, UserDetails.class);

                            friendName.setText(userDetails.getUserName());
                            friendMobile.setText(userDetails.getMobile());

                            list = new ArrayList<UserDetails>();
                            list.add(userDetails);
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

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
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

        friendName = (TextView) dialog.findViewById(R.id.friendName);
        friendMobile = (TextView) dialog.findViewById(R.id.friendNumber);

        acceptBtn.setText(getString(R.string.send_req));
        rejectBtn.setText(getString(R.string.btn_clear1));

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

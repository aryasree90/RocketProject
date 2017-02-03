package rocket.club.com.rocketpoker;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocket.club.com.rocketpoker.adapter.FriendListAdapter;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.FriendsListClass;
import rocket.club.com.rocketpoker.classes.GameInvite;
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
    ImageButton searchBtn = null;

    LinearLayout showFriendDetails = null;
    ImageView friendImage = null;
    TextView friendName, friendMobile,friendNotFoundTxt;
    ArrayList<UserDetails> list = null;
    String searchAFriend = "";
    List<FriendsListClass> friendsList = new ArrayList<FriendsListClass>();

    View.OnClickListener clickListener = null;
    private final String TAG = "Friends Fragment";
    final String FRIEND_REQ_URL = AppGlobals.SERVER_URL + "frndReq.php";
    public static final String FRIEND_SEARCH_URL = AppGlobals.SERVER_URL + "searchFriend.php";
    final String INVITE_TO_PLAY_URL = AppGlobals.SERVER_URL + "inviteToPlay.php";
    private int pageType = 1;
    ProgressDialog progressDialog = null;

    MaterialBetterSpinner selectGameType = null;
    Button btnDateTime = null;
    String selDateTime = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        initializeWidgets(view);
        setClickListener();

        if(pageType == AppGlobals.INVITE_TO_CLUB) {
            setTimeSlotDialog();
        }

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);

        appGlobals.selectedNums.clear();
        appGlobals.selectedPos.clear();
        Bundle bundle = getArguments();
        pageType = bundle.getInt("type");

        friendsListView = (RecyclerView) view.findViewById(R.id.friends_recycler_view);
        addNewFriend = (Button) view.findViewById(R.id.add_new_friend);

        if(pageType == AppGlobals.FRIEND_LIST) {
            addNewFriend.setText(getString(R.string.add_new_friend));
        } else {
            addNewFriend.setText(getString(R.string.invite_to_play));
        }

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

        fetchFriendList();

        mAdapter = new FriendListAdapter(friendsList, pageType, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        friendsListView.setLayoutManager(mLayoutManager);
        friendsListView.setItemAnimator(new DefaultItemAnimator());
        friendsListView.setAdapter(mAdapter);
    }

    private void fetchFriendList() {
        try {
            friendsList.clear();
            DBHelper db = new DBHelper(context);

            int contactType = AppGlobals.SUGGESTED_FRIENDS;
            if(pageType == AppGlobals.INVITE_TO_CLUB) {
                contactType = AppGlobals.ACCEPTED_FRIENDS;
            }

            ArrayList<ContactClass> contactList = db.getContacts(contactType);
            for(ContactClass contactClass : contactList) {
                String name = contactClass.getContactName();
                String image = contactClass.getUserImage();
                friendsList.add(new FriendsListClass(name, contactClass.getPhoneNumber(), image, contactClass.getStatus()));
            }
        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
        }

    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.add_new_friend:
                        if (pageType == AppGlobals.FRIEND_LIST) {
                            createDialog();
                        } else {

                            if(!appGlobals.isNetworkConnected(context)) {
                                appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                                break;
                            }

                            String listMob = "";
                            int count = appGlobals.selectedNums.size();
                            for (String mob : appGlobals.selectedNums) {
                                if (!TextUtils.isEmpty(listMob))
                                    listMob += "::";
                                listMob += mob;
                            }
                            appGlobals.selectedNums.clear();
                            appGlobals.selectedPos.clear();

                            String game = selectGameType.getText().toString();
                            String schedule = btnDateTime.getText().toString();

                            if (TextUtils.isEmpty(listMob)) {
                                appGlobals.toastMsg(context, getString(R.string.select_friend), appGlobals.LENGTH_LONG);
                            } else if (TextUtils.isEmpty(game)) {
                                appGlobals.toastMsg(context, getString(R.string.select_game), appGlobals.LENGTH_LONG);
                                setTimeSlotDialog();
                            } else if (TextUtils.isEmpty(schedule)) {
                                appGlobals.toastMsg(context, getString(R.string.select_time), appGlobals.LENGTH_LONG);
                                setTimeSlotDialog();
                            } else {
                                DBHelper db = new DBHelper(context);
                                String timeStamp = "" +  System.currentTimeMillis();
                                GameInvite gameInvite = new GameInvite(appGlobals.sharedPref.getLoginMobile(), listMob, game, schedule, timeStamp, count);

                                db.insertInvitationDetails(gameInvite);

                                Map<String, String> frnd_map = new HashMap<String, String>();
                                frnd_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                                frnd_map.put("invite_list", listMob);
                                frnd_map.put("game", game);
                                frnd_map.put("schedule", schedule);
                                frnd_map.put("timeStamp", timeStamp);
                                frnd_map.put("count", "" + count);
                                frnd_map.put("task", AppGlobals.SEND_INVITE);
                                progressDialog = appGlobals.showDialog(context, getString(R.string.send_invitation));

                                serverCall(frnd_map, INVITE_TO_PLAY_URL);
                            }
                        }
                        break;
                    case R.id.acceptFriend:
                        friendNotFoundTxt.setVisibility(View.GONE);
                        String frndMob = friendMobile.getText().toString();

                        DBHelper db = new DBHelper(context);
                        ContactClass contactFrnd = db.getContacts(frndMob);

                        if(contactFrnd != null) {
                            String param = "";
                            if(!TextUtils.isEmpty(contactFrnd.getContactName())) {
                                param = contactFrnd.getContactName();
                            } else {
                                param = contactFrnd.getPhoneNumber();
                            }
                            String alreadyFrnd = getString(R.string.already_frnd, param);
                            appGlobals.toastMsg(context, alreadyFrnd, appGlobals.LENGTH_LONG);
                        } else {
                            Map<String, String> frnd_map = new HashMap<String, String>();
                            frnd_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                            frnd_map.put("frnd_mobile", friendMobile.getText().toString());
                            frnd_map.put("task", appGlobals.NEW_FRND_REQ);
                            progressDialog = appGlobals.showDialog(context, getString(R.string.saving));
                            serverCall(frnd_map, FRIEND_REQ_URL);
                            resetDialog();
                        }
                        break;
                    case R.id.rejectFriend:
                        resetDialog();
                        break;
                    case R.id.searchBtn:
//                        String search_mob = searchFriend.getText().toString();
                        searchAFriend = searchFriend.getText().toString();

                        if(searchAFriend.isEmpty()) {
                            appGlobals.toastMsg(context, getString(R.string.login_invalid_num), appGlobals.LENGTH_LONG);
                            return;
                        } else if (appGlobals.sharedPref.getLoginMobile().contains(searchAFriend)) {
                            appGlobals.toastMsg(context, getString(R.string.req_to_own_num), appGlobals.LENGTH_LONG);
                        } else {
                            searchFriend.setEnabled(false);
                            Map<String, String> search_map = new HashMap<String, String>();
                            search_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                            search_map.put("frnd_mobile", searchAFriend);
                            progressDialog = appGlobals.showDialog(context, getString(R.string.search_member));
                            serverCall(search_map, FRIEND_SEARCH_URL);
                        }
                        break;
                    case R.id.selectDateTime:
                            showDatePickerDialog();
                        break;
                    case R.id.confirmTime:
                        if(dialog != null)
                            dialog.cancel();
                        break;
                }
            }
        };

        addNewFriend.setOnClickListener(clickListener);
    }

    private void showDatePickerDialog() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        selDateTime = checkTime(dayOfMonth) + "-" + checkTime(month) + "-" + year;
                        showTimePickerDialog();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        selDateTime += " " + checkTime(hourOfDay) + ":" + checkTime(minute);
                        btnDateTime.setText(selDateTime);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private String checkTime(int val) {
        String finalVal = "";
        if(val < 10)
            finalVal = "0" + val;
        else
            finalVal = "" + val;
        return finalVal;
    }

    private void serverCall(final Map<String, String> map, final String URL) {

        if(!appGlobals.isNetworkConnected(context)) {
            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
            appGlobals.cancelDialog(progressDialog);
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(URL.equals(FRIEND_REQ_URL)) {
                            if(response.equals("Success")) {
                                if (list != null && list.size() == 1) {
                                    DBHelper db = new DBHelper(context);
                                    db.insertContactDetails(list, false);
                                    appGlobals.toastMsg(context, getString(R.string.req_sent), appGlobals.LENGTH_LONG);
                                } else {
                                    appGlobals.toastMsg(context, getString(R.string.req_not_sent), appGlobals.LENGTH_LONG);
                                }
                            }
                        } else if(URL.equals(FRIEND_SEARCH_URL)){
                            try {

                                friendNotFoundTxt.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                UserDetails userDetails = gson.fromJson(response, UserDetails.class);

                                friendName.setText(userDetails.getUserName());
                                friendMobile.setText(userDetails.getMobile());

                                String thumbName = appGlobals.thumbImageName(userDetails.getUserImage());

                                if(!TextUtils.isEmpty(thumbName)) {
                                    String imageUrl = AppGlobals.SERVER_URL + thumbName;

                                    appGlobals.loadImageFromServerWithDefault(imageUrl, friendImage,
                                            userDetails.getUserImage(), false, context);
                                } else {
                                    friendImage.setImageResource(R.drawable.default_profile);
                                }

                                list = new ArrayList<UserDetails>();
                                list.add(userDetails);

                                showFriendDetails.setVisibility(View.VISIBLE);
                            } catch(Exception e) {
                                appGlobals.toastMsg(context, getString(R.string.friend_not_found), appGlobals.LENGTH_LONG);
                                searchFriend.setText("");
                                friendNotFoundTxt.setVisibility(View.VISIBLE);
                                searchAFriend = "";
                                searchFriend.setEnabled(true);
                                searchFriend.requestFocus();
                            }
                        } else if(URL.equals(INVITE_TO_PLAY_URL)) {
                            Class fragmentInvite = InvitationListFragment.class;

                            try {
                                Fragment fragment = (Fragment) fragmentInvite.newInstance();
                                appGlobals.currentFragmentClass = fragmentInvite;

                                // Insert the fragment by replacing any existing fragment
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                            } catch (Exception e) {
                                appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
                            }
                        }
                        appGlobals.cancelDialog(progressDialog);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
                        appGlobals.cancelDialog(progressDialog);
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

    private void setTimeSlotDialog() {
        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_time_slot);

        dialog.setTitle(getString(R.string.set_game_time));

        selectGameType = (MaterialBetterSpinner) dialog.findViewById(R.id.selectGameType);
        btnDateTime = (Button) dialog.findViewById(R.id.selectDateTime);
        Button confirmBtn = (Button) dialog.findViewById(R.id.confirmTime);

        String[] GAME_LIST = getResources().getStringArray(R.array.game_list);

        ArrayAdapter<String> gameTypeAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, GAME_LIST);
        selectGameType = (MaterialBetterSpinner)
                dialog.findViewById(R.id.selectGameType);
        selectGameType.setAdapter(gameTypeAdapter);

        selectGameType.setOnClickListener(clickListener);
        btnDateTime.setOnClickListener(clickListener);
        confirmBtn.setOnClickListener(clickListener);

        dialog.show();

        appGlobals.setDialogLayoutParams(dialog, context, false, true);

    }

    private void createDialog() {
        dialog=new Dialog(context);
        dialog.setContentView(R.layout.activity_add_new_friend);

        dialog.setTitle(getString(R.string.search_friends));

        searchBtn = (ImageButton) dialog.findViewById(R.id.searchBtn);
        searchFriend = (EditText) dialog.findViewById(R.id.searchText);
        TextView acceptBtn= (TextView) dialog.findViewById(R.id.acceptFriend);
        TextView rejectBtn= (TextView) dialog.findViewById(R.id.rejectFriend);
        showFriendDetails = (LinearLayout) dialog.findViewById(R.id.show_friend_details);
        friendNotFoundTxt = (TextView) dialog.findViewById(R.id.txt_friend_not_found);

        friendImage = (ImageView) dialog.findViewById(R.id.friendImage);
        friendName = (TextView) dialog.findViewById(R.id.friendName);
        friendMobile = (TextView) dialog.findViewById(R.id.friendNumber);
        searchFriend.requestFocus();

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
        searchFriend.setEnabled(true);
        friendNotFoundTxt.setVisibility(View.GONE);
        dialog.cancel();
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(AppGlobals.NOTIF_FRND_REQ_RESP)) {
                fetchFriendList();
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter frndReqMsgFilter = new IntentFilter(AppGlobals.NOTIF_FRND_REQ_RESP);
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

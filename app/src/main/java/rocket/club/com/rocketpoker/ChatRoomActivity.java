package rocket.club.com.rocketpoker;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocket.club.com.rocketpoker.adapter.ChatRoomAdapter;
import rocket.club.com.rocketpoker.adapter.FriendListAdapter;
import rocket.club.com.rocketpoker.classes.ChatListClass;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.FriendsListClass;
import rocket.club.com.rocketpoker.classes.LocationClass;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

import static rocket.club.com.rocketpoker.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static rocket.club.com.rocketpoker.CommonUtilities.EXTRA_MESSAGE;

/**
 * Created by Admin on 8/27/2016.
 */
public class ChatRoomActivity extends AppCompatActivity {

    Context context = null;
    AppGlobals appGlobals = null;

    RecyclerView chatRecyclerView = null;
    EditText chatMsg = null;
    ImageButton sendMsg = null;
    Toolbar toolBar = null;
    ChatRoomAdapter mAdapter = null;
    View.OnClickListener clickListener = null;
    List<ChatListClass> chatList = null;
    public static final String CHAT_MESSAGE = "ChatMessage";

    final String TAG = "Chat Room Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        initializeWidgets();
        setClickListener();
    }

    private void initializeWidgets() {
        context = getApplicationContext();
        appGlobals = AppGlobals.getInstance(context);

        chatRecyclerView = (RecyclerView) findViewById(R.id.chat_list);
        chatMsg = (EditText) findViewById(R.id.chat_msg);
        sendMsg = (ImageButton) findViewById(R.id.send_msg);

        toolBar = (Toolbar)findViewById(R.id.hometoolbar);
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chat Room");
        toolBar.setNavigationIcon(R.mipmap.ic_arrow_back);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        appGlobals.sharedPref.setChatCount(0);

        IntentFilter chatMsgFilter = new IntentFilter(AppGlobals.CHAT_ROOM);
        registerReceiver(mHandleMessageReceiver, chatMsgFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        chatList = new ArrayList<ChatListClass>();
        try {
            DBHelper db = new DBHelper(context);
            chatList = db.getMessages();
        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
        }
        mAdapter = new ChatRoomAdapter(chatList, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        chatRecyclerView.setLayoutManager(mLayoutManager);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatRecyclerView.setAdapter(mAdapter);
        chatRecyclerView.scrollToPosition(chatList.size()-1);
        AppGlobals.inChatRoom = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AppGlobals.inChatRoom = false;

        try {
            unregisterReceiver(mHandleMessageReceiver);
        } catch (Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "UnRegister Receiver Error > " + e.getMessage(), LogClass.ERROR_MSG);
        }
    }

    private void setClickListener() {

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.send_msg:

                        String msg = chatMsg.getText().toString();

                        if(!TextUtils.isEmpty(msg)) {
                            if(appGlobals.isNetworkConnected(context)) {
                                ChatListClass msgClass = new ChatListClass();
                                String mob = appGlobals.sharedPref.getLoginMobile();

                                msgClass.setSenderMob(mob);
                                msgClass.setMsg(msg);
                                msgClass.setTime(System.currentTimeMillis());
//                                msgClass.setLocation(appGlobals.sharedPref.getLocation());

                                Gson gson = new Gson();
                                LocationClass locClass = gson.fromJson(appGlobals.sharedPref.getLocation(), LocationClass.class);
                                msgClass.setLocation(locClass);

                                DBHelper db = new DBHelper(context);
                                db.insertMessages(msgClass);

                                String str = gson.toJson(msgClass);
                                serverCall(str);

                                chatMsg.setText("");
                                chatList.add(msgClass);
                                mAdapter.notifyDataSetChanged();
                                chatRecyclerView.scrollToPosition(chatList.size() - 1);
                            }
                        }
                        break;
                }
            }
        };
        chatMsg.setOnClickListener(clickListener);
        sendMsg.setOnClickListener(clickListener);
    }

    private void serverCall(final String msgJson) {
        final String VALIDATION_URL = AppGlobals.SERVER_URL + "chatRoom.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Response from server " + response , LogClass.DEBUG_MSG);

                        try {

                            JSONArray msgArr = new JSONArray(response);
                            JSONObject msgObj = msgArr.getJSONObject(0);

                            String msgId = msgObj.getString("msgId");

                            JSONObject msgDet = new JSONObject(msgObj.getString("msgJson"));
                            String timeStamp = msgDet.getString("time");

                            DBHelper db = new DBHelper(context);
                            db.updateMessages(msgId, timeStamp);

                        } catch(Exception e) {
                            appGlobals.logClass.setLogMsg(TAG, "Exception in response" + e.toString(), LogClass.ERROR_MSG);
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
                Map<String,String> map = new HashMap<String,String>();
                map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                map.put("msgJson", msgJson);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(AppGlobals.CHAT_ROOM)) {

                try {
                    String message = intent.getExtras().getString(CHAT_MESSAGE, "");
                    JSONArray msgArr = new JSONArray(message);
                    JSONObject msgObj = msgArr.getJSONObject(0);

                    String msgId = msgObj.getString("msgId");

                    JSONObject msgDet = new JSONObject(msgObj.getString("msgJson"));
                    String timeStamp = msgDet.getString("time");

                    ChatListClass newChatList = new ChatListClass();
                    newChatList.setMsgId(msgId);
                    newChatList.setTime(Long.parseLong(timeStamp));
                    newChatList.setMsg(msgDet.getString("msg"));
                    newChatList.setSenderMob(msgDet.getString("senderMob"));

                    if(msgDet.has("location")) {
                        Gson gson = new Gson();
                        LocationClass locClass = gson.fromJson(msgDet.getString("location"), LocationClass.class);
                        newChatList.setLocation(locClass);
                    }

                    if(mAdapter != null && chatList != null) {
                        chatList.add(newChatList);
                        mAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(chatList.size()-1);
                    }

                } catch(Exception e) {
                    appGlobals.logClass.setLogMsg(TAG, "Exception in auto set otp " + e.toString(), LogClass.ERROR_MSG);
                }

            }
        }
    };
}

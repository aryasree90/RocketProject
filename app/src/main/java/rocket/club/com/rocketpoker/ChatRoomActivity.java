package rocket.club.com.rocketpoker;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import rocket.club.com.rocketpoker.adapter.ChatRoomAdapter;
import rocket.club.com.rocketpoker.adapter.FriendListAdapter;
import rocket.club.com.rocketpoker.classes.ChatListClass;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.FriendsListClass;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;

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

        List<ChatListClass> chatList= new ArrayList<ChatListClass>();
        try {
            DBHelper db = new DBHelper(context);
            chatList = db.getMessages();
        } catch(Exception e) {
            e.printStackTrace();
        }
        mAdapter = new ChatRoomAdapter(chatList, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        chatRecyclerView.setLayoutManager(mLayoutManager);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatRecyclerView.setAdapter(mAdapter);
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
                                msgClass.setLocation(appGlobals.sharedPref.getLocation());

                                DBHelper db = new DBHelper(context);

                                if(db.insertMessages(msgClass)) {
                                    Gson gson = new Gson();
                                    String str = gson.toJson(msgClass);

                                    Log.d("____", "____" + str);
                                }
                            }
                        }
                        break;
                }
            }
        };
        chatMsg.setOnClickListener(clickListener);
        sendMsg.setOnClickListener(clickListener);
    }
}

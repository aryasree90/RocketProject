package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.ChatListClass;
import rocket.club.com.rocketpoker.classes.FriendsListClass;
import rocket.club.com.rocketpoker.utils.AppGlobals;

/**
 * Created by Admin on 8/27/2016.
 */
public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.MyViewHolder> {

    private List<ChatListClass> chatList;
    AppGlobals appGlobals = null;
    final private String TAG = "ChatRoomAdapter";
    String loginNum = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout receivedMsgLayout, sentMsgLayout;

        public TextView rec_senderId, rec_msg, rec_time;
        public TextView sent_msg, sent_time;

        public MyViewHolder(View view) {
            super(view);
            receivedMsgLayout = (LinearLayout) view.findViewById(R.id.received_msg_layout);
            rec_senderId = (TextView) view.findViewById(R.id.sender_name);
            rec_msg = (TextView) view.findViewById(R.id.chat_msg);
            rec_time = (TextView) view.findViewById(R.id.sender_time);

            sentMsgLayout = (LinearLayout) view.findViewById(R.id.sent_msg_layout);
            sent_msg = (TextView) view.findViewById(R.id.sent_msg);
            sent_time = (TextView) view.findViewById(R.id.sent_time);
        }
    }


    public ChatRoomAdapter(List<ChatListClass> chatList, Context context) {
        this.chatList = chatList;
        appGlobals = AppGlobals.getInstance(context);

        loginNum = appGlobals.sharedPref.getLoginMobile();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChatListClass itemList = chatList.get(position);


        if(itemList.getSenderMob().equals(loginNum)) {
            holder.sentMsgLayout.setVisibility(View.VISIBLE);
            holder.receivedMsgLayout.setVisibility(View.GONE);

            holder.sent_msg.setText(itemList.getMsg());
            holder.sent_time.setText(""+itemList.getTime());
        } else {
            holder.sentMsgLayout.setVisibility(View.GONE);
            holder.receivedMsgLayout.setVisibility(View.VISIBLE);

            holder.rec_senderId.setText(itemList.getSenderMob());
            holder.rec_msg.setText(itemList.getMsg());
            holder.rec_time.setText(""+itemList.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}

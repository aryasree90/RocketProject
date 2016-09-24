package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.games.Game;

import java.util.List;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.ChatListClass;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.GameInvite;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;

/**
 * Created by Admin on 8/27/2016.
 */
public class GameInviteAdapter extends RecyclerView.Adapter<GameInviteAdapter.MyViewHolder> {

    private List<GameInvite> gameInviteList;
    AppGlobals appGlobals = null;
    final private String TAG = "GameInviteAdapter";
    String loginNum = "";
    Context context = null;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout btnLayout;
        public TextView game_invite, owner, time, count, status;
        public Button accept, reject;

        public MyViewHolder(View view) {
            super(view);

            game_invite = (TextView) view.findViewById(R.id.gameName);
            owner = (TextView) view.findViewById(R.id.inviteowner);
            time = (TextView) view.findViewById(R.id.invitetime);
            count = (TextView) view.findViewById(R.id.invitecount);
            status = (TextView) view.findViewById(R.id.invitestatus);

            btnLayout = (LinearLayout) view.findViewById(R.id.btnLayout);
            accept = (Button) view.findViewById(R.id.accept);
            reject = (Button) view.findViewById(R.id.reject);
        }
    }

    public GameInviteAdapter(List<GameInvite> gameInviteList, Context context) {
        this.gameInviteList = gameInviteList;
        this.context = context;
        appGlobals = AppGlobals.getInstance(context);

        loginNum = appGlobals.sharedPref.getLoginMobile();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_invite_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GameInvite itemList = gameInviteList.get(position);

//        String time = AppGlobals.convertTime(itemList.getTime());

        if(itemList.getSenderMob().equals(loginNum)) {
            holder.btnLayout.setVisibility(View.GONE);
            holder.status.setVisibility(View.GONE);
        } else {
            DBHelper db = new DBHelper(context);
            ContactClass contactClass = db.getContacts(itemList.getSenderMob());

            String senderName = "";

            if (contactClass != null && !TextUtils.isEmpty(contactClass.getContactName())) {
                senderName = contactClass.getContactName();
            } else {
                senderName = itemList.getSenderMob();
            }
            holder.owner.setText(senderName);

            if(itemList.getStatus() == AppGlobals.UNSELECT_GAME) {
                holder.btnLayout.setVisibility(View.VISIBLE);
                holder.status.setVisibility(View.GONE);
            } else {
                holder.btnLayout.setVisibility(View.GONE);
                holder.status.setVisibility(View.VISIBLE);
                if(itemList.getStatus() == AppGlobals.ACCEPT_GAME) {
                    holder.status.setText(context.getString(R.string.accepted));
                    holder.status.setTextColor(Color.GREEN);
                } else {
                    holder.status.setText(context.getString(R.string.rejected));
                    holder.status.setTextColor(Color.RED);
                }
            }
        }

        holder.game_invite.setText(itemList.getGame());
        holder.time.setText(itemList.getSchedule());
        holder.count.setText("Members : " + itemList.getCount());
    }

    @Override
    public int getItemCount() {
        return gameInviteList.size();
    }
}

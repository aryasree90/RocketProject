package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.ChatListClass;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;

/**
 * Created by Admin on 9/25/2016.
 */
public class GameMembersAdapter  extends RecyclerView.Adapter<GameMembersAdapter.MyViewHolder> {

    private List<String> itemList;
    AppGlobals appGlobals = null;
    final private String TAG = "GameMembersAdapter";
    String loginNum = "";
    Context context = null;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView memberId, status;

        public MyViewHolder(View view) {
            super(view);

            memberId = (TextView) view.findViewById(R.id.member_id);
            status = (TextView) view.findViewById(R.id.member_status);
        }
    }


    public GameMembersAdapter(List<String> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
        appGlobals = AppGlobals.getInstance(context);

        loginNum = appGlobals.sharedPref.getLoginMobile();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_members_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String item = itemList.get(position);

        Log.d("_____", "_____ " + item);

        String memberId = "";//item.split(":")[0];
        String memberStatusId = "";//item.split(":")[1];

        if(item.contains(":")) {

            memberId = item.split(":")[0];
            memberStatusId = item.split(":")[1].trim();

            holder.memberId.setText(memberId);
Log.d("__________", "________________ " + memberId + " " + memberStatusId);
            String memberStatus = "";
            if (memberStatusId.equals(AppGlobals.ACCEPT_GAME)) {
                memberStatus = context.getString(R.string.accepted);
            } else if (memberStatusId.equals(AppGlobals.REJECT_GAME)) {
                memberStatus = context.getString(R.string.rejected);
            }
            holder.status.setText(memberStatus);
        } else {
            holder.memberId.setText(item);
            holder.status.setText(context.getString(R.string.no_resp));
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}


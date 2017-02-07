package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.ContactClass;
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

        String memberId = "";//item.split(":")[0];
        int memberStatusId = 0;//item.split(":")[1];

        if(item.contains(":")) {

            memberId = item.split(":")[0];
            memberStatusId = Integer.parseInt(item.split(":")[1].trim());

            ContactClass contactClass = appGlobals.sqLiteDb.getContacts(memberId);

            if(contactClass != null && !contactClass.getContactName().isEmpty())
                holder.memberId.setText(contactClass.getContactName());
            else
                holder.memberId.setText(memberId);

            String memberStatus = "";
            if (memberStatusId == AppGlobals.ACCEPT_GAME) {
                memberStatus = context.getString(R.string.accepted);
            } else if (memberStatusId == AppGlobals.REJECT_GAME) {
                memberStatus = context.getString(R.string.rejected);
            }
            holder.status.setText(memberStatus);
        } else {
//            holder.memberId.setText(item);

            ContactClass contactClass = appGlobals.sqLiteDb.getContacts(item);

            if(contactClass != null && !contactClass.getContactName().isEmpty())
                holder.memberId.setText(contactClass.getContactName());
            else
                holder.memberId.setText(item);

            holder.status.setText(context.getString(R.string.no_resp));
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}


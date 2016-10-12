package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.FriendsListClass;
import rocket.club.com.rocketpoker.utils.AppGlobals;

/**
 * Created by Admin on 7/22/2016.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder> implements Filterable {

    Context context;
    AppGlobals appGlobals;
    private int pageType = 1;
    private CheckBox checkBox = null;
    private List<FriendsListClass> friendsList;
    private List<FriendsListClass> tempList;
    private View.OnClickListener clickListener = null;
    final private String TAG = "FriendsListAdapter";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName, friendNumber;
        ImageView friendImage;
        CheckBox selectedItem;

        public MyViewHolder(View view) {
            super(view);
            selectedItem = (CheckBox) view.findViewById(R.id.selectedName);
            friendName = (TextView) view.findViewById(R.id.friendName);
            friendNumber = (TextView) view.findViewById(R.id.friendNumber);
            friendImage = (ImageView) view.findViewById(R.id.friendImage);
        }
    }


    public FriendListAdapter(List<FriendsListClass> friendsList, int pageType, Context context) {
        this.friendsList = friendsList;
        this.pageType = pageType;
        this.context = context;
        this.appGlobals = AppGlobals.getInstance(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_friends_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FriendsListClass friendList = friendsList.get(position);
        holder.friendName.setText(friendList.getName());
        holder.friendNumber.setText(friendList.getMobile());
        holder.friendImage.setImageResource(R.drawable.default_profile);

        if(pageType == AppGlobals.FRIEND_LIST) {
            holder.selectedItem.setVisibility(View.GONE);
        } else {
            holder.selectedItem.setVisibility(View.VISIBLE);
            checkBox = holder.selectedItem;
            setClickListener(friendList.getMobile(), position);
        }
    }

    private void setClickListener(final String mob, final int pos) {
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appGlobals.selectedPos.contains(pos)) {
                    int mobPos = appGlobals.selectedPos.indexOf(pos);
                    appGlobals.selectedNums.remove(mobPos);
                    appGlobals.selectedPos.remove(mobPos);
                } else {
                    appGlobals.selectedNums.add(mob);
                    appGlobals.selectedPos.add(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<FriendsListClass> results = new ArrayList<FriendsListClass>();
                if (tempList == null)
                    tempList = friendsList;
                if (constraint != null) {
                    if (tempList != null & tempList.size() > 0) {
                        for (final FriendsListClass friend : tempList) {
                            if (friend.getName().toLowerCase().contains(constraint.toString()))
                                results.add(friend);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null) {
                    friendsList = (ArrayList<FriendsListClass>) results.values;
                    notifyDataSetChanged();
                }

            }
        };
    }
}
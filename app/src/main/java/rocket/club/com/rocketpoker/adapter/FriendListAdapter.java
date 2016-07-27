package rocket.club.com.rocketpoker.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.FriendsListClass;

/**
 * Created by Admin on 7/22/2016.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder> implements Filterable {

    private List<FriendsListClass> friendsList;
    private List<FriendsListClass> tempList;
    final private String TAG = "FriendsListAdapter";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName, friendNumber;
        ImageView friendImage;

        public MyViewHolder(View view) {
            super(view);
            friendName = (TextView) view.findViewById(R.id.friendName);
            friendNumber = (TextView) view.findViewById(R.id.friendNumber);
            friendImage = (ImageView) view.findViewById(R.id.friendImage);
        }
    }


    public FriendListAdapter(List<FriendsListClass> friendsList) {
        this.friendsList = friendsList;
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
                if(results == null)
                    Log.d(TAG, "Result is null");
                Log.d(TAG, "Results found " + results.count);
                friendsList = (ArrayList<FriendsListClass>) results.values;
                notifyDataSetChanged();

            }
        };
    }
}
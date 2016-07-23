package rocket.club.com.rocketpoker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.FriendsListClass;

/**
 * Created by Admin on 7/22/2016.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder> {

    private List<FriendsListClass> friendsList;

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
}
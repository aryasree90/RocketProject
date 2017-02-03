package rocket.club.com.rocketpoker.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
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
        CircleImageView friendImage;
        CheckBox selectedItem;

        public MyViewHolder(View view) {
            super(view);
            selectedItem = (CheckBox) view.findViewById(R.id.selectedName);
            friendName = (TextView) view.findViewById(R.id.friendName);
            friendNumber = (TextView) view.findViewById(R.id.friendNumber);
            friendImage = (CircleImageView) view.findViewById(R.id.friendImage);
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
        final FriendsListClass friendList = friendsList.get(position);
        holder.friendName.setText(friendList.getName());
        holder.friendNumber.setText(friendList.getMobile());

        String thumbName = appGlobals.thumbImageName(friendList.getImage());

        if(friendList.getStatus() != AppGlobals.SUGGESTED_FRIENDS && !TextUtils.isEmpty(thumbName)) {
            String imageUrl = AppGlobals.SERVER_URL + thumbName;
            appGlobals.loadImageFromServerWithDefault(imageUrl, holder.friendImage, "", false, context);
        } else {
            holder.friendImage.setVisibility(View.VISIBLE);
            holder.friendImage.setImageResource(R.drawable.default_profile);
        }

        holder.friendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mainImageUrl = AppGlobals.SERVER_URL + friendList.getImage();
//                Toast.makeText(context, mainImageUrl, Toast.LENGTH_LONG).show();
                showImageDialog(mainImageUrl);
            }
        });

        /*if(friendList.getImage() == null || TextUtils.isEmpty(friendList.getImage())) {
            String imagePath = appGlobals.getRocketsPath(context) + "";
            HashMap<String, String> params = new HashMap<>();
            params.put("mobile", appGlobals.sharedPref.getLoginMobile());
            params.put("frndMob", friendList.getMobile());
            appGlobals.searchUpdatedImage(context, params, imagePath, holder.friendImage);
        } else {
            String imagePath = appGlobals.getRocketsPath(context) + "/" + friendList.getImage();
            File filePath = new File(imagePath);
            if (filePath.exists()) {
                holder.friendImage.setVisibility(View.VISIBLE);
                holder.friendImage.setImageURI(Uri.fromFile(filePath));
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile", appGlobals.sharedPref.getLoginMobile());
                params.put("frndMob", friendList.getMobile());
                appGlobals.searchUpdatedImage(context, params, imagePath, holder.friendImage);
            } else {
                String imageUrl = AppGlobals.SERVER_URL + friendList.getImage();
                appGlobals.loadImageFromServerWithDefault(imageUrl, holder.friendImage, imagePath,
                        true, context);
            }
        }*/

        if(pageType == AppGlobals.FRIEND_LIST) {
            holder.selectedItem.setVisibility(View.GONE);
        } else {
            holder.selectedItem.setVisibility(View.VISIBLE);
            checkBox = holder.selectedItem;
            setClickListener(friendList.getMobile(), position);
        }
    }

    private void showImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_image_view);

//        dialog.setTitle(getString(R.string.set_game_time));

        ImageView fullImage = (ImageView) dialog.findViewById(R.id.fullImage);
        appGlobals.loadImageFromServerWithDefault(imageUrl, fullImage, "", false, context);


        dialog.show();

        appGlobals.setDialogLayoutParams(dialog, context, false, true);

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
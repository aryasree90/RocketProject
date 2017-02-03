package rocket.club.com.rocketpoker.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import rocket.club.com.rocketpoker.InvitationListFragment;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.FriendsListClass;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

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
    ProgressDialog progressDialog = null;
    private View.OnClickListener clickListener = null;
    final private String TAG = "FriendsListAdapter";
    final String FRIEND_REQ_URL = AppGlobals.SERVER_URL + "frndReq.php";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName, friendNumber;
        CircleImageView friendImage;
        Button addFriend;
        CheckBox selectedItem;

        public MyViewHolder(View view) {
            super(view);
            selectedItem = (CheckBox) view.findViewById(R.id.selectedName);
            friendName = (TextView) view.findViewById(R.id.friendName);
            friendNumber = (TextView) view.findViewById(R.id.friendNumber);
            friendImage = (CircleImageView) view.findViewById(R.id.friendImage);
            addFriend = (Button) view.findViewById(R.id.add_friend);
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

        holder.addFriend.setVisibility(View.GONE);

        if(pageType == AppGlobals.FRIEND_LIST) {
            Log.d(TAG, "___________________________ " + friendList.getStatus() + " " + friendList.getMobile());
            holder.selectedItem.setVisibility(View.GONE);
            if(friendList.getStatus() == AppGlobals.SUGGESTED_FRIENDS) {
                holder.addFriend.setVisibility(View.VISIBLE);

                holder.addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String friendMob = friendList.getMobile();

                        UserDetails userDetails = new UserDetails();
                        userDetails.setMobile(friendList.getMobile());
                        userDetails.setUserName(friendList.getName());
                        userDetails.setUserImage(friendList.getImage());
                        userDetails.setStatus(-1);

                        Map<String, String> frnd_map = new HashMap<String, String>();
                        frnd_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                        frnd_map.put("frnd_mobile", friendMob);
                        frnd_map.put("task", appGlobals.NEW_FRND_REQ);
                        progressDialog = appGlobals.showDialog(context, context.getString(R.string.saving));
                        serverCall(frnd_map, FRIEND_REQ_URL, userDetails);
                    }
                });
            } else if(friendList.getStatus() == AppGlobals.PENDING_REQUEST) {
                holder.addFriend.setVisibility(View.VISIBLE);
                holder.addFriend.setEnabled(false);
            }
        } else {
            holder.selectedItem.setVisibility(View.VISIBLE);
            checkBox = holder.selectedItem;
            setClickListener(friendList.getMobile(), position);
        }
    }

    private void serverCall(final Map<String, String> map, final String URL, final UserDetails userDetails) {

        if(!appGlobals.isNetworkConnected(context)) {
            appGlobals.toastMsg(context, context.getString(R.string.no_internet), appGlobals.LENGTH_LONG);
            appGlobals.cancelDialog(progressDialog);
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(URL.equals(FRIEND_REQ_URL)) {
                            if(response.equals("Success")) {
                                ArrayList<UserDetails> list = new ArrayList<UserDetails>();
                                list.add(userDetails);
                                DBHelper db = new DBHelper(context);
                                db.insertContactDetails(list, false);
                                appGlobals.toastMsg(context, context.getString(R.string.req_sent), appGlobals.LENGTH_LONG);
                            }
                        }
                        appGlobals.cancelDialog(progressDialog);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
                        appGlobals.cancelDialog(progressDialog);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void showImageDialog(String imageUrl) {
        Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_image_view);

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
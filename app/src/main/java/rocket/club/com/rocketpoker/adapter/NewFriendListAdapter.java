package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public class NewFriendListAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    View.OnClickListener itemClickListener;
    ArrayList<ContactClass> userList = null;

    TextView headerText;
    Button accept, reject;
    AppGlobals appGlobals = null;

    private final String TAG = "New Friend List Adapter";

    public NewFriendListAdapter(Context context, ArrayList<ContactClass> list, View.OnClickListener clickListener) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        userList = list;
        itemClickListener = clickListener;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.new_friend_item, container, false);

        appGlobals = AppGlobals.getInstance(mContext);

        CircleImageView itemImage = (CircleImageView) itemView.findViewById(R.id.newFriendImageView);
        headerText = (TextView) itemView.findViewById(R.id.newFriendHeaderText);
        accept = (Button) itemView.findViewById(R.id.acceptFriend);
        reject = (Button) itemView.findViewById(R.id.rejectFriend);

        ContactClass detail = userList.get(position);
        String display = detail.getContactName() + "-" + detail.getPhoneNumber();
        headerText.setText(display);

        setClickListener(detail.getPhoneNumber());

        container.addView(itemView);
        return itemView;
    }

    private void setClickListener(final String frnd_mob) {
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverCall(frnd_mob, AppGlobals.ACCEPTED_FRIENDS);
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverCall(frnd_mob, AppGlobals.REJECT_FRIENDS);
            }
        });
    }

    private void serverCall(final String frnd_mob, final int status) {

        if(!appGlobals.isNetworkConnected(mContext)) {
            appGlobals.toastMsg(mContext, mContext.getString(R.string.no_internet), appGlobals.LENGTH_LONG);
            return;
        }

        final String FRIEND_RESP_URL = AppGlobals.SERVER_URL + "frndReq.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FRIEND_RESP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Success")) {
                            appGlobals.sqLiteDb.updateContacts(status, frnd_mob);

                            Intent autoIntent = new Intent(AppGlobals.NOTIF_FRND_REQ);
                            mContext.sendBroadcast(autoIntent);

                        } else {
                            appGlobals.toastMsg(mContext, mContext.getString(R.string.unable_to_connect_server), appGlobals.LENGTH_LONG);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                map.put("frnd_mobile", frnd_mob);
                map.put("status", Integer.toString(status));
                map.put("task", appGlobals.REPLY_FRND_REQ);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
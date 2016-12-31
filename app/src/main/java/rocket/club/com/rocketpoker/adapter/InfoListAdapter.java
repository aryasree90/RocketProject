package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.ConnectionDetector;
import rocket.club.com.rocketpoker.EventDetailActivity;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 10/12/2016.
 */
public class InfoListAdapter extends PagerAdapter {

    Context context;
    ImageButton likeImageBtn, shareImageBtn;
    TextView eventHeader = null, eventSubHeader = null;
    ImageView eventImage = null;
    LayoutInflater mLayoutInflater;
    ArrayList<InfoDetails> infoList = null;
    View.OnClickListener itemClickListener;
    ConnectionDetector connectionDetector = null;
    View.OnClickListener clickListener = null;
    final String VALIDATION_URL = AppGlobals.SERVER_URL + "likeEvent.php";

    AppGlobals appGlobals = null;
    String activityType = null;

    private final String TAG = "InfoListAdapter";

    public InfoListAdapter(Context context, ArrayList<InfoDetails> infoList,
                           View.OnClickListener clickListener, String actType) {
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.infoList = infoList;
        connectionDetector = new ConnectionDetector(context);
        appGlobals = AppGlobals.getInstance(context);
        this.clickListener = clickListener;
        this.activityType = actType;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        int layoutId = R.layout.activity_event;

        if (activityType.equals(AppGlobals.EVENT_INFO)) {
            layoutId = R.layout.activity_event;
        } else if (activityType.equals(AppGlobals.SERVICE_INFO)) {
            layoutId = R.layout.activity_service;
        }

        View itemView = mLayoutInflater.inflate(layoutId, container, false);

        LinearLayout likeShareTool = (LinearLayout) itemView.findViewById(R.id.likeShareTool);
        likeImageBtn = (ImageButton) itemView.findViewById(R.id.likeImage);
        shareImageBtn = (ImageButton) itemView.findViewById(R.id.shareImage);
        eventHeader = (TextView) itemView.findViewById(R.id.eventHeaderText);
        eventSubHeader = (TextView) itemView.findViewById(R.id.eventSummaryText);
        eventImage = (ImageView) itemView.findViewById(R.id.eventImage);
        TextView imageText = (TextView) itemView.findViewById(R.id.imageText);

        final InfoDetails infoItem = infoList.get(position);
        try {

            String imgPath = AppGlobals.SERVER_URL + infoItem.getInfoImage();
            appGlobals.loadImageFromServer(imgPath, eventImage, imageText, context);
        }catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
        }

        eventHeader.setText(infoItem.getInfoTitle());
        eventSubHeader.setText(infoItem.getInfoSubTitle());

        if (activityType.equals(AppGlobals.EVENT_INFO)) {
            likeShareTool.setVisibility(View.VISIBLE);
            if (appGlobals.sharedPref.getLikeEventList().contains(infoItem.getId())) {
                likeImageBtn.setImageResource(R.mipmap.ic_favorite);
            } else {
                likeImageBtn.setImageResource(R.mipmap.ic_favorite_border);
            }
        } else if(activityType.equals(AppGlobals.SERVICE_INFO)) {
            likeShareTool.setVisibility(View.GONE);
        }

        likeImageBtn.setOnClickListener(clickListener);
        shareImageBtn.setOnClickListener(clickListener);

        likeImageBtn.setTag(position);
        shareImageBtn.setTag(position);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    private void serverCall(final Map<String,String> params) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().contains("success")) {

                        } else {
                            appGlobals.toastMsg(context, context.getString(R.string.pls_try_later), appGlobals.LENGTH_LONG);
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
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}

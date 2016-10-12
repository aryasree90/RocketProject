package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

import rocket.club.com.rocketpoker.ConnectionDetector;
import rocket.club.com.rocketpoker.EventDetailActivity;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;

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

    AppGlobals appGlobals = null;

    private final String TAG = "InfoListAdapter";

    public InfoListAdapter(Context context, ArrayList<InfoDetails> infoList, View.OnClickListener clickListener) {
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.infoList = infoList;
        connectionDetector = new ConnectionDetector(context);
        appGlobals = AppGlobals.getInstance(context);
        this.clickListener = clickListener;
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
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.activity_event, container, false);

        likeImageBtn = (ImageButton) itemView.findViewById(R.id.likeImage);
        shareImageBtn = (ImageButton) itemView.findViewById(R.id.shareImage);
        eventHeader = (TextView) itemView.findViewById(R.id.eventHeaderText);
        eventSubHeader = (TextView) itemView.findViewById(R.id.eventSummaryText);
        eventImage = (ImageView) itemView.findViewById(R.id.eventImage);

        InfoDetails infoItem = infoList.get(position);

        eventImage.setImageResource(R.drawable.event1);

        eventHeader.setText(infoItem.getInfoTitle());
        eventSubHeader.setText(infoItem.getInfoSubTitle());

        if (infoItem.getInfoLikeStatus().equals("true")) {
            likeImageBtn.setImageResource(R.mipmap.ic_favorite);
        } else {
            likeImageBtn.setImageResource(R.mipmap.ic_favorite_border);
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
        container.removeView((RelativeLayout) object);
    }
}

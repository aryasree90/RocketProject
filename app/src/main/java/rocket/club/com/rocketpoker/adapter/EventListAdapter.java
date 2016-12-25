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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import rocket.club.com.rocketpoker.EventDetailActivity;
import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;

public class EventListAdapter extends PagerAdapter {

    Context context;
    ImageView itemImage;
    RelativeLayout itemLayout;
    AppGlobals appGlobals = null;

    LayoutInflater mLayoutInflater;
    ArrayList<InfoDetails> infoList = null;
    View.OnClickListener itemClickListener;

    private final String TAG = "EventListAdapter";

    public EventListAdapter(Context context, ArrayList<InfoDetails> infoList) {
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.infoList = infoList;
        appGlobals = AppGlobals.getInstance(context);
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.event_item, container, false);

        itemLayout = (RelativeLayout) itemView.findViewById(R.id.eventItemLayout);
        itemImage = (ImageView) itemView.findViewById(R.id.eventImageView);
        TextView headerText = (TextView) itemView.findViewById(R.id.eventHeaderText);

        InfoDetails infoItem = infoList.get(position);

        String imgPath = appGlobals.getRocketsPath(context) + "/" + infoItem.getInfoImage();
        File imgFile = new File(imgPath);
        if(imgFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(imgPath);
            itemImage.setImageBitmap(bmp);
        }
//        itemImage.setImageResource(R.drawable.event1);
        headerText.setText(infoItem.getInfoTitle());
        setItemClickListener(position, infoItem);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    private void setItemClickListener(final int pos, InfoDetails infoItem) {
        itemClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.eventItemLayout:
                    case R.id.eventImageView:
                        Intent eventActivity = new Intent(context, EventDetailActivity.class);
                        eventActivity.putExtra(EventDetailActivity.ACTIVITY_TYPE, AppGlobals.EVENT_INFO);
                        eventActivity.putExtra(EventDetailActivity.ITEM_POS, pos);
                        context.startActivity(eventActivity);
                        break;
                }
            }
        };
        itemLayout.setOnClickListener(itemClickListener);
        itemImage.setOnClickListener(itemClickListener);
    }

}
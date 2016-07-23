package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import rocket.club.com.rocketpoker.EventDetailActivity;
import rocket.club.com.rocketpoker.R;

public class EventListAdapter extends PagerAdapter {

    Context context;
    ImageView itemImage;
    RelativeLayout itemLayout;

    LayoutInflater mLayoutInflater;
    int[] mResources;
    View.OnClickListener itemClickListener;

    private final String TAG = "EventListAdapter";

    public EventListAdapter(Context context, int[] mRes) {
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResources=mRes;
    }

    @Override
    public int getCount() {
        return mResources.length;
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

        itemImage.setImageResource(mResources[position]);
        setItemClickListener(position);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    private void setItemClickListener(int pos) {
        itemClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.eventItemLayout:
                    case R.id.eventImageView:
                        Intent eventActivity = new Intent(context, EventDetailActivity.class);
                        context.startActivity(eventActivity);
                        break;
                }
            }
        };
        itemLayout.setOnClickListener(itemClickListener);
        itemImage.setOnClickListener(itemClickListener);
    }

}
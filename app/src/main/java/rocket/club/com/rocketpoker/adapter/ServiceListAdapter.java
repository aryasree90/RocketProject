package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import rocket.club.com.rocketpoker.R;

public class ServiceListAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    int[] mResources;
    View.OnClickListener itemClickListener;

    public ServiceListAdapter(Context context, int[] mRes, View.OnClickListener clickListener) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResources=mRes;
        itemClickListener = clickListener;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.service_item, container, false);

        LinearLayout itemLayout = (LinearLayout) itemView.findViewById(R.id.serviceItemLayout);
        ImageView itemImage = (ImageView) itemView.findViewById(R.id.serviceImageView);
        TextView headerText = (TextView) itemView.findViewById(R.id.serviceHeaderText);
        TextView summaryText = (TextView) itemView.findViewById(R.id.serviceSummaryText);

        itemImage.setImageResource(mResources[position]);
        itemImage.setOnClickListener(itemClickListener);
        itemImage.setTag(position);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}
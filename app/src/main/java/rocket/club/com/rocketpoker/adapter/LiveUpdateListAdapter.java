package rocket.club.com.rocketpoker.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.classes.LiveUpdateDetails;

public class LiveUpdateListAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<LiveUpdateDetails> updateList;
    View.OnClickListener itemClickListener;

    public LiveUpdateListAdapter(Context context, ArrayList<LiveUpdateDetails> list, View.OnClickListener clickListener) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        updateList=list;
        itemClickListener = clickListener;
    }

    @Override
    public int getCount() {
        return updateList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        int layout = R.layout.live_update_item;

        LiveUpdateDetails updateDetails = updateList.get(position);

        switch(updateDetails.getUpdateType()) {
            case "1":
                layout = R.layout.live_update_item;
                break;
            case "2":
                layout = R.layout.live_update_item;
                break;
            case "3":
                layout = R.layout.live_update_item;
                break;
        }

        View itemView = mLayoutInflater.inflate(layout, container, false);

        TextView msgHeader = (TextView) itemView.findViewById(R.id.updateHeader);
        TextView msgText1 = (TextView) itemView.findViewById(R.id.updateText1);
        TextView msgText2 = (TextView) itemView.findViewById(R.id.updateText2);
        TextView msgText3 = (TextView) itemView.findViewById(R.id.updateText3);
        TextView msgComments = (TextView) itemView.findViewById(R.id.updateComments);

        msgHeader.setText(updateDetails.getUpdateHeader());

        /*itemImage.setImageResource(updateList.get[position]);
        itemImage.setOnClickListener(itemClickListener);
        itemImage.setTag(position);*/

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}
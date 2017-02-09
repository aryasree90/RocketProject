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
import java.util.Arrays;
import java.util.List;

import rocket.club.com.rocketpoker.R;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.classes.LiveUpdateDetails;

public class LiveUpdateListAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<String> liveUpdateList;
    ArrayList<LiveUpdateDetails> updateList;
    View.OnClickListener itemClickListener;

    private final String TAG = "LiveUpdateListAdapter";

    public LiveUpdateListAdapter(Context context, ArrayList<LiveUpdateDetails> list, View.OnClickListener clickListener) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        updateList=list;
        itemClickListener = clickListener;

        String[] updateStr = context.getResources().getStringArray(R.array.live_update_list);
        liveUpdateList = Arrays.asList(updateStr);
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

        View itemView = layoutInflater.inflate(R.layout.live_update_item, container, false);
        LiveUpdateDetails updateDetails = updateList.get(position);

        int backgroundId = 0;
        int index= liveUpdateList.indexOf(updateDetails.getUpdateType());

        switch(index) {
            case 0:
                backgroundId = R.drawable.winner;
                break;
            case 1:
                backgroundId = R.drawable.game1;
                break;
            case 2:
                backgroundId = R.drawable.game1;
                break;
        }

        LinearLayout liveUpdateLayout = (LinearLayout) itemView.findViewById(R.id.liveUpdateLayout);
        liveUpdateLayout.setBackgroundResource(backgroundId);

        TextView msgHeader = (TextView) itemView.findViewById(R.id.updateHeader);
        TextView msgText1 = (TextView) itemView.findViewById(R.id.updateText1);
        TextView msgText2 = (TextView) itemView.findViewById(R.id.updateText2);
        TextView msgText3 = (TextView) itemView.findViewById(R.id.updateText3);
        TextView msgComments = (TextView) itemView.findViewById(R.id.updateComments);

        msgHeader.setText(updateDetails.getUpdateHeader());
        msgText1.setText(updateDetails.getUpdateText1());
        msgText2.setText(updateDetails.getUpdateText2());
        msgText3.setText(updateDetails.getUpdateText3());
        msgComments.setText(updateDetails.getUpdateComments());

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
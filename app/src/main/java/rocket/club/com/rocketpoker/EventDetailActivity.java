package rocket.club.com.rocketpoker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import rocket.club.com.rocketpoker.adapter.EventListAdapter;
import rocket.club.com.rocketpoker.adapter.InfoListAdapter;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;

public class EventDetailActivity extends AppCompatActivity {

    Context context;
    private ViewPager infoItemPager = null;
    private TextView infoEmptyText = null;
    private boolean likeStatus = false;
    AppGlobals appGlobals = null;
    View.OnClickListener clickListener = null;
    ConnectionDetector connectionDetector = null;
    ArrayList<InfoDetails> infoList = null;

    public static final String ACTIVITY_TYPE = "activityType";
    public static final String ITEM_POS = "itemPos";

    String activityType = AppGlobals.EVENT_INFO;
    int reqPos = 0;

    Toolbar toolBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_view_pager);

        initializeWidgets();
        setClickListener();
        initializeData();

    }

    private void initializeWidgets() {
        context = getApplicationContext();
        appGlobals = AppGlobals.getInstance(context);

        Bundle bundle = getIntent().getExtras();

        if (bundle.containsKey(ACTIVITY_TYPE))
            activityType = bundle.getString(ACTIVITY_TYPE);

        if (bundle.containsKey(ITEM_POS))
            reqPos = bundle.getInt(ITEM_POS);

        DBHelper db = new DBHelper(context);
        infoList = db.getRocketsInfo(activityType);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        infoItemPager = (ViewPager) findViewById(R.id.infoList);
        infoEmptyText = (TextView) findViewById(R.id.emptyInfoItem);

        toolBar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();
        if (activityType.equals(AppGlobals.EVENT_INFO))
            actionBar.setTitle(getString(R.string.event_details));
        else if (activityType.equals(AppGlobals.SERVICE_INFO))
            actionBar.setTitle(getString(R.string.service_details));
        toolBar.setNavigationIcon(R.mipmap.ic_arrow_back);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void initializeData() {

        if (!infoList.isEmpty()) {
            infoItemPager.setVisibility(View.VISIBLE);
            infoEmptyText.setVisibility(View.GONE);
            InfoListAdapter infoAdapter = new InfoListAdapter(context, infoList, clickListener);
            infoItemPager.setAdapter(infoAdapter);
        } else {
            infoItemPager.setVisibility(View.GONE);
            infoEmptyText.setVisibility(View.VISIBLE);

            if (activityType.equals(AppGlobals.EVENT_INFO)) {
                infoEmptyText.setText(getString(R.string.empty_events));
            } else if (activityType.equals(AppGlobals.SERVICE_INFO)) {
                infoEmptyText.setText(getString(R.string.empty_service));
            }
        }
    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    int pos = Integer.parseInt(v.getTag().toString());
                    switch (v.getId()) {
                        case R.id.likeImage:

                            ImageView likeImageBtn = getLikeImageAt(pos);
                            if (likeImageBtn != null) {
                                InfoDetails infoItem = infoList.get(pos);
                                if (infoItem.getInfoLikeStatus().equals("false")) {
                                    likeImageBtn.setImageResource(R.mipmap.ic_favorite);
                                    infoItem.setInfoLikeStatus("true");
                                } else {
                                    likeImageBtn.setImageResource(R.mipmap.ic_favorite_border);
                                    infoItem.setInfoLikeStatus("false");
                                }
                            }

                            break;
                        case R.id.shareImage:

                            TextView eventHeader = getShareTextAt(pos);
                            if (eventHeader != null) {
                                String headerMsg = eventHeader.getText().toString();
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, headerMsg);
                                sendIntent.setType("text/plain");
                                context.startActivity(Intent.createChooser(sendIntent,
                                        context.getResources().getText(R.string.send_to)).
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }

                            break;
                    }
                } else {
                    appGlobals.toastMsg(context, context.getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                }
            }
        };
    }

    private ImageView getLikeImageAt(int itemIndex) {
        int visiblePosition = infoItemPager.getVerticalScrollbarPosition();
        View v = infoItemPager.getChildAt(itemIndex - visiblePosition);

        ImageView progress = null;
        if (v != null) {
            progress = (ImageView) v
                    .findViewById(R.id.likeImage);
        }
        return progress;
    }

    private TextView getShareTextAt(int itemIndex) {
        int visiblePosition = infoItemPager.getVerticalScrollbarPosition();
        View v = infoItemPager.getChildAt(itemIndex - visiblePosition);

        TextView progress = null;
        if (v != null) {
            progress = (TextView) v
                    .findViewById(R.id.eventHeaderText);
        }
        return progress;
    }
}

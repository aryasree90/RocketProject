package rocket.club.com.rocketpoker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import rocket.club.com.rocketpoker.utils.AppGlobals;

public class EventDetailActivity extends AppCompatActivity {

    Context context;
    ImageButton likeImageBtn, shareImageBtn;
    TextView eventHeader = null;
    private boolean likeStatus = false;
    AppGlobals appGlobals = null;
    View.OnClickListener clickListener = null;
    ConnectionDetector connectionDetector = null;

    Toolbar toolBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        initializeWidgets();
        initializeData();
        setClickListener();
    }

    private void initializeWidgets() {
        context = getApplicationContext();
        appGlobals = AppGlobals.getInstance();

        connectionDetector = new ConnectionDetector(getApplicationContext());

        likeImageBtn = (ImageButton) findViewById(R.id.likeImage);
        shareImageBtn = (ImageButton) findViewById(R.id.shareImage);
        eventHeader = (TextView) findViewById(R.id.eventHeaderText);

        toolBar = (Toolbar)findViewById(R.id.hometoolbar);
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Event Details");
        toolBar.setNavigationIcon(R.mipmap.ic_arrow_back);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void initializeData() {
        likeStatus = false;
    }

    private void setClickListener() {

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.likeImage:
                        if(connectionDetector.isConnectingToInternet()) {
                            likeStatus = !likeStatus;
                            if (likeStatus) {
                                likeImageBtn.setImageResource(R.mipmap.ic_favorite);
                            } else {
                                likeImageBtn.setImageResource(R.mipmap.ic_favorite_border);
                            }
                        } else {
                            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                        }
                        break;
                    case R.id.shareImage:
                        if(connectionDetector.isConnectingToInternet()) {
                            String headerMsg = eventHeader.getText().toString();
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, headerMsg);
                            sendIntent.setType("text/plain");
                            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                        } else {
                            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                        }
                        break;
                }
            }
        };

        toolBar.setOnClickListener(clickListener);
        likeImageBtn.setOnClickListener(clickListener);
        shareImageBtn.setOnClickListener(clickListener);
    }
}

package rocket.club.com.rocketpoker;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import rocket.club.com.rocketpoker.utils.AppGlobals;

/**
 * Created by Admin on 7/27/2016.
 */
public class AddNewFriendActivity extends AppCompatActivity {

    Context context;
    AppGlobals appGlobals = null;
    View.OnClickListener clickListener = null;
    ConnectionDetector connectionDetector = null;

    Toolbar toolBar = null;
    Button accept, reject, search;
    LinearLayout showFriendsDetailsLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friend);

        initializeWidgets();
        setClickListener();
    }

    private void initializeWidgets() {
        context = getApplicationContext();
        appGlobals = AppGlobals.getInstance();
        connectionDetector = new ConnectionDetector(getApplicationContext());

        showFriendsDetailsLayout = (LinearLayout)findViewById(R.id.show_friend_details);
        accept = (Button)findViewById(R.id.acceptFriend);
        reject = (Button)findViewById(R.id.rejectFriend);
        search = (Button)findViewById(R.id.searchBtn);

        toolBar = (Toolbar)findViewById(R.id.hometoolbar);
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Search Friends");
        toolBar.setNavigationIcon(R.mipmap.ic_arrow_back);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setClickListener() {

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.searchBtn:
                        showFriendsDetailsLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.acceptFriend:
                        Toast.makeText(context, "Accept Friend", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.rejectFriend:
                        Toast.makeText(context, "Reject Friend", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        toolBar.setOnClickListener(clickListener);
        search.setOnClickListener(clickListener);
        accept.setOnClickListener(clickListener);
        reject.setOnClickListener(clickListener);
    }
}

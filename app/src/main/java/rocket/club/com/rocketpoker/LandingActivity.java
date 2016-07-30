package rocket.club.com.rocketpoker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import de.hdodenhof.circleimageview.CircleImageView;
import rocket.club.com.rocketpoker.utils.AppGlobals;

public class LandingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context = null;
    AppGlobals appGlobals= null;

    private static final String TAG = "LandingActivity";
    View.OnClickListener clickListener = null;

    Toolbar toolbar;
    ImageButton imgChatRoom, imgAddFriend, imgRocket;
    FloatingActionButton actionButton;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    CircleImageView circularImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        initializeWidgets();
        setClickListener();
    }

    private void initializeWidgets() {
        context = getApplicationContext();
        appGlobals = AppGlobals.getInstance(context);

        imgRocket = (ImageButton)findViewById(R.id.rocket_img);
        imgChatRoom = (ImageButton)findViewById(R.id.chat_room_img);
        imgAddFriend = (ImageButton)findViewById(R.id.invite_to_play);

        actionButton = (FloatingActionButton) findViewById(R.id.fab);
        actionButton.setVisibility(View.GONE);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        View headerView = getLayoutInflater().inflate(R.layout.nav_header_landing, navigationView, false);
        navigationView.addHeaderView(headerView);

        circularImageView = (CircleImageView) headerView.findViewById(R.id.profileImage);

        if(appGlobals.currentFragmentClass == null)
            appGlobals.currentFragmentClass = HomeFragment.class;

        setFragment(appGlobals.currentFragmentClass);
    }

    private void setClickListener() {

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.chat_room_img:
                        Toast.makeText(context, "Chat Room Coming Soon", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.invite_to_play:
                        Toast.makeText(context, "Invite Your Friends to club by one click", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.rocket_img:
                        Class fragmentClass = AboutFragment.class;
                        setFragment(fragmentClass);
                        break;
                    case R.id.profileImage:
                        Intent profileIntent = new Intent(context, ProfileActivity.class);
                        startActivity(profileIntent);
                        finish();
                        break;
                    case R.id.fab:
                        Toast.makeText(context, "Coming soon...", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        imgRocket.setOnClickListener(clickListener);
        imgChatRoom.setOnClickListener(clickListener);
        imgAddFriend.setOnClickListener(clickListener);
        circularImageView.setOnClickListener(clickListener);
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                if(appGlobals.currentFragmentClass == HomeFragment.class) {
                    super.onBackPressed();
                    //additional code
                } else {
                    setFragment(HomeFragment.class);
                }
            } else {
                getFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_sign_out:
                Toast.makeText(LandingActivity.this, "Sign out", Toast.LENGTH_LONG).show();
                appGlobals.sharedPref.setLogInStatus(false);
                appGlobals.sharedPref.setLoginMobile("");
                appGlobals.sharedPref.setRegId("");
                GCMRegistrar.setRegisteredOnServer(context, false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        selectDrawerItem(menuItem);
        return true;
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Class fragmentClass = null;

        switch(menuItem.getItemId()) {
            case R.id.my_home:
                Toast.makeText(context, "My Home", Toast.LENGTH_LONG).show();
                fragmentClass = HomeFragment.class;
                break;
            case R.id.my_friends:
                Toast.makeText(context, "My Friends", Toast.LENGTH_LONG).show();
                fragmentClass = FriendsFragment.class;
                break;
            case R.id.about_us:
                fragmentClass = AboutFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        if(fragmentClass != null) {
            setFragment(fragmentClass);
            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
        }
        // Close the navigation drawer
        drawer.closeDrawers();
    }

    private void setFragment(Class setClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) setClass.newInstance();
            appGlobals.currentFragmentClass = setClass;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }
}

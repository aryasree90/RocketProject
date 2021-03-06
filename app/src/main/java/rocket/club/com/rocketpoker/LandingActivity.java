package rocket.club.com.rocketpoker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import rocket.club.com.rocketpoker.async.ContactAsync;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public class LandingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context = null;
    AppGlobals appGlobals= null;

    private static final String TAG = "LandingActivity";
    View.OnClickListener clickListener = null;

    Toolbar toolbar;
    ImageButton imgChatRoom, imgAddFriend, imgRocket, refreshContact;
    TextView chatCount, inviteCount;
    FloatingActionButton actionButton;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    CircleImageView circularImageView;
    TextView textName, textNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        initializeWidgets();
        setClickListener();
        checkContactSync();
    }

    private void initializeWidgets() {
        context = getApplicationContext();
        appGlobals = AppGlobals.getInstance(context);

        imgRocket = (ImageButton)findViewById(R.id.rocket_img);
        imgChatRoom = (ImageButton)findViewById(R.id.chat_room_img);
        imgAddFriend = (ImageButton)findViewById(R.id.invite_to_play);

        chatCount = (TextView)findViewById(R.id.chatCount);
        inviteCount = (TextView)findViewById(R.id.inviteCount);
        refreshContact = (ImageButton)findViewById(R.id.refresh);

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

//        setMenuVisibility();
        setItemVisibility();

        View headerView = getLayoutInflater().inflate(R.layout.nav_header_landing, navigationView, false);
        navigationView.addHeaderView(headerView);

        circularImageView = (CircleImageView) headerView.findViewById(R.id.profileImage);
        textName = (TextView) headerView.findViewById(R.id.textName);
        textNum = (TextView) headerView.findViewById(R.id.textNum);

        textName.setText(appGlobals.sharedPref.getUserName());
        textNum.setText(appGlobals.sharedPref.getLoginMobile());

        String imgFileName = appGlobals.sharedPref.getLoginMobile() + ".jpg";
        String imgPath = appGlobals.getRocketsPath(context) + "/" + imgFileName;
        File imageFile = new File(imgPath);

        if(!imageFile.exists()) {

            HashMap<String, String> params = new HashMap<>();
            params.put("mobile", appGlobals.sharedPref.getLoginMobile());
            params.put("frndMob", appGlobals.sharedPref.getLoginMobile());

            appGlobals.searchUpdatedImage(context, params, imgPath, circularImageView);
        }

        if(imageFile.exists())
            circularImageView.setImageURI(Uri.fromFile(imageFile));

        if(appGlobals.currentFragmentClass == null)
            appGlobals.currentFragmentClass = HomeFragment.class;

        setFragment(appGlobals.currentFragmentClass, null);

        AppGlobals.tempActivity = this;

        if(!appGlobals.checkLocationPermission(context, AppGlobals.ACCESS_COARSE_LOC)
                || !appGlobals.checkLocationPermission(context, AppGlobals.ACCESS_FINE_LOC)) {
            ActivityCompat.requestPermissions(LandingActivity.this,
                    new String[]{AppGlobals.ACCESS_COARSE_LOC, AppGlobals.ACCESS_FINE_LOC},
                    AppGlobals.REQUEST_CODE_LOCATION);
        } else {
            appGlobals.startLocationIntent(context);
        }

        appGlobals.sharedPref.setInitLoad(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case AppGlobals.REQUEST_CODE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    appGlobals.startLocationIntent(context);
                }
                return;
            }
        }
    }

    private void setClickListener() {

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.chat_room_img:
                        Intent chatRoomIntent = new Intent(context, ChatRoomActivity.class);
                        startActivity(chatRoomIntent);
                        break;
                    case R.id.invite_to_play:
                        Class fragmentInvite = InvitationListFragment.class;
                        setFragment(fragmentInvite, null);
                        break;
                    case R.id.rocket_img:
                        Class fragmentAbout = AboutFragment.class;
                        setFragment(fragmentAbout, null);
                        break;
                    case R.id.profileImage:
                    case R.id.textName:
                    case R.id.textNum:
                        Intent profileIntent = new Intent(context, ProfileActivity.class);
                        startActivity(profileIntent);
                        finish();
                        break;
                    case R.id.refresh:
                        if(!appGlobals.contactSyncInProgress) {
                            appGlobals.toastMsg(context, getString(R.string.contact_sync), appGlobals.LENGTH_LONG);
                            ContactAsync contactAsync = new ContactAsync(context);
                            contactAsync.execute();
                        }
                        break;
                }
            }
        };

        refreshContact.setOnClickListener(clickListener);
        imgRocket.setOnClickListener(clickListener);
        imgChatRoom.setOnClickListener(clickListener);
        imgAddFriend.setOnClickListener(clickListener);
        circularImageView.setOnClickListener(clickListener);
        textName.setOnClickListener(clickListener);
        textNum.setOnClickListener(clickListener);
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
                    setFragment(HomeFragment.class, null);
                }
            } else {
                getFragmentManager().popBackStack();
            }
        }
    }

   /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing, menu);
        return true;
    }  */

    private void setItemVisibility() {

        int msgType = appGlobals.sharedPref.getUserType();

        navigationView.getMenu().findItem(R.id.admin).setVisible(false);
        navigationView.getMenu().findItem(R.id.cashier).setVisible(false);
        navigationView.getMenu().findItem(R.id.editor).setVisible(false);

        if(msgType == AppGlobals.EDITOR) {
            navigationView.getMenu().findItem(R.id.editor).setVisible(true);
        } else if(msgType == AppGlobals.CASHIER) {
            navigationView.getMenu().findItem(R.id.cashier).setVisible(true);
        } else if(msgType == AppGlobals.ADMIN) {
            navigationView.getMenu().findItem(R.id.admin).setVisible(true);
        } else if(msgType == AppGlobals.SUPER_ADMIN) {
            navigationView.getMenu().findItem(R.id.admin).setVisible(true);
            navigationView.getMenu().findItem(R.id.cashier).setVisible(true);
            navigationView.getMenu().findItem(R.id.editor).setVisible(true);
        }

    }

    private void setMenuVisibility() {

        int msgType = appGlobals.sharedPref.getUserType();

        navigationView.getMenu().setGroupVisible(R.id.editor, false);
        navigationView.getMenu().setGroupVisible(R.id.cashier, false);
        navigationView.getMenu().setGroupVisible(R.id.admin, false);

        if(msgType == AppGlobals.EDITOR) {
            navigationView.getMenu().setGroupVisible(R.id.editor, true);
        } else if(msgType == AppGlobals.CASHIER) {
            navigationView.getMenu().setGroupVisible(R.id.cashier, true);
        } else if(msgType == AppGlobals.ADMIN) {
            navigationView.getMenu().setGroupVisible(R.id.editor, true);
            navigationView.getMenu().setGroupVisible(R.id.cashier, true);
            navigationView.getMenu().setGroupVisible(R.id.admin, true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_sign_out:
                appGlobals.toastMsg(context, getString(R.string.sign_out), appGlobals.LENGTH_LONG);
                appGlobals.sharedPref.setLogInStatus(false);
                appGlobals.sharedPref.setLoginMobile("");
                appGlobals.sharedPref.setRegId("");
                GCMRegistrar.setRegisteredOnServer(context, false);

                Intent loginActivity = new Intent(context, LoginActivity.class);
                loginActivity.putExtra(LoginActivity.pageType, LoginActivity.loginPage);
                startActivity(loginActivity);
                this.finish();
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
        Bundle args = null;

        switch(menuItem.getItemId()) {
            case R.id.my_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.my_friends:
                fragmentClass = FriendsFragment.class;
                args = new Bundle();
                args.putInt("type", AppGlobals.FRIEND_LIST);
                break;
            case R.id.location:
                fragmentClass = LocationFragment.class;
                break;
            case R.id.about_us:
                fragmentClass = AboutFragment.class;
                break;
            case R.id.services:
                fragmentClass = null;
                Intent fragmentActivity = new Intent(context, EventDetailActivity.class);
                fragmentActivity.putExtra(EventDetailActivity.ACTIVITY_TYPE, AppGlobals.SERVICE_INFO);
                fragmentActivity.putExtra(EventDetailActivity.ITEM_POS, 0);
                fragmentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fragmentActivity);
                break;
            case R.id.settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.contact_us:
                fragmentClass = ContactFragment.class;
                break;
            case R.id.edit_events:
                fragmentClass = AddEventFragment.class;
                args = new Bundle();
                args.putString(EventDetailActivity.ACTIVITY_TYPE, AppGlobals.EVENT_INFO);
                break;
            case R.id.edit_services:
                fragmentClass = AddEventFragment.class;
                args = new Bundle();
                args.putString(EventDetailActivity.ACTIVITY_TYPE, AppGlobals.SERVICE_INFO);
                break;
            case R.id.edit_live_update:
                fragmentClass = AddLiveUpdateFragment.class;
                break;
            case R.id.edit_game_type:
                fragmentClass = AddGameTypeFragment.class;
                break;
            case R.id.role_assign:
                fragmentClass = AssignRoleFragment.class;
                break;
            case R.id.id_assign:
                fragmentClass = AssignIdFragment.class;
                break;
            case R.id.user_transactions:
                fragmentClass = UserTransFragment.class;
                break;
            case R.id.salary_transactions:
                fragmentClass = SalaryTransFragment.class;
                break;
            case R.id.exp_transactions:
                fragmentClass = ExpenseTransFragment.class;
                break;
            case  R.id.transactions:
                /*fragmentClass = TransactionFragment.class;
                args = new Bundle();
                args.putString(TransactionFragment.MOB_TRANS, appGlobals.sharedPref.getLoginMobile());*/

                Intent filterTrans = new Intent(context, FilterTransaction.class);
                startActivity(filterTrans);

                /*String searchId =  appGlobals.sharedPref.getRocketId();

                Intent filterTrans = new Intent(context, FilterTransaction.class);
                filterTrans.putExtra(FilterTransaction.FILTER_TRANS, FilterTransaction.USER);
                filterTrans.putExtra(FilterTransaction.USER_ID, searchId);
                filterTrans.putExtra(FilterTransaction.USER_TYPE, true);
                startActivity(filterTrans);*/

                break;
            case R.id.add_exp_type:
                fragmentClass = AddExpenseType.class;
                break;
            case R.id.add_emp_sal:
                fragmentClass = AddEmpSalary.class;
                break;
            case R.id.salary_list:
                fragmentClass = ClubDetails.class;
                args = new Bundle();
                args.putInt(ClubDetails.ID, ClubDetails.SAL_ID);
                break;
            case R.id.exp_list:
                fragmentClass = ClubDetails.class;
                args = new Bundle();
                args.putInt(ClubDetails.ID, ClubDetails.EXP_ID);
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        if(fragmentClass != null) {
            setFragment(fragmentClass, args);
            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
        }
        // Close the navigation drawer
        drawer.closeDrawers();
    }

    private void setFragment(Class setClass, Bundle args) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) setClass.newInstance();
            if(args != null)
                fragment.setArguments(args);
            appGlobals.currentFragmentClass = setClass;
        } catch (Exception e) {
            appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setCount();
    }

    private void setCount() {

        int chatCountVal = appGlobals.sharedPref.getChatCount();
        int inviteCountVal = appGlobals.sharedPref.getInviteCount();

        if(chatCountVal > 0) {
            chatCount.setText("" + chatCountVal);
            chatCount.setVisibility(View.VISIBLE);
        } else {
            chatCount.setVisibility(View.GONE);
        }

        if(inviteCountVal > 0) {
            inviteCount.setText("" + inviteCountVal);
            inviteCount.setVisibility(View.VISIBLE);
        } else {
            inviteCount.setVisibility(View.GONE);
        }
    }

    // To auto refresh contacts every 24 hours
    private void checkContactSync() {
        long curTime = System.currentTimeMillis();
        long lastUpdated = appGlobals.sharedPref.getContactSyncTime();
        long diffInHrs = ((curTime - lastUpdated)/1000)/3600;

        if(diffInHrs >= 24 && !appGlobals.contactSyncInProgress) {
            appGlobals.logClass.setLogMsg(TAG, "Reached more than 24 hours contact sync", LogClass.DEBUG_MSG);
            ContactAsync contactAsync = new ContactAsync(context);
            contactAsync.execute();

            removeOldDataFromDB();
        }
    }

    //To remove old data from db every specific time
    private void removeOldDataFromDB() {
        appGlobals.logClass.setLogMsg(TAG, "removeOldDataFromDB", LogClass.DEBUG_MSG);
        appGlobals.sqLiteDb.removeOldData();
    }
}

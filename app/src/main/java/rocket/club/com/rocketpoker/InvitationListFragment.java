package rocket.club.com.rocketpoker;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;

import rocket.club.com.rocketpoker.adapter.GameInviteAdapter;
import rocket.club.com.rocketpoker.classes.GameInvite;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Administrator on 9/19/2016.
 */
public class InvitationListFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;
    RecyclerView inviteList = null;
    FloatingActionButton inviteToPlay;
    RadioButton allBtn, recBtn, sentBtn;
    View.OnClickListener clickListener = null;

    GameInviteAdapter inviteAdapter = null;

    private final String TAG = "InvitationListFragment";

    private ArrayList<GameInvite> showInviteList = new ArrayList<>();
    private ArrayList<GameInvite> fullInviteList = new ArrayList<>();
    private ArrayList<GameInvite> recInviteList = new ArrayList<>();
    private ArrayList<GameInvite> sentInviteList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_invitationlist, container, false);

        initializeWidgets(view);
        setClickListener();

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        inviteList = (RecyclerView) view.findViewById(R.id.inviteList);
        inviteToPlay = (FloatingActionButton) view.findViewById(R.id.btn_invitetoplay);

        allBtn = (RadioButton) view.findViewById(R.id.radioAll);
        recBtn = (RadioButton) view.findViewById(R.id.radioReceived);
        sentBtn = (RadioButton) view.findViewById(R.id.radioSent);

        fullInviteList = appGlobals.sqLiteDb.getInvitations();

        setListAdapter();

        appGlobals.sharedPref.setInviteCount(0);
    }

    private void setListAdapter() {

        try {
            fullInviteList = appGlobals.sqLiteDb.getInvitations();

            showInviteList.addAll(fullInviteList);
        } catch(Exception e) {
            appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
        }

        inviteAdapter = new GameInviteAdapter(showInviteList, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        inviteList.setLayoutManager(mLayoutManager);
        inviteList.setItemAnimator(new DefaultItemAnimator());
        inviteList.setAdapter(inviteAdapter);

        String loginNum = appGlobals.sharedPref.getLoginMobile();
        for(GameInvite eachItem : fullInviteList) {
            if(eachItem.getSenderMob().equals(loginNum)) {
                sentInviteList.add(eachItem);
            } else {
                recInviteList.add(eachItem);
            }
        }
    }

    private void setClickListener() {

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.btn_invitetoplay:
                        Class fragmentInvite = FriendsFragment.class;
                        Bundle args = new Bundle();
                        args.putInt("type", AppGlobals.INVITE_TO_CLUB);

                        try {
                            Fragment fragment = (Fragment) fragmentInvite.newInstance();
                            fragment.setArguments(args);
                            appGlobals.currentFragmentClass = fragmentInvite;

                            // Insert the fragment by replacing any existing fragment
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        } catch (Exception e) {
                            appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
                        }
                        break;
                    case R.id.radioAll:
                        setList(fullInviteList);
                        break;
                    case R.id.radioReceived:
                        setList(recInviteList);
                        break;
                    case R.id.radioSent:
                        setList(sentInviteList);
                        break;
                }
            }
        };
        inviteToPlay.setOnClickListener(clickListener);
        allBtn.setOnClickListener(clickListener);
        recBtn.setOnClickListener(clickListener);
        sentBtn.setOnClickListener(clickListener);
    }

    private void setList(ArrayList<GameInvite> showList) {

        showInviteList.clear();
        showInviteList.addAll(showList);

        inviteAdapter.notifyDataSetChanged();
    }
}
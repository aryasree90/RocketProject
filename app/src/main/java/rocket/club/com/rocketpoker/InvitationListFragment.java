package rocket.club.com.rocketpoker;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Administrator on 9/19/2016.
 */
public class InvitationListFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;
    FloatingActionButton inviteToPlay;
    View.OnClickListener clickListener = null;

    private final String TAG = "InvitationListFragment";

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
        inviteToPlay = (FloatingActionButton) view.findViewById(R.id.btn_invitetoplay);
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
                }
            }
        };
        inviteToPlay.setOnClickListener(clickListener);
    }
}
package rocket.club.com.rocketpoker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gcm.GCMRegistrar;

import rocket.club.com.rocketpoker.utils.AppGlobals;


public class SettingsFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;
    TextView dialogMsg;
    Button okButton,yesbtn,nobtn;
    ToggleButton notifToggle = null, chatToggle = null;
    View.OnClickListener onClickListener = null;
    Dialog dialog = null;
    LinearLayout signout_layout,profile_layout;
    boolean commonNotif, chatNotif;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initializeWidgets(view);
        setOnClickListener();

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);

        notifToggle = (ToggleButton) view.findViewById(R.id.notifToggle);
        chatToggle = (ToggleButton) view.findViewById(R.id.chatToggle);
        profile_layout=(LinearLayout) view.findViewById(R.id.profileLayout);
        signout_layout=(LinearLayout) view.findViewById(R.id.signoutLayout);
        yesbtn=(Button)view.findViewById(R.id.signoutYes);
        nobtn=(Button)view.findViewById(R.id.signoutNo);

        setNotif(1);
        setNotif(2);
    }

    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.notifToggle:
                        appGlobals.sharedPref.setCommonNotif(!commonNotif);
                        setNotif(1);
                        createDialog(getString(R.string.common_notif_msg));
                        break;
                    case R.id.chatToggle:
                        appGlobals.sharedPref.setChatNotif(!chatNotif);
                        setNotif(2);
                        createDialog(getString(R.string.chat_notif_msg));
                        break;
                    case R.id.signoutLayout:
                        createSignOutDialog();
                        break;
                    case R.id.okDialog:
                        dialog.cancel();
                        break;
                    case R.id.signoutYes:
                        dialog.dismiss();
                        redirectToLogin();
                        break;
                    case R.id.signoutNo:
                        dialog.cancel();
                        break;
                    case R.id.profileLayout:
                        Intent intent=new Intent(context,ProfileActivity.class);
                        startActivity(intent);
                        break;

                }
            }
        };

        notifToggle.setOnClickListener(onClickListener);
        chatToggle.setOnClickListener(onClickListener);
        profile_layout.setOnClickListener(onClickListener);
        signout_layout.setOnClickListener(onClickListener);
    }

    private void redirectToLogin() {
        appGlobals.sharedPref.setLogInStatus(false);
        appGlobals.sharedPref.setLoginMobile("");
        appGlobals.sharedPref.setRegId("");
        appGlobals.currentFragmentClass = null;
        GCMRegistrar.setRegisteredOnServer(context, false);

        Intent loginActivity = new Intent(context, LoginActivity.class);
        loginActivity.putExtra(LoginActivity.pageType, LoginActivity.loginPage);
        startActivity(loginActivity);
//        getActivity().getFragmentManager().beginTransaction().remove(SettingsFragment.class).commit();
        getActivity().finish();
    }

    private void setNotif(int setNotif) {

        if(setNotif == 1) {
            commonNotif = appGlobals.sharedPref.getCommonNotif();
            notifToggle.setChecked(commonNotif);
        } else {
            chatNotif = appGlobals.sharedPref.getChatNotif();
            chatToggle.setChecked(chatNotif);
        }
    }

    private void createDialog(String msg) {
        dialog=new Dialog(context);
        dialog.setContentView(R.layout.default_dialog);

        dialog.setTitle(getString(R.string.settings));

        dialogMsg = (TextView) dialog.findViewById(R.id.txt_msg);
        dialogMsg.setText(msg);
        okButton = (Button) dialog.findViewById(R.id.okDialog);
        okButton.setOnClickListener(onClickListener);

        dialog.show();
    }

    private void createSignOutDialog() {
        dialog=new Dialog(context);
        dialog.setContentView(R.layout.signout_dialogue);

        dialog.setTitle(getString(R.string.signout_heading));

        yesbtn = (Button) dialog.findViewById(R.id.signoutYes);
        yesbtn.setOnClickListener(onClickListener);

        nobtn = (Button) dialog.findViewById(R.id.signoutNo);
        nobtn.setOnClickListener(onClickListener);

        dialog.show();
    }

}

package rocket.club.com.rocketpoker;

import android.app.Dialog;
import android.content.Context;
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

import rocket.club.com.rocketpoker.utils.AppGlobals;


public class SettingsFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;
    TextView dialogMsg;
    Button okButton;
    ToggleButton notifToggle = null, chatToggle = null;
    View.OnClickListener onClickListener = null;
    Dialog dialog = null;
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
                    case R.id.okDialog:
                        dialog.cancel();
                        break;
                }
            }
        };

        notifToggle.setOnClickListener(onClickListener);
        chatToggle.setOnClickListener(onClickListener);
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

}

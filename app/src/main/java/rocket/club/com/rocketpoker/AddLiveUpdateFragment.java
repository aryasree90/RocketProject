package rocket.club.com.rocketpoker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.adapter.InfoListAdapter;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.classes.LiveUpdateDetails;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public class AddLiveUpdateFragment extends Fragment {

    Context context;
    AppGlobals appGlobals = null;
    View.OnClickListener clickListener = null;
    ConnectionDetector connectionDetector = null;
    private static final String TAG = "AddLiveUpdateFragment";

    MaterialBetterSpinner updateTypeSpinner = null;
    ArrayAdapter<String> updateTypeAdapter = null;
    EditText header, text1, text2, text3, comments;
    Button save, clear;
    ProgressDialog progressDialog = null;

    final String VALIDATION_URL = AppGlobals.SERVER_URL + AppGlobals.EDITORS_URL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_live_update, container, false);

        initializeWidgets(view);
        setClickListener();

        return view;
    }

    private void initializeWidgets(View view) {

        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        String[] UPDATE_LIST = getResources().getStringArray(R.array.live_update_list);

        updateTypeAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, UPDATE_LIST);
        updateTypeSpinner = (MaterialBetterSpinner) view.findViewById(R.id.updateType);
        updateTypeSpinner.setAdapter(updateTypeAdapter);

        header = (EditText) view.findViewById(R.id.updateHeader);
        text1 = (EditText) view.findViewById(R.id.updateText1);
        text2 = (EditText) view.findViewById(R.id.updateText2);
        text3 = (EditText) view.findViewById(R.id.updateText3);
        comments = (EditText) view.findViewById(R.id.updateComments);

        save = (Button) view.findViewById(R.id.saveBtn);
        clear = (Button) view.findViewById(R.id.clearBtn);

    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.saveBtn:

                        String updateType = updateTypeSpinner.getText().toString();
                        String updateHeader = header.getText().toString();
                        String updateText1 = text1.getText().toString();
                        String updateText2 = text2.getText().toString();
                        String updateText3 = text3.getText().toString();
                        String updateComments = comments.getText().toString();

                        if(TextUtils.isEmpty(updateType)) {
                            appGlobals.toastMsg(context, "Select a type", appGlobals.LENGTH_LONG);
                            return;
                        } else if(TextUtils.isEmpty(updateHeader)) {
                            appGlobals.toastMsg(context, "Enter Header", appGlobals.LENGTH_LONG);
                            return;
                        }

                        String curTime = "" + System.currentTimeMillis();

                        LiveUpdateDetails updateDetails = new LiveUpdateDetails(updateType,
                                updateHeader, updateText1, updateText2, updateText3,
                                updateComments, curTime);

                        Map<String,String> map = new HashMap<String,String>();
                        map.put("type", updateType);
                        map.put("header", updateHeader);
                        map.put("text1", updateText1);
                        map.put("text2", updateText2);
                        map.put("text3", updateText3);
                        map.put("comments", updateComments);
                        map.put("timeStamp", curTime);
                        map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                        map.put("task", AppGlobals.LIVE_UPDATE_INFO);

                        progressDialog = appGlobals.showDialog(context, getString(R.string.save_live_update));

                        serverCall(map);

                        break;
                    case R.id.clearBtn:
                        clearFields();
                        break;
                }
            }
        };
        save.setOnClickListener(clickListener);
        clear.setOnClickListener(clickListener);
    }

    private void clearFields() {
        updateTypeSpinner.setText("");
        header.setText("");
        text1.setText("");
        text2.setText("");
        text3.setText("");
        comments.setText("");
    }

    private void serverCall(final Map<String,String> params) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Received " + response, LogClass.INFO_MSG);
                        CommonUtilities.getRocketsLiveUpdate(context, response);
                        clearFields();
                        appGlobals.cancelDialog(progressDialog);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
                        appGlobals.cancelDialog(progressDialog);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}

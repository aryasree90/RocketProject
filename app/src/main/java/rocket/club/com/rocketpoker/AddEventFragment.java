package rocket.club.com.rocketpoker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gcm.GCMRegistrar;

import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 11/23/2016.
 */
public class AddEventFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;
    View.OnClickListener onClickListener = null;

    String activityType = "";
    final String TAG = "AddEventFragment";
    ConnectionDetector connectionDetector = null;
    final String VALIDATION_URL = AppGlobals.SERVER_URL + AppGlobals.EDITORS_URL;

    EditText headerText, summaryText;
    ImageView eventImage;
    Button save, clear;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_event_fragment, container, false);

        initializeWidgets(view);
        setOnClickListener();

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        Bundle bundle = getArguments();
        activityType = bundle.getString(EventDetailActivity.ACTIVITY_TYPE);

        eventImage = (ImageView) view.findViewById(R.id.eventImage);
        headerText = (EditText) view.findViewById(R.id.headerText);
        summaryText = (EditText) view.findViewById(R.id.summaryText);

        TextView actTypeText = (TextView) view.findViewById(R.id.actType);

        if (activityType.equals(AppGlobals.EVENT_INFO)) {
            headerText.setHint(getString(R.string.eventHeader));
            summaryText.setHint(getString(R.string.eventSummary));
            actTypeText.setText(getString(R.string.addEvent));
        } else {
            headerText.setHint(getString(R.string.serviceHeader));
            summaryText.setHint(getString(R.string.serviceSummary));
            actTypeText.setText(getString(R.string.addService));
        }

        save = (Button) view.findViewById(R.id.saveBtn);
        clear = (Button) view.findViewById(R.id.clearBtn);
    }

    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.saveBtn:
                        if(connectionDetector.isConnectingToInternet()) {

                            String header = headerText.getText().toString();
                            String summary = summaryText.getText().toString();

                            if(TextUtils.isEmpty(header) || TextUtils.isEmpty(summary)) {
                                appGlobals.toastMsg(context, getString(R.string.enter_all), appGlobals.LENGTH_LONG);
                            }

                            String timeStamp = String.valueOf(System.currentTimeMillis());

                            Map<String,String> map = new HashMap<String,String>();
                            map.put("header", header);
                            map.put("summary", summary);
                            map.put("timeStamp", timeStamp);
                            map.put("task", activityType);
                            map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                            serverCall(map);
                        } else {
                            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                        }
                        break;
                    case R.id.clearBtn:
                        clearFields();
                        break;
                }
            }
        };
        save.setOnClickListener(onClickListener);
        clear.setOnClickListener(onClickListener);
    }

    private void clearFields() {
        headerText.setText("");
        summaryText.setText("");
    }

    private void serverCall(final Map<String,String> params) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Received " + response, LogClass.INFO_MSG);
                        CommonUtilities.getRocketsInfo(context, response);
                        clearFields();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
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

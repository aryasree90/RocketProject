package rocket.club.com.rocketpoker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

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

import rocket.club.com.rocketpoker.classes.LiveUpdateDetails;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 11/28/2016.
 */
public class AddGameTypeFragment extends Fragment {

    Context context;
    AppGlobals appGlobals = null;
    View.OnClickListener clickListener = null;
    ConnectionDetector connectionDetector = null;
    private static final String TAG = "AddGameTypeFragment";

    EditText gameType;
    Button save, clear;

    final String VALIDATION_URL = AppGlobals.SERVER_URL + AppGlobals.EDITORS_URL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_game, container, false);

        initializeWidgets(view);
        setClickListener();

        return view;
    }

    private void initializeWidgets(View view) {

        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        gameType = (EditText) view.findViewById(R.id.gameName);

        save = (Button) view.findViewById(R.id.saveBtn);
        clear = (Button) view.findViewById(R.id.clearBtn);

    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.saveBtn:

                        String game = gameType.getText().toString();

                        if(TextUtils.isEmpty(game)) {
                            appGlobals.toastMsg(context, "Enter a Game", appGlobals.LENGTH_LONG);
                            return;
                        }

                        String curTime = "" + System.currentTimeMillis();

                        Map<String,String> map = new HashMap<String,String>();
                        map.put("header", game);
                        map.put("timeStamp", curTime);
                        map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                        map.put("task", AppGlobals.NEW_GAME);

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
        gameType.setText("");
    }

    private void serverCall(final Map<String,String> params) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Received " + response, LogClass.INFO_MSG);
                        CommonUtilities.getRocketsNewGame(context, response);
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
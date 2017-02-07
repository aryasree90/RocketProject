package rocket.club.com.rocketpoker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public class AddLiveUpdateFragment extends Fragment {

    Context context;
    AppGlobals appGlobals = null;
    View.OnClickListener clickListener = null;
    ConnectionDetector connectionDetector = null;
    private static final String TAG = "AddLiveUpdateFragment";

    LinearLayout updtDetLayout = null;
    MaterialBetterSpinner updateTypeSpinner = null, gameTypeSpinner = null;
    ArrayAdapter<String> updateTypeAdapter = null, gameTypeAdapter = null;
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

    private void loadGameNameSpinner() {
        String[] GAME_LIST = appGlobals.sqLiteDb.getRocketsGameList();

        gameTypeAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, GAME_LIST);

        gameTypeSpinner.setAdapter(gameTypeAdapter);

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

        updtDetLayout = (LinearLayout) view.findViewById(R.id.updt_det_layout);

        gameTypeSpinner = (MaterialBetterSpinner) view.findViewById(R.id.gameType);
        loadGameNameSpinner();
        gameTypeSpinner.setVisibility(View.GONE);

        header = (EditText) view.findViewById(R.id.updateHeader);
        text1 = (EditText) view.findViewById(R.id.updateText1);
        text2 = (EditText) view.findViewById(R.id.updateText2);
        text3 = (EditText) view.findViewById(R.id.updateText3);
        comments = (EditText) view.findViewById(R.id.updateComments);

        save = (Button) view.findViewById(R.id.saveBtn);
        clear = (Button) view.findViewById(R.id.clearBtn);

        updateTypeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String headerHint = "", text1Hint = "", text2Hint = "";

                header.setVisibility(View.VISIBLE);
                text1.setVisibility(View.VISIBLE);
                gameTypeSpinner.setVisibility(View.GONE);
                updtDetLayout.setVisibility(View.VISIBLE);

                if(position == 0) {         //Winner

                    header.setHint(getString(R.string.winner_name));
                    gameTypeSpinner.setHint(getString(R.string.game_type));
                    text2.setHint(getString(R.string.table_no));

                    text1.setVisibility(View.GONE);
                    gameTypeSpinner.setVisibility(View.VISIBLE);

                } else if(position == 1) {  //Current running game

                    gameTypeSpinner.setHint(getString(R.string.game_type));
                    text1.setHint(getString(R.string.table_no));
                    text2.setHint(getString(R.string.update_text));

                    header.setVisibility(View.GONE);
                    gameTypeSpinner.setVisibility(View.VISIBLE);

                } else if(position == 2) {  //Yet to start

                    gameTypeSpinner.setHint(getString(R.string.game_type));
                    text1.setHint(getString(R.string.table_no));
                    text2.setHint(getString(R.string.set_game_time));

                    header.setVisibility(View.GONE);
                    gameTypeSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

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

                        if(updateType.equalsIgnoreCase("Winner")) {
                            updateText1 = gameTypeSpinner.getText().toString();
                        } else {
                            updateHeader = gameTypeSpinner.getText().toString();
                        }

                        if(TextUtils.isEmpty(updateType) || TextUtils.isEmpty(updateHeader) ||
                                TextUtils.isEmpty(updateText1)) {
                            appGlobals.toastMsg(context, getString(R.string.enter_first_3), appGlobals.LENGTH_LONG);
                            return;
                        }

                        updateHeader = updateHeader.substring(0,1).toUpperCase() + updateHeader.substring(1);

                        String curTime = "" + System.currentTimeMillis();

                        /*LiveUpdateDetails updateDetails = new LiveUpdateDetails(updateType,
                                updateHeader, updateText1, updateText2, updateText3,
                                updateComments, curTime);*/

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
        updtDetLayout.setVisibility(View.GONE);
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

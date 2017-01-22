package rocket.club.com.rocketpoker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocket.club.com.rocketpoker.classes.ExpType;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 1/22/2017.
 */
public class AddExpenseType extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;
    List<String> typeList = null;
    View.OnClickListener onClickListener = null;

    final String TAG = "AddExpenseType";
    ConnectionDetector connectionDetector = null;
    public static final String ADD_EXP_TYPE = AppGlobals.SERVER_URL + "expenseType.php";

    ProgressDialog progressDialog = null;
    MaterialBetterSpinner curExpType;
    ArrayAdapter<String> expTypeAdapter;
    EditText newExpType;
    TextView errMsg;
    Button save, clear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_exp_type, container, false);

        initializeWidgets(view);
        setOnClickListener();

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        errMsg = (TextView) view.findViewById(R.id.err_msg);
        curExpType = (MaterialBetterSpinner) view.findViewById(R.id.curExpType);
        newExpType = (EditText) view.findViewById(R.id.newExpType);
        save = (Button) view.findViewById(R.id.btn_save);
        clear = (Button) view.findViewById(R.id.btn_clear);

        typeList = new ArrayList<String>();
        typeList.add("NA");

        expTypeAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, typeList);

        curExpType.setAdapter(expTypeAdapter);

        Map<String, String> fetch_map = new HashMap<String, String>();
        fetch_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
        fetch_map.put("type", "2");
        progressDialog = appGlobals.showDialog(context, getString(R.string.fetch_exp_type));
        serverCall(fetch_map, ADD_EXP_TYPE, "");
    }

    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.btn_save:

                        if(!connectionDetector.isConnectingToInternet()) {
                            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                            return;
                        }

                        String expType = newExpType.getText().toString();

                        if(TextUtils.isEmpty(expType)) {
                            errMsg.setText(getString(R.string.empty_exp_text));
                            errMsg.setVisibility(View.VISIBLE);
                            newExpType.requestFocus();
                            return;
                        }

                        expType = expType.substring(0, 1).toUpperCase() + expType.substring(1);

                        if(typeList.contains(expType)) {
                            errMsg.setText(getString(R.string.exp_text_avail));
                            errMsg.setVisibility(View.VISIBLE);
                            newExpType.requestFocus();
                            return;
                        }

                        errMsg.setVisibility(View.INVISIBLE);

                        Map<String, String> save_map = new HashMap<String, String>();
                        save_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                        save_map.put("type", "1");
                        save_map.put("expType", expType);
                        save_map.put("timeStamp", "" + System.currentTimeMillis());
                        progressDialog = appGlobals.showDialog(context, getString(R.string.fetch_exp_type));
                        serverCall(save_map, ADD_EXP_TYPE, expType);

                        break;
                    case R.id.btn_clear:
                        clearFields();
                        break;
                }
            }
        };
        save.setOnClickListener(onClickListener);
        clear.setOnClickListener(onClickListener);
    }

    private void clearFields() {
        errMsg.setText("");
        errMsg.setVisibility(View.INVISIBLE);
        curExpType.setText("");
        newExpType.setText("");
    }

    private void serverCall(final Map<String,String> params, final String url, final String newExpType) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Received " + response, LogClass.INFO_MSG);
                        if(TextUtils.isEmpty(newExpType)) {
                            try {
                                Gson gson = new Gson();
                                ExpType[] expTypeList = gson.fromJson(response, ExpType[].class);

                                typeList.clear();

                                for (ExpType expType : expTypeList) {
                                    typeList.add(expType.getExpType());
                                }

                                expTypeAdapter = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_dropdown_item_1line, typeList);
                                curExpType.setAdapter(expTypeAdapter);
                            } catch (Exception e) {
                                appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
                            }
                        } else {
                            if(response.equalsIgnoreCase("success")) {
                                appGlobals.toastMsg(context, getString(R.string.trans_save), appGlobals.LENGTH_LONG);
                                typeList.add(newExpType);
                                expTypeAdapter = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_dropdown_item_1line, typeList);
                                curExpType.setAdapter(expTypeAdapter);
                                clearFields();
                            }
                        }
                        appGlobals.cancelDialog(progressDialog);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
                        if(TextUtils.isEmpty(newExpType)) {
                            appGlobals.toastMsg(context, getString(R.string.fetch_exp_type_fail), appGlobals.LENGTH_LONG);
                        } else {
                            appGlobals.toastMsg(context, getString(R.string.trans_fail), appGlobals.LENGTH_LONG);
                        }
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

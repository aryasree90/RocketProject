package rocket.club.com.rocketpoker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocket.club.com.rocketpoker.classes.EmpDetails;
import rocket.club.com.rocketpoker.classes.ExpType;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 1/21/2017.
 */
public class ExpenseTransFragment extends Fragment {

    Context context;
    AppGlobals appGlobals = null;
    List<String> typeList = null;
    View.OnClickListener clickListener = null;
    ConnectionDetector connectionDetector = null;
    private static final String TAG = "ExpenseTransFragment";

    TextView errMsg;
    MaterialBetterSpinner expType;
    EditText expText, amount, expDesc;
    Button clear, save;

    ProgressDialog progressDialog = null;

    public static final String FETCH_EXP_TYPE = AppGlobals.SERVER_URL + "expenseType.php";
    public static final String SAVE_EXP = AppGlobals.SERVER_URL + "clubExpenses.php";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_transaction, container, false);

        initializeWidgets(view);
        setClickListener();

        return view;
    }

    private void initializeWidgets(View view) {

        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        errMsg = (TextView) view.findViewById(R.id.err_msg);
        expType = (MaterialBetterSpinner) view.findViewById(R.id.expType);
        expText = (EditText) view.findViewById(R.id.expTypeText);
        amount = (EditText) view.findViewById(R.id.amount);
        expDesc = (EditText) view.findViewById(R.id.expDesc);
        save = (Button) view.findViewById(R.id.btn_save);
        clear = (Button) view.findViewById(R.id.btn_clear);

        typeList = new ArrayList<String>();
        typeList.add(getString(R.string.newItem));

        ArrayAdapter<String> payTypeAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, typeList);
        expType.setAdapter(payTypeAdapter);

        expType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedExp = typeList.get(position);

                if(getString(R.string.newItem).equals(selectedExp)) {
                    expText.setVisibility(View.VISIBLE);
                } else {
                    expText.setVisibility(View.GONE);
                }
            }
        });

        Map<String, String> fetch_map = new HashMap<String, String>();
        fetch_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
        fetch_map.put("type", "2");
        progressDialog = appGlobals.showDialog(context, getString(R.string.fetch_exp_type));
        serverCall(fetch_map, FETCH_EXP_TYPE);
    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.btn_save:

                        if(!connectionDetector.isConnectingToInternet()) {
                            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                            return;
                        }

                        String selectedExpType = expType.getText().toString();
                        String newExpType = expText.getText().toString();
                        String amt = amount.getText().toString();
                        String desc = expDesc.getText().toString();

                        if(TextUtils.isEmpty(selectedExpType)) {
                            errMsg.setText(getString(R.string.empty_exp_type));
                            errMsg.setVisibility(View.VISIBLE);
                            expType.requestFocus();
                            return;
                        }

                        if((getString(R.string.newItem).equals(selectedExpType)) &&
                                TextUtils.isEmpty(newExpType)) {
                            errMsg.setText(getString(R.string.empty_exp_text));
                            errMsg.setVisibility(View.VISIBLE);
                            expText.requestFocus();
                            return;
                        }

                        if(TextUtils.isEmpty(amt)) {
                            errMsg.setText(getString(R.string.empty_exp_amount));
                            errMsg.setVisibility(View.VISIBLE);
                            amount.requestFocus();
                            return;
                        }

                        if(TextUtils.isEmpty(desc)) {
                            desc = "NA";
                        }

                        errMsg.setVisibility(View.INVISIBLE);

                        String saveExpType = expType.getText().toString();

                        if(getString(R.string.newItem).equals(saveExpType)) {
                            selectedExpType = expText.getText().toString();
                        }

                        Map<String, String> save_map = new HashMap<String, String>();
                        save_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                        save_map.put("expType", selectedExpType);
                        save_map.put("amount", amt);
                        save_map.put("timeStamp", "" + System.currentTimeMillis());
                        save_map.put("desc", desc);
                        progressDialog = appGlobals.showDialog(context, getString(R.string.assign_detail));
                        serverCall(save_map, SAVE_EXP);

                        break;
                    case R.id.btn_clear:
                        clearFields();
                        break;
                }
            }
        };
        save.setOnClickListener(clickListener);
        clear.setOnClickListener(clickListener);
    }

    private void clearFields() {
        errMsg.setText("");
        errMsg.setVisibility(View.INVISIBLE);
        expType.setText("");
        expText.setText("");
        expText.setVisibility(View.GONE);
        amount.setText("");
        expDesc.setText("");
    }

    private void serverCall(final Map<String,String> params, final String url) {

        if(!connectionDetector.isConnectingToInternet()) {
            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
            appGlobals.cancelDialog(progressDialog);
            return;
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Received " + response, LogClass.INFO_MSG);
                        if(url.equals(FETCH_EXP_TYPE)) {
                            try {
                                Gson gson = new Gson();
                                ExpType[] expTypeList = gson.fromJson(response, ExpType[].class);

                                for(ExpType expType : expTypeList) {
                                    typeList.add(expType.getExpType());
                                }

                                ArrayAdapter<String> payTypeAdapter = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_dropdown_item_1line, typeList);
                                expType.setAdapter(payTypeAdapter);
                            } catch(Exception e) {
                                appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
                            }
                        } else if(url.equals(SAVE_EXP)) {
                            if(response.equalsIgnoreCase("success")) {
                                appGlobals.toastMsg(context, getString(R.string.trans_save), appGlobals.LENGTH_LONG);
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
                        if(url.equals(FETCH_EXP_TYPE)) {
                            appGlobals.toastMsg(context, getString(R.string.fetch_exp_type_fail), appGlobals.LENGTH_LONG);
                        } else if(url.equals(SAVE_EXP)) {
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

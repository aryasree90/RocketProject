package rocket.club.com.rocketpoker;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocket.club.com.rocketpoker.classes.EmpDetails;
import rocket.club.com.rocketpoker.classes.ExpType;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 1/22/2017.
 */
public class AddEmpSalary extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;
    List<String> typeList = null;
    View.OnClickListener onClickListener = null;

    final String TAG = "AddEmpSalary";
    ConnectionDetector connectionDetector = null;
    public static final String ADD_EMP_SAL = AppGlobals.SERVER_URL + "salaryDetails.php";

    ProgressDialog progressDialog = null;
    EditText amount, searchNum;
    TextView errMsg, curSal, txtMemNotFound;
    TextView memNum, memName, memId;
    ImageButton searchBtn;
    Button save, clear;
    LinearLayout salLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_emp_sal, container, false);

        initializeWidgets(view);
        setOnClickListener();

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        amount = (EditText)view.findViewById(R.id.newSalary);
        searchNum = (EditText) view.findViewById(R.id.searchText);
        txtMemNotFound = (TextView) view.findViewById(R.id.txt_friend_not_found);
        curSal = (TextView) view.findViewById(R.id.cur_salary);
        searchBtn = (ImageButton) view.findViewById(R.id.searchBtn);
        errMsg = (TextView) view.findViewById(R.id.err_msg);
        salLayout = (LinearLayout) view.findViewById(R.id.salLayout);

        save = (Button) view.findViewById(R.id.btn_save);
        clear = (Button) view.findViewById(R.id.btn_clear);

        memId = (TextView) view.findViewById(R.id.memberId);
        memNum = (TextView) view.findViewById(R.id.memberNumber);
        memName = (TextView) view.findViewById(R.id.memberName);
    }

    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.searchBtn:
                        String empNum = searchNum.getText().toString();

                        if(TextUtils.isEmpty(empNum)) {
                            appGlobals.toastMsg(context, getString(R.string.login_invalid_num), appGlobals.LENGTH_LONG);
                            return;
                        }

                        if(!connectionDetector.isConnectingToInternet()) {
                            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                            return;
                        }

                        Map<String, String> search_map = new HashMap<String, String>();
                        search_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                        search_map.put("mem_mobile", empNum);
                        search_map.put("type", "2");
                        progressDialog = appGlobals.showDialog(context, getString(R.string.search_member));
                        serverCall(search_map, ADD_EMP_SAL, 1);

                        break;
                    case R.id.btn_save:

                        String id = memId.getText().toString();
                        String name = memName.getText().toString();
                        String num = memNum.getText().toString();
                        String amt = amount.getText().toString();
                        String oldAmt = curSal.getText().toString();

                        if(TextUtils.isEmpty(amt)) {
                            errMsg.setVisibility(View.VISIBLE);
                            errMsg.setText(getString(R.string.empty_exp_amount));
                            curSal.requestFocus();
                            return;
                        }

                        if(!connectionDetector.isConnectingToInternet()) {
                            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                            return;
                        }

                        errMsg.setVisibility(View.INVISIBLE);

                        String insertType = "insert";

                        if(oldAmt.contains(":")) {
                            String oldVal = oldAmt.split(getString(R.string.cur_sal))[1];
                            if(!oldVal.trim().equals("0")) {
                                insertType = "update";
                            }
                        }

                        Map<String, String> add_map = new HashMap<String, String>();
                        add_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                        add_map.put("memId", id);
                        add_map.put("memName", name);
                        add_map.put("mem_mobile", num);
                        add_map.put("amount", amt);
                        add_map.put("timeStamp", "" + System.currentTimeMillis());
                        add_map.put("insertType", insertType);
                        add_map.put("type", "1");
                        progressDialog = appGlobals.showDialog(context, getString(R.string.save_sal));
                        serverCall(add_map, ADD_EMP_SAL, 2);

                        break;
                    case R.id.btn_clear:
                        clearFields();
                        break;
                }
            }
        };
        searchBtn.setOnClickListener(onClickListener);
        save.setOnClickListener(onClickListener);
        clear.setOnClickListener(onClickListener);
    }

    private void clearFields() {
        txtMemNotFound.setVisibility(View.INVISIBLE);
        searchNum.setEnabled(true);
        searchNum.setText("");
        curSal.setText(getString(R.string.cur_sal));
        amount.setText("");
        errMsg.setVisibility(View.INVISIBLE);
        salLayout.setVisibility(View.INVISIBLE);
    }

    private void serverCall(final Map<String,String> params, final String url, final int call) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Received " + response, LogClass.INFO_MSG);
                        if(call == 1) {
                            try {
                                txtMemNotFound.setVisibility(View.INVISIBLE);
                                searchNum.setEnabled(false);

                                Gson gson = new Gson();
                                EmpDetails[] empDetailList = gson.fromJson(response, EmpDetails[].class);
                                int size = empDetailList.length;
                                EmpDetails empDetails = empDetailList[--size];

                                String tot = empDetails.getEmpSalary();

                                if (TextUtils.isEmpty(tot))
                                    tot = "0";

                                memId.setText(empDetails.getUserId());
                                memNum.setText(empDetails.getReg_mob());
                                memName.setText(empDetails.getName());
                                curSal.setText(getString(R.string.cur_sal) + tot);
                                salLayout.setVisibility(View.VISIBLE);

                            } catch (Exception e) {
                                txtMemNotFound.setVisibility(View.VISIBLE);
                                searchNum.setEnabled(true);
                                salLayout.setVisibility(View.INVISIBLE);
                            }
                        } else if(call == 2) {
                            if(response.equals("success")) {
                                appGlobals.toastMsg(context, getString(R.string.sal_save), appGlobals.LENGTH_LONG);
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
                        if(call == 1) {
                            txtMemNotFound.setVisibility(View.VISIBLE);
                            searchNum.setEnabled(true);
                            salLayout.setVisibility(View.INVISIBLE);
                        } else if(call == 2) {

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


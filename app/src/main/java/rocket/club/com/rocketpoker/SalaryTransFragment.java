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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.classes.EmpDetails;
import rocket.club.com.rocketpoker.classes.LiveUpdateDetails;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 1/10/2017.
 */
public class SalaryTransFragment extends Fragment {

    Context context;
    AppGlobals appGlobals = null;
    View.OnClickListener clickListener = null;
    ConnectionDetector connectionDetector = null;
    private static final String TAG = "SalaryTransFragment";

    Button save, clear;
    Button searchBtn, clearBtn;
    EditText searchUser, salAmount;
    LinearLayout transSalary = null, btnLayout = null;
    RelativeLayout showMemLayout = null;
    MaterialBetterSpinner payTypeSpinner;
    ProgressDialog progressDialog = null;
    ArrayAdapter<String> payTypeAdapter = null;
    TextView memNotFound, memId, memNum, memName, totSal, advPaid, balAmt, month, errMsg;

    public static final String MEMBER_SEARCH_URL = AppGlobals.SERVER_URL + "salaryDetails.php";
    public static final String MEMBER_SALARY_URL = AppGlobals.SERVER_URL + "memberSalary.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.salary_transaction, container, false);

        initializeWidgets(view);
        setClickListener();

        return view;
    }

    private void initializeWidgets(View view) {

        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        save = (Button) view.findViewById(R.id.btn_save);
        clear = (Button) view.findViewById(R.id.btn_clear);
        memId = (TextView) view.findViewById(R.id.memberId);
        errMsg = (TextView) view.findViewById(R.id.err_msg);
        salAmount = (EditText) view.findViewById(R.id.amount);
        totSal = (TextView) view.findViewById(R.id.totalSalary);
        memName = (TextView) view.findViewById(R.id.memberName);
        memNum = (TextView) view.findViewById(R.id.memberNumber);
        searchUser = (EditText) view.findViewById(R.id.searchText);
        searchBtn = (Button) view.findViewById(R.id.searchBtn);
        clearBtn = (Button) view.findViewById(R.id.clearBtn);
        btnLayout = (LinearLayout) view.findViewById(R.id.lnr_searchclearBtn);
        transSalary = (LinearLayout) view.findViewById(R.id.trans_salary);
        memNotFound = (TextView) view.findViewById(R.id.txt_member_not_found);
        showMemLayout = (RelativeLayout) view.findViewById(R.id.show_member_details);

        advPaid = (TextView) view.findViewById(R.id.advAmtPaid);
        balAmt = (TextView) view.findViewById(R.id.balAmt);
        month = (TextView) view.findViewById(R.id.month);

        String[] PAY_TYPE_LIST = getResources().getStringArray(R.array.pay_type_cash_out);
        payTypeSpinner = (MaterialBetterSpinner) view.findViewById(R.id.payType);
        payTypeSpinner.setVisibility(View.VISIBLE);
        payTypeAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, PAY_TYPE_LIST);
        payTypeSpinner.setAdapter(payTypeAdapter);

    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.clearBtn:
                        searchUser.setText("");
                        memNotFound.setVisibility(View.GONE);
                        break;
                    case R.id.searchBtn:

                        String empNum = searchUser.getText().toString();

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
                        serverCall(search_map, MEMBER_SEARCH_URL);

                        break;
                    case R.id.btn_save:

                        String _id = memId.getText().toString();
                        String mob = memNum.getText().toString();
                        String salary = salAmount.getText().toString();
                        String payType = payTypeSpinner.getText().toString();
                        String adv = advPaid.getText().toString();
                        String mnth = month.getText().toString();
                        String tot = totSal.getText().toString();
                        String bal = balAmt.getText().toString();


                        if(TextUtils.isEmpty(salary)) {
                            errMsg.setText(getString(R.string.add_emp_sal));
                            errMsg.setVisibility(View.VISIBLE);
                            return;
                        }

                        if(Integer.parseInt(salary) > Integer.parseInt(bal)) {
                            errMsg.setText(getString(R.string.larger_amount));
                            errMsg.setVisibility(View.VISIBLE);
                            return;
                        }

                        if(TextUtils.isEmpty(payType)) {
                            errMsg.setText(getString(R.string.select_paymnt_type));
                            errMsg.setVisibility(View.VISIBLE);
                            return;
                        }

                        errMsg.setText("");
                        errMsg.setVisibility(View.INVISIBLE);

                        if(!connectionDetector.isConnectingToInternet()) {
                            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                            return;
                        }

                        salary = "" + (Integer.parseInt(adv) + Integer.parseInt(salary));

                        Map<String, String> salary_map = new HashMap<String, String>();
                        salary_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                        salary_map.put("mem_id", _id);
                        salary_map.put("mem_num", mob);
//                        salary_map.put("advsalary", "");
                        salary_map.put("salary", salary);
                        salary_map.put("month", mnth);
                        salary_map.put("pay_type", payType);
                        salary_map.put("timeStamp", System.currentTimeMillis() + "");
                        progressDialog = appGlobals.showDialog(context, getString(R.string.search_member));
                        serverCall(salary_map, MEMBER_SALARY_URL);

                        break;
                    case R.id.btn_clear:
                        clearFields();
                        break;
                }
            }
        };
        searchBtn.setOnClickListener(clickListener);
        clearBtn.setOnClickListener(clickListener);
        save.setOnClickListener(clickListener);
        clear.setOnClickListener(clickListener);
    }

    private void clearFields() {
        searchUser.setText("");
        searchUser.setEnabled(true);
        errMsg.setVisibility(View.GONE);
        memNotFound.setVisibility(View.GONE);
        showMemLayout.setVisibility(View.GONE);
        transSalary.setVisibility(View.GONE);
        btnLayout.setVisibility(View.VISIBLE);
        payTypeSpinner.setText("");

        salAmount.setText("");
        memId.setText("");
        memName.setText("");
        memNum.setText("");
        totSal.setText("");
        advPaid.setText("");
        balAmt.setText("");
        month.setText("");

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
                        if(url.equals(MEMBER_SEARCH_URL)) {
                            try {

                                Gson gson = new Gson();
                                EmpDetails[] empDetailList = gson.fromJson(response, EmpDetails[].class);
                                int size = empDetailList.length;
                                EmpDetails empDetails = empDetailList[--size];

                                String tot = empDetails.getEmpSalary();

                                if(tot == null || tot.isEmpty()) {
                                    memNotFound.setVisibility(View.VISIBLE);
                                    memNotFound.setText(getString(R.string.empty_sal));
                                    appGlobals.cancelDialog(progressDialog);
                                    return;
                                }

                                memNotFound.setVisibility(View.INVISIBLE);
                                showMemLayout.setVisibility(View.VISIBLE);
                                transSalary.setVisibility(View.VISIBLE);
                                searchUser.setEnabled(true);
                                btnLayout.setVisibility(View.GONE);

                                memId.setText(empDetails.getUserId());
                                memName.setText(empDetails.getName());
                                memNum.setText(empDetails.getReg_mob());
                                totSal.setText(tot);

                                String bal = "";//empDetails.getAdvSalary();
                                String adv = empDetails.getSalary();
                                String mnth = empDetails.getMonth();

                                if(tot == null || tot.isEmpty())
                                    tot = "0";

                                if(adv == null || adv.isEmpty())
                                    adv = "0";

                                int salToPay = Integer.parseInt(tot) - Integer.parseInt(adv);

                                if(salToPay > 0) {
                                    bal = "" + salToPay;
                                }

                                if(TextUtils.isEmpty(bal))
                                    bal = "0";
                                if(TextUtils.isEmpty(mnth) || !getMonth().equals(mnth)) {
                                    mnth = getMonth();
                                }

                                advPaid.setText(adv);
                                balAmt.setText(bal);
                                month.setText(mnth);
                            }catch(Exception e) {
                                memNotFound.setVisibility(View.VISIBLE);
                                memNotFound.setText(getString(R.string.member_not_found));
                                showMemLayout.setVisibility(View.GONE);
                                transSalary.setVisibility(View.GONE);
                                btnLayout.setVisibility(View.VISIBLE);
                            }

                        } else if(url.equals(MEMBER_SALARY_URL)) {
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
                        if(url.equals(MEMBER_SEARCH_URL)) {
                            memNotFound.setVisibility(View.VISIBLE);
                            showMemLayout.setVisibility(View.INVISIBLE);
                            transSalary.setVisibility(View.INVISIBLE);
                        } else if(url.equals(MEMBER_SALARY_URL)) {
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

    private String getMonth() {
        long timeStamp = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp);
        int pos = cal.get(Calendar.MONTH);
        return appGlobals.monthList[pos];
    }
}

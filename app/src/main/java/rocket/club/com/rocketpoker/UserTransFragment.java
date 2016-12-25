package rocket.club.com.rocketpoker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.classes.UserTransaction;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 12/25/2016.
 */
public class UserTransFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;

    EditText searchMem, amount;
    TextView memNotFound, memId, memName, memNum;
    TextView creditAvail, bonusAvail;
    ImageButton searchBtn;
    LinearLayout memberDetails, transDetails;
    MaterialBetterSpinner transTypeSpinner = null;
    ArrayAdapter<String> transTypeAdapter = null;
    Button save, clear;

    String[] TRANSACTION_TYPE_LIST = null;
    ProgressDialog progressDialog = null;
    View.OnClickListener clickListener = null;
    public static final String FETCH_TRANS_URL = AppGlobals.SERVER_URL + "fetchTransaction.php";
    public static final String MEMBER_TRANS_URL = AppGlobals.SERVER_URL + "memberTransaction.php";
    public static final String MEMBER_SEARCH_URL = AppGlobals.SERVER_URL + "searchMember.php";
    private final String TAG = "UserTransFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_transaction, container, false);

        initializeWidgets(view);
        setClickListener();

        return view;
    }

    private void initializeWidgets(View view) {

        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);

        searchMem = (EditText) view.findViewById(R.id.searchText);
        memNotFound = (TextView) view.findViewById(R.id.txt_member_not_found);
        searchBtn = (ImageButton) view.findViewById(R.id.searchBtn);
        memberDetails = (LinearLayout) view.findViewById(R.id.show_member_details);
        transDetails = (LinearLayout) view.findViewById(R.id.trans_details);

        memId = (TextView) view.findViewById(R.id.memberId);
        memName = (TextView) view.findViewById(R.id.memberName);
        memNum = (TextView) view.findViewById(R.id.memberNumber);

        amount = (EditText) view.findViewById(R.id.amount);

        TRANSACTION_TYPE_LIST = getResources().getStringArray(R.array.trans_type_list);
        transTypeSpinner = (MaterialBetterSpinner) view.findViewById(R.id.transType);
        transTypeAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, TRANSACTION_TYPE_LIST);
        transTypeSpinner.setAdapter(transTypeAdapter);

        save = (Button) view.findViewById(R.id.btn_save);
        clear = (Button) view.findViewById(R.id.btn_clear);

        creditAvail = (TextView) view.findViewById(R.id.credit);
        bonusAvail = (TextView) view.findViewById(R.id.bonus);
    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.searchBtn:
                        String searchAFriend = searchMem.getText().toString();

                        if(searchAFriend.isEmpty()) {
                            appGlobals.toastMsg(context, getString(R.string.login_invalid_num), appGlobals.LENGTH_LONG);
                            return;
/*                        } else if (appGlobals.sharedPref.getLoginMobile().contains(searchAFriend)) {
                            appGlobals.toastMsg(context, getString(R.string.req_to_own_num), appGlobals.LENGTH_LONG);*/
                        } else {
                            //searchMem.setEnabled(false);
                            Map<String, String> search_map = new HashMap<String, String>();
                            search_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                            search_map.put("mem_mobile", searchAFriend);
                            progressDialog = appGlobals.showDialog(context, getString(R.string.search_member));
                            serverCall(search_map, MEMBER_SEARCH_URL);
                        }
                        break;
                    case R.id.btn_save:

                        String amt = amount.getText().toString();
                        String userMob =  memNum.getText().toString();
                        String transType = transTypeSpinner.getText().toString();

                        int creditAmt = Integer.parseInt(creditAvail.getText().toString().split(getString(R.string.credit_))[1].trim());
                        int bonusAmt = Integer.parseInt(bonusAvail.getText().toString().split(getString(R.string.bonus_))[1].trim());

                        if(TextUtils.isEmpty(amt)) {
                            appGlobals.toastMsg(context, getString(R.string.invalid_amt), appGlobals.LENGTH_LONG);
                            return;
                        }

                        long timeStamp = System.currentTimeMillis();

                        if(transType.equals(TRANSACTION_TYPE_LIST[2])) {
                            creditAmt += Integer.parseInt(amt);
                        } else if(transType.equals(TRANSACTION_TYPE_LIST[3])) {
                            creditAmt -= Integer.parseInt(amt);
                        }

                        if(transType.equals(TRANSACTION_TYPE_LIST[4])) {
                            bonusAmt += Integer.parseInt(amt);
                        } else if(transType.equals(TRANSACTION_TYPE_LIST[5])) {
                            bonusAmt -= Integer.parseInt(amt);
                        }

                        if(creditAmt > 5000) {
                            appGlobals.toastMsg(context, getString(R.string.credit_limit), appGlobals.LENGTH_LONG);
                            return;
                        }

                        if(bonusAmt < 0) {
                            appGlobals.toastMsg(context, getString(R.string.bonus_limit), appGlobals.LENGTH_LONG);
                            return;
                        }

                        Map<String, String> trans_map = new HashMap<String, String>();
                        trans_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                        trans_map.put("user", userMob);
                        trans_map.put("amount", amt);
                        trans_map.put("transType", transType);
                        trans_map.put("timeStamp", String.valueOf(timeStamp));
                        trans_map.put("credit", "" + creditAmt);
                        trans_map.put("bonus", "" + bonusAmt);

                        progressDialog = appGlobals.showDialog(context, getString(R.string.saving_trans));
                        serverCall(trans_map, MEMBER_TRANS_URL);

                        break;
                    case R.id.btn_clear:
                        clearFields();
                        break;
                }
            }
        };
        searchBtn.setOnClickListener(clickListener);
        save.setOnClickListener(clickListener);
        clear.setOnClickListener(clickListener);
    }

    private void serverCall(final Map<String, String> map, final String URL) {

        if(!appGlobals.isNetworkConnected(context)) {
            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
            appGlobals.cancelDialog(progressDialog);
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(URL.equals(MEMBER_SEARCH_URL)){
                            try {
                                memNotFound.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                UserDetails userDetails = gson.fromJson(response, UserDetails.class);

                                memId.setText(userDetails.getUserId());
                                memName.setText(userDetails.getUserName());
                                memNum.setText(userDetails.getMobile());

                                memberDetails.setVisibility(View.VISIBLE);
                                transDetails.setVisibility(View.VISIBLE);

                                fetchUserTransDetails(userDetails.getMobile());
                            } catch(Exception e) {
                                appGlobals.toastMsg(context, getString(R.string.friend_not_found), appGlobals.LENGTH_LONG);
                                memNotFound.setVisibility(View.VISIBLE);
                                clearFields();
                            }
                        } else if(URL.equals(MEMBER_TRANS_URL)) {
                            if(response.equals("success")) {
                                appGlobals.toastMsg(context, getString(R.string.trans_save), appGlobals.LENGTH_LONG);
                                clearFields();
                            } else {
                                appGlobals.toastMsg(context, getString(R.string.trans_fail), appGlobals.LENGTH_LONG);
                            }
                        } else if(URL.equals(FETCH_TRANS_URL)) {
                            if(!TextUtils.isEmpty(response)) {
                                Gson gson = new Gson();

                                UserTransaction[] userTransactions = gson.fromJson(response, UserTransaction[].class);

                                if(userTransactions.length > 0) {
                                    UserTransaction trans = userTransactions[0];

                                    String creditAmt = "0", bonusAmt = "0";

                                    if(!TextUtils.isEmpty(trans.getAvail_credit()))
                                        creditAmt = trans.getAvail_credit();

                                    if(!TextUtils.isEmpty(trans.getBonus()))
                                        bonusAmt = trans.getBonus();

                                    creditAvail.setText(getString(R.string.credit_) + creditAmt);
                                    bonusAvail.setText(getString(R.string.bonus_) + bonusAmt);
                                }
                            }
                        }
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
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void fetchUserTransDetails(String mob) {
        Map<String, String> search_map = new HashMap<String, String>();
        search_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
        search_map.put("user", mob);

        serverCall(search_map, FETCH_TRANS_URL);
    }

    private void clearFields() {
        searchMem.setText("");
        searchMem.requestFocus();

        memId.setText("");
        memName.setText("");
        memNum.setText("");
        amount.setText("");

        memberDetails.setVisibility(View.INVISIBLE);
        transDetails.setVisibility(View.INVISIBLE);
    }
}
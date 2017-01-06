package rocket.club.com.rocketpoker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.adapter.FriendListAdapter;
import rocket.club.com.rocketpoker.adapter.TransactionAdapter;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.classes.UserTransaction;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 12/25/2016.
 */
public class TransactionFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;

    RecyclerView transactionView = null;
    ProgressDialog progressDialog = null;

    TextView creditText, bonusText;

    public static final String MOB_TRANS = "Mobile Transaction";

    String mobNum = "";
    private final String TAG = "TransactionFragment";
    public static final String FETCH_TRANS_URL = AppGlobals.SERVER_URL + "fetchTransaction.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        initializeWidgets(view);

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);

        Bundle bundle = getArguments();
        mobNum = bundle.getString(MOB_TRANS);

        transactionView = (RecyclerView) view.findViewById(R.id.transList);
        creditText = (TextView) view.findViewById(R.id.credit);
        bonusText = (TextView) view.findViewById(R.id.bonus);

        initializeData(mobNum);

/*        if(pageType == PAGE_CASHIER) {
            searchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mob = searchText.getText().toString();

                    if(TextUtils.isEmpty(mob)) {
                        appGlobals.toastMsg(context, getString(R.string.login_mobile), Toast.LENGTH_LONG);
                        return;
                    }

                    initializeData(mob);
                }
            });
        } else {
            initializeData(appGlobals.sharedPref.getLoginMobile());
        }*/
    }

    private void initializeData(String user) {
        Map<String, String> search_map = new HashMap<String, String>();
        search_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
        search_map.put("user", user);

        progressDialog = appGlobals.showDialog(context, getString(R.string.fetch_trans));

        serverCall(search_map, FETCH_TRANS_URL);
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
                        if(URL.equals(FETCH_TRANS_URL)) {
                            if(!TextUtils.isEmpty(response)) {
                                Gson gson = new Gson();
                                UserTransaction[] userTransactions = gson.fromJson(response, UserTransaction[].class);

                                String creditAmt = "0", bonusAmt = "0";
                                if(userTransactions.length > 0) {
                                    UserTransaction latestTrans = userTransactions[0];
                                    creditAmt = latestTrans.getAvail_credit();
                                    bonusAmt = latestTrans.getBonus();
                                    loadTransactionDetails(userTransactions);
                                }

                                creditText.setText(getString(R.string.credit_) + creditAmt);
                                bonusText.setText(getString(R.string.bonus_) + bonusAmt);
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

    private void loadTransactionDetails(UserTransaction[] userTransactions) {

        /*UserTransaction latestTrans = userTransactions[0];
        creditText.setText(getString(R.string.credit_) + latestTrans.getAvail_credit());
        bonusText.setText(getString(R.string.bonus_) + latestTrans.getBonus());*/

        TransactionAdapter adapter = new TransactionAdapter(userTransactions, context);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        transactionView.setLayoutManager(mLayoutManager);
        transactionView.setItemAnimator(new DefaultItemAnimator());
        transactionView.setAdapter(adapter);
    }
}

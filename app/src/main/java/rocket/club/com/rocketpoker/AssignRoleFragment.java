package rocket.club.com.rocketpoker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocket.club.com.rocketpoker.adapter.AdminRoleAdapter;
import rocket.club.com.rocketpoker.classes.RegisterDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 12/4/2016.
 */
public class AssignRoleFragment extends Fragment {

    Context context;
    AppGlobals appGlobals;
    ConnectionDetector connectionDetector = null;

    RegisterDetails[] registerDetails = null;
    AdminRoleAdapter mAdapter = null;
    EditText selectMember = null;
    RecyclerView selectedMembersList = null;
    View.OnClickListener clickListener = null;
    ProgressDialog progressDialog = null;
    Button saveBtn, clearBtn;

    ArrayList<RegisterDetails> changedMembers = new ArrayList<>();

    private static final String TAG = "AssignRoleFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assign_role, container, false);

        initializeWidgets(view);
        setClickListener();
        initializeData();

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        selectMember = (EditText) view.findViewById(R.id.selectMember);
        selectedMembersList = (RecyclerView) view.findViewById(R.id.selectedMembers);

        saveBtn = (Button) view.findViewById(R.id.btn_save);
        clearBtn = (Button) view.findViewById(R.id.btn_clear);

        registerDetails = new RegisterDetails[0];

        selectMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.btn_save:
                        saveChanges();
                        break;
                    case R.id.btn_clear:
                        selectMember.setText("");
                        loadDetails();
                        break;
                }
            }
        };
        saveBtn.setOnClickListener(clickListener);
        clearBtn.setOnClickListener(clickListener);
    }

    private void initializeData() {
        Map<String,String> map = new HashMap<String,String>();
        map.put("mobile", appGlobals.sharedPref.getLoginMobile());

        String url = AppGlobals.SERVER_URL + "getRocketUserList.php";
        progressDialog = appGlobals.showDialog(context, getString(R.string.search_member));
        serverCall(map, url, 1);
    }

    private void serverCall(final Map<String, String> params, String url, final int caller) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(caller == 1) {
                            Gson gson = new Gson();
                            registerDetails = gson.fromJson(response, RegisterDetails[].class);

                            loadDetails();
                        } else if(caller == 2) {

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
                return params;
            }
        };

        if(connectionDetector.isConnectingToInternet()) {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        } else {
            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
        }
    }

    private void loadDetails() {
        mAdapter = new AdminRoleAdapter(registerDetails, context, changedMembers);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        selectedMembersList.setLayoutManager(mLayoutManager);
        selectedMembersList.setItemAnimator(new DefaultItemAnimator());
        selectedMembersList.setAdapter(mAdapter);
    }

    public void saveChanges() {
        final String url = AppGlobals.SERVER_URL + "memberUpdateList.php";
        for(RegisterDetails regDetails : changedMembers) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("mobile", appGlobals.sharedPref.getLoginMobile());
            map.put("user_mob", regDetails.getReg_mob());
            map.put("user_type", regDetails.getUser_type());
            map.put("gcm_regid", regDetails.getGcm_regid());
            map.put("user_id", regDetails.getUserId());

            progressDialog = appGlobals.showDialog(context, getString(R.string.assign_detail));

            serverCall(map, url, 2);
        }
    }
}

package rocket.club.com.rocketpoker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 12/25/2016.
 */
public class UserTransFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;

    EditText searchMem;
    TextView memNotFound, memId, memName, memNum;
    ImageButton searchBtn;
    LinearLayout memberDetails;

    ProgressDialog progressDialog = null;
    View.OnClickListener clickListener = null;
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

        memId = (TextView) view.findViewById(R.id.memberId);
        memName = (TextView) view.findViewById(R.id.memberName);
        memNum = (TextView) view.findViewById(R.id.memberNumber);
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
                }
            }
        };
        searchBtn.setOnClickListener(clickListener);
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
                            } catch(Exception e) {
                                appGlobals.toastMsg(context, getString(R.string.friend_not_found), appGlobals.LENGTH_LONG);
                                searchMem.setText("");
                                memNotFound.setVisibility(View.VISIBLE);
                 //               searchMem.setEnabled(true);
                                searchMem.requestFocus();
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


}

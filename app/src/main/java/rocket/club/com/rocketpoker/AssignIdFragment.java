package rocket.club.com.rocketpoker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.adapter.AdminRoleAdapter;
import rocket.club.com.rocketpoker.classes.RegisterDetails;
import rocket.club.com.rocketpoker.classes.UserDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 12/4/2016.
 */
public class AssignIdFragment extends Fragment {

    Context context;
    AppGlobals appGlobals;
    ConnectionDetector connectionDetector = null;

    EditText searchFriend;
    ImageButton searchBtn;
    LinearLayout memberDetails = null;
    Button changeId, clearBtn;
    TextView errorMsg, memberName, memberNum, memberId;
    View.OnClickListener clickListener = null;
    ProgressDialog progressDialog = null;

    final String URL = AppGlobals.SERVER_URL + "memberIdUpdate.php";

    private static final String TAG = "AssignIdFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assign_id, container, false);

        initializeWidgets(view);
        setClickListener();

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        memberDetails = (LinearLayout) view.findViewById(R.id.show_friend_details);
        searchFriend = (EditText) view.findViewById(R.id.searchText);
        searchBtn = (ImageButton) view.findViewById(R.id.searchBtn);
        errorMsg = (TextView) view.findViewById(R.id.txt_friend_not_found);

        memberId = (TextView) view.findViewById(R.id.member_id);
        memberNum = (TextView) view.findViewById(R.id.friendNumber);
        memberName = (TextView) view.findViewById(R.id.friendName);

        changeId = (Button) view.findViewById(R.id.changeId);
        clearBtn = (Button) view.findViewById(R.id.clearBtn);
    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.searchBtn:

                        String searchAFriend = searchFriend.getText().toString();

                        if(searchAFriend.isEmpty()) {
                            appGlobals.toastMsg(context, getString(R.string.login_invalid_num), appGlobals.LENGTH_LONG);
                            return;
                        } else if (appGlobals.sharedPref.getLoginMobile().contains(searchAFriend)) {
                            appGlobals.toastMsg(context, getString(R.string.req_to_own_num), appGlobals.LENGTH_LONG);
                        } else {
                            searchFriend.setEnabled(false);
                            Map<String, String> search_map = new HashMap<String, String>();
                            search_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                            search_map.put("frnd_mobile", searchAFriend);
                            progressDialog = appGlobals.showDialog(context, getString(R.string.search_member));

                            serverCall(search_map, FriendsFragment.FRIEND_SEARCH_URL);
                        }

                        break;
                    case R.id.changeId:
                        String memId = memberId.getText().toString();

                        if(memId.isEmpty()) {
                            appGlobals.toastMsg(context, getString(R.string.login_invalid_num), appGlobals.LENGTH_LONG);
                            return;
                        } else {
                            Map<String, String> member_map = new HashMap<String, String>();
                            member_map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                            member_map.put("user_mob", memberNum.getText().toString());
                            member_map.put("user_id", memId);
                            progressDialog = appGlobals.showDialog(context, getString(R.string.assign_detail));
                            serverCall(member_map, URL);
                        }
                        break;
                    case R.id.clearBtn:
                        clearFields();
                        break;
                }
            }
        };
        searchBtn.setOnClickListener(clickListener);
        changeId.setOnClickListener(clickListener);
        clearBtn.setOnClickListener(clickListener);
    }

    private void clearFields() {
        memberDetails.setVisibility(View.GONE);
        errorMsg.setVisibility(View.GONE);

        searchFriend.setEnabled(true);
        searchFriend.setText("");
        memberId.setText("");
        memberNum.setText("");
        memberName.setText("");
    }

    private void serverCall(final Map<String, String> params, final String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(url.equals(FriendsFragment.FRIEND_SEARCH_URL)) {
                            try {
                                errorMsg.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                UserDetails userDetails = gson.fromJson(response, UserDetails.class);

                                memberId.setText(userDetails.getUserId());
                                memberName.setText(userDetails.getUserName());
                                memberNum.setText(userDetails.getMobile());

                                memberDetails.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                appGlobals.toastMsg(context, getString(R.string.friend_not_found), appGlobals.LENGTH_LONG);
                                searchFriend.setText("");
                                errorMsg.setVisibility(View.VISIBLE);
                                searchFriend.setEnabled(true);
                                searchFriend.requestFocus();
                                appGlobals.logClass.setLogMsg(TAG, e.toString(), LogClass.ERROR_MSG);
                            }
                        } else if(response.equals("success")){
                            clearFields();
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

    @Override
    public void onPause() {
        super.onPause();

    }
}

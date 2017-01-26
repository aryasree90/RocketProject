package rocket.club.com.rocketpoker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocket.club.com.rocketpoker.adapter.ClubDetailsAdapter;
import rocket.club.com.rocketpoker.classes.DetailsListClass;
import rocket.club.com.rocketpoker.classes.EmpDetails;
import rocket.club.com.rocketpoker.classes.ExpTrans;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 1/26/2017.
 */
public class FilterTransaction extends Activity {

    Context context = null;
    AppGlobals appGlobals = null;
    View.OnClickListener clickListener = null;

    Button clearBtn;
    TextView label;
    RecyclerView filterView;
    MaterialBetterSpinner filter1;
    Button filter2, filter3;

    final String TAG = "FilterTransaction";
    ProgressDialog progressDialog = null;
    ConnectionDetector connectionDetector = null;

    public static final String FILTER_TRANS = "Filter Trans";
    public static final String EXPENSE = "expense";
    public static final String SALARY = "salary";
    public static final String USER = "user";
    public static final String USER_TYPE = "User Type";

    private List<String> typeList = new ArrayList<>();
    private String filterTrans = "";
    private boolean normalUser = true;
    String startTime = "", endTime = "";

    public static final String FETCH_DET = AppGlobals.SERVER_URL + "fetchTransWithQuery.php";
    public static final String FETCH_ID = AppGlobals.SERVER_URL + "fetchRocketId.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_trans);

        initializeWidgets();
        setOnClickListener();

    }

    private void initializeWidgets() {
        context = getApplicationContext();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        clearBtn = (Button) findViewById(R.id.clearBtn);
        label = (TextView) findViewById(R.id.label);
        filter1 = (MaterialBetterSpinner) findViewById(R.id.filter1);
        filter2 = (Button) findViewById(R.id.filter2);
        filter3 = (Button) findViewById(R.id.filter3);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            if(bundle.containsKey(FILTER_TRANS)) {
                filterTrans = bundle.getString(FILTER_TRANS);

                if(filterTrans.equals(USER)) {
                    if(bundle.containsKey(USER_TYPE)) {
                        normalUser = bundle.getBoolean(USER_TYPE);
                    } else {
                        normalUser = true;
                    }
                } else {
                    normalUser = false;
                }
            } else {
                filterTrans = USER;
            }
        } else {
            filterTrans = USER;
            normalUser = true;
        }

        initLayout();

        if(normalUser) {
            String rocketId = appGlobals.sharedPref.getRocketId();
            typeList.clear();
            typeList.add(rocketId);
            loadFilter();
        } else {
            Map<String, String> map = new HashMap<String, String>();
            map.put("mobile", appGlobals.sharedPref.getLoginMobile());
            map.put("type", filterTrans);

            progressDialog = appGlobals.showDialog(this, "Fetching Details");
            serverCall(map, FETCH_ID);
        }
    }

    private void initLayout() {

        String labelText = "", filter1Text = "";
        if(filterTrans.equals(EXPENSE)) {
            labelText = getString(R.string.exp_list);
            filter1Text = getString(R.string.exp_type);
        } else if(filterTrans.equals(SALARY)) {
            labelText = getString(R.string.sal_list);
            filter1Text = getString(R.string.rocket_id);
        } else if(filterTrans.equals(USER)) {
            labelText = getString(R.string.user_trans);
            filter1Text = getString(R.string.rocket_id);

            if(normalUser) {
                filter1.setEnabled(false);
            }
        }

        label.setText(labelText);
        filter1.setHint(filter1Text);

        typeList.clear();
        typeList.add("NA");

        loadFilter();
    }

    private void loadFilter() {
        ArrayAdapter<String> expTypeAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, typeList);
        filter1.setAdapter(expTypeAdapter1);
    }

    private void showDatePickerDialog(final Button btn, final boolean start) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        int month = monthOfYear + 1;
                        String selDateTime = checkTime(dayOfMonth) + "-" + checkTime(month) + "-" + year;
                        btn.setText(selDateTime);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        try {
                            if(start)
                                selDateTime +=  " 00:00:00";
                            else
                                selDateTime +=  " 23:59:59";

                            Date d = sdf.parse(selDateTime);

                            Calendar c = Calendar.getInstance();
                            c.setTime(d);
                            long time = c.getTimeInMillis();

                            if(start)
                                startTime = "" + time;
                            else
                                endTime = "" + time;

                        } catch(ParseException pe) {
                            pe.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private String checkTime(int val) {
        String finalVal = "";
        if(val < 10)
            finalVal = "0" + val;
        else
            finalVal = "" + val;
        return finalVal;
    }

    private void setOnClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.clearBtn:

                        break;
                    case R.id.filter2:
                        showDatePickerDialog(filter2, true);
                        callServer();
                        break;
                    case R.id.filter3:
                        showDatePickerDialog(filter3, false);
                        callServer();
                        break;
                }
            }
        };
        clearBtn.setOnClickListener(clickListener);
        filter2.setOnClickListener(clickListener);
        filter3.setOnClickListener(clickListener);
    }

    private void callServer() {
        if(startTime.isEmpty()) {
            return;
        }

        if(endTime.isEmpty()) {
            return;
        }

        String memId = filter1.getText().toString();

        if(!memId.isEmpty() && memId.contains(":")) {
            memId = memId.split(":")[0].trim();
        }

        if(normalUser && TextUtils.isEmpty(memId))
            memId = appGlobals.sharedPref.getLoginMobile();

        Map<String,String> map = new HashMap<String,String>();
        map.put("mobile", appGlobals.sharedPref.getLoginMobile());
        map.put("memId", memId);
        map.put("start", startTime);
        map.put("end", endTime);
        map.put("type", filterTrans);

        serverCall(map, FETCH_DET);
    }

    private void serverCall(final Map<String,String> params, final String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Gson gson = new Gson();
                        if(url.equals(FETCH_ID)) {
                            DetailsListClass tempClass[] = gson.fromJson(response, DetailsListClass[].class);

                            if(tempClass.length <= 0)
                                return;

                            typeList.clear();
                            for(DetailsListClass temp : tempClass) {
                                if (filterTrans.equals(EXPENSE)) {
                                    typeList.add(temp.getItem1());
                                } else {
                                    typeList.add(temp.getItem1() + ":" + temp.getItem2());
                                }
                            }
                            if(typeList.size() > 0)
                                loadFilter();
                        } else if(url.equals(FETCH_DET)) {
                            Log.d("__________________", "____________________" + response);
                        }

//                        loadFilter();
                        appGlobals.cancelDialog(progressDialog);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        appGlobals.cancelDialog(progressDialog);
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
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

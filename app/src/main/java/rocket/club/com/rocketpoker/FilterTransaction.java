package rocket.club.com.rocketpoker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;

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
import rocket.club.com.rocketpoker.classes.UserTransDetails;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 1/26/2017.
 */
public class FilterTransaction extends AppCompatActivity {

    Context context = null;
    AppGlobals appGlobals = null;
    View.OnClickListener clickListener = null;

    Button clearBtn, searchBtn, graphBtn;
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
    public static final String USER_ID = "User Id";

    private List<String> typeList = new ArrayList<>();
    private String filterTrans = "";
    private boolean normalUser = true;
    String startTime = "", endTime = "", rocketId = "";
    Toolbar toolBar = null;
    ActionBar actionBar = null;

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

        toolBar = (Toolbar)findViewById(R.id.hometoolbar);
        setSupportActionBar(toolBar);

        actionBar = getSupportActionBar();
//        actionBar.setTitle("Chat Room");
        toolBar.setNavigationIcon(R.mipmap.ic_arrow_back);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        graphBtn = (Button) findViewById(R.id.graphBtn);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        clearBtn = (Button) findViewById(R.id.clearBtn);
        label = (TextView) findViewById(R.id.label);
        filter1 = (MaterialBetterSpinner) findViewById(R.id.filter1);
        filter2 = (Button) findViewById(R.id.filter2);
        filter3 = (Button) findViewById(R.id.filter3);
        filterView = (RecyclerView) findViewById(R.id.clubList);

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

                    if(bundle.containsKey(USER_ID)) {
                        rocketId = bundle.getString(USER_ID);
                    } else {
                        rocketId = appGlobals.sharedPref.getRocketId();
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
            if(rocketId.isEmpty())
                rocketId = appGlobals.sharedPref.getRocketId();
            typeList.clear();
            typeList.add(rocketId);
            loadFilter(true);
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

//        label.setText(labelText);
        actionBar.setTitle(labelText);
        filter1.setHint(filter1Text);

        typeList.clear();
        typeList.add("NA");

        loadFilter(false);
    }

    private void loadFilter(boolean callServer) {
        ArrayAdapter<String> expTypeAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, typeList);
        filter1.setAdapter(expTypeAdapter1);

        if(callServer) {
            fetchDetServer(rocketId);
        }
    }

    private void fetchDetServer(String memId) {

        if(startTime.isEmpty()) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            startTime = "" + cal.getTimeInMillis();
        }

        if(endTime.isEmpty()) {
            endTime = "" + System.currentTimeMillis();
        }

        if(memId.equals("All")) {
            memId = "";
        }

        Map<String,String> map = new HashMap<String,String>();
        map.put("mobile", appGlobals.sharedPref.getLoginMobile());
        map.put("memId", memId);
        map.put("start", startTime);
        map.put("end", endTime);
        map.put("type", filterTrans);

        serverCall(map, FETCH_DET);
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
                    case R.id.searchBtn:
                        fetchDetails();
                        break;
                    case R.id.graphBtn:

                        if(!appGlobals.chartList.isEmpty()) {
                            Intent chartIntent = new Intent(FilterTransaction.this, TransactionGraph.class);
                            startActivity(chartIntent);
                        }
                        break;
                    case R.id.clearBtn:

                        filter1.setText("");
                        filter2.setText("");
                        filter3.setText("");
                        filterView.setVisibility(View.INVISIBLE);

                        break;
                    case R.id.filter2:
                        showDatePickerDialog(filter2, true);
                        break;
                    case R.id.filter3:
                        showDatePickerDialog(filter3, false);
                        break;
                }
            }
        };
        searchBtn.setOnClickListener(clickListener);
        clearBtn.setOnClickListener(clickListener);
        graphBtn.setOnClickListener(clickListener);
        filter2.setOnClickListener(clickListener);
        filter3.setOnClickListener(clickListener);
    }

    private void fetchDetails() {

        String memId = filter1.getText().toString();

        if(!memId.isEmpty() && memId.contains(":")) {
            memId = memId.split(":")[0].trim();
        }

        if(normalUser && TextUtils.isEmpty(memId))
            memId = appGlobals.sharedPref.getLoginMobile();

        fetchDetServer(memId);
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
                            typeList.add("All");
                            for(DetailsListClass temp : tempClass) {
                                if (filterTrans.equals(EXPENSE)) {
                                    typeList.add(temp.getItem1());
                                } else {
                                    typeList.add(temp.getItem1() + ":" + temp.getItem2());
                                }
                            }
                            if(typeList.size() > 0)
                                loadFilter(true);
                        } else if(url.equals(FETCH_DET)) {
                            if(response.isEmpty())
                                return;

                            filterView.setVisibility(View.INVISIBLE);

                            DetailsListClass[] detailList = null;
                            if(filterTrans.equals(EXPENSE)) {
                                ExpTrans[] expTrans = gson.fromJson(response, ExpTrans[].class);
                                if(expTrans.length <= 0) {

                                    return;
                                }
                                detailList = loadExpDetails(expTrans);
                            } else if(filterTrans.equals(SALARY)) {
                                EmpDetails[] empDet = gson.fromJson(response, EmpDetails[].class);
                                if(empDet.length <= 0) {
                                    return;
                                }
                                detailList = loadEmpDetails(empDet);
                            } else if(filterTrans.equals(USER)) {
                                UserTransDetails[] userDet = gson.fromJson(response, UserTransDetails[].class);
                                if(userDet.length <= 0) {
                                    return;
                                }
                                detailList = loadUserDetails(userDet);
                            }

                            if(detailList != null) {
                                filterView.setVisibility(View.VISIBLE);
                                ClubDetailsAdapter clubAdapter = new ClubDetailsAdapter(detailList, context, null, 1);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                                filterView.setLayoutManager(mLayoutManager);
                                filterView.setItemAnimator(new DefaultItemAnimator());
                                filterView.setAdapter(clubAdapter);
                            }
                        }

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

    private DetailsListClass[] loadExpDetails(ExpTrans[] expTrans) {

        DetailsListClass[] detailList = new DetailsListClass[expTrans.length];
        int pos = 0;

        for(ExpTrans transItem : expTrans) {
            DetailsListClass tempClass = new DetailsListClass();

            String month = AppGlobals.getMonthYear(Long.parseLong(transItem.getTimeStamp()));
            tempClass.setItems(transItem.getExpType(), transItem.getExpAmount(), month);
            tempClass.setExtraItems(transItem.getDescription(), transItem.getAdded_by(), "");
            detailList[pos++] = tempClass;
        }
        return detailList;
    }

    private DetailsListClass[] loadUserDetails(UserTransDetails[] userTrans) {

        DetailsListClass[] detailList = new DetailsListClass[userTrans.length];
        int pos = 0;

        appGlobals.chartList.clear();

        for(UserTransDetails userDet : userTrans) {
            DetailsListClass detailClass = new DetailsListClass();

            String date = AppGlobals.convertDateTime(Long.parseLong(userDet.getTimeStamp()));
            detailClass.setItems(userDet.getTrans_type(), userDet.getAmount(), date);
            detailClass.setExtraItems(userDet.getExtra(), userDet.getCashier_mob(), "");
            detailList[pos++] = detailClass;

            DetailsListClass tempClass = new DetailsListClass();
            tempClass.setItems(userDet.getTrans_type(), userDet.getAmount(), userDet.getTimeStamp());
            appGlobals.chartList.add(tempClass);
        }

        return detailList;
    }

    private DetailsListClass[] loadEmpDetails(EmpDetails[] empDet) {

        DetailsListClass[] detailList = new DetailsListClass[empDet.length];
        int pos = 0;

        for(EmpDetails empItem : empDet) {
            DetailsListClass tempClass = new DetailsListClass();

            tempClass.setItems(empItem.getEmpNum(), empItem.getSalary(), empItem.getMonth());
            tempClass.setExtraItems(empItem.getPayType(), empItem.getTimeStamp(), empItem.getCashier_mob());
            detailList[pos++] = tempClass;
        }
        return detailList;
    }
}

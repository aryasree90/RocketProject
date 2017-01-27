package rocket.club.com.rocketpoker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.ArrayList;
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
 * Created by Admin on 1/22/2017.
 */
public class ClubDetails extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;
    Button btnClear = null, showTransBtn;
    View.OnClickListener clickListener = null;
    MaterialBetterSpinner filter1, filter2, filter3;
    TextView label1, label2, label3, label;
    List<String> typeList1, typeList2, typeList3;

    final String TAG = "ClubDetails";
    public static final String ID = "id";
    public static final int EXP_ID = 1;
    public static final int SAL_ID = 2;
    int setId = EXP_ID;
    ProgressDialog progressDialog = null;
    ConnectionDetector connectionDetector = null;
    RecyclerView clubDetailsView = null;
    DetailsListClass[] detailList = null;
    ClubDetailsAdapter clubAdapter = null;
    public static final String FETCH_DET = AppGlobals.SERVER_URL + AppGlobals.FETCH_FROM_TABLE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.club_details, container, false);

        initializeWidgets(view);
        setOnClickListener();

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        filter1 = (MaterialBetterSpinner) view.findViewById(R.id.filter1);
        filter2 = (MaterialBetterSpinner) view.findViewById(R.id.filter2);
        filter3 = (MaterialBetterSpinner) view.findViewById(R.id.filter3);

        label = (TextView) view.findViewById(R.id.label);
        label1 = (TextView) view.findViewById(R.id.label1);
        label2 = (TextView) view.findViewById(R.id.label2);
        label3 = (TextView) view.findViewById(R.id.label3);

        showTransBtn = (Button) view.findViewById(R.id.showTransBtn);
        showTransBtn.setVisibility(View.VISIBLE);
        btnClear = (Button) view.findViewById(R.id.clearBtn);

        clubDetailsView = (RecyclerView) view.findViewById(R.id.clubList);

        typeList1 = new ArrayList<String>();
        typeList2 = new ArrayList<String>();
        typeList3 = new ArrayList<String>();

        typeList1.add("NA");
        typeList2.add("NA");
        typeList3.add("NA");

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(ID))
            setId = bundle.getInt(ID);

        if(setId == EXP_ID) {
            initExpPage();
        } else if(setId == SAL_ID) {
            initSalPage();
        }

        loadFilter();

        filter1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filterItem = typeList1.get(position);
                filter2.setText("");
                filter2.refreshDrawableState();

                filter3.setText("");
                filter3.refreshDrawableState();
                clubAdapter.getFilter().filter(filterItem);
            }
        });

        filter2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filterItem = typeList2.get(position);

                filter1.setText("");
                filter1.refreshDrawableState();

                filter3.setText("");
                filter3.refreshDrawableState();
                clubAdapter.getFilter().filter(filterItem);
            }
        });

        filter3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filterItem = typeList3.get(position);

                filter1.setText("");
                filter1.refreshDrawableState();

                filter2.setText("");
                filter2.refreshDrawableState();
                clubAdapter.getFilter().filter(filterItem);
            }
        });
    }

    private void initExpPage() {

        label.setText(getString(R.string.exp_list));
        label1.setText(getString(R.string.exp_label1));
        label2.setText(getString(R.string.exp_label2));
        label3.setText(getString(R.string.exp_label3));

        filter1.setHint(getString(R.string.exp_label1));
        filter2.setHint(getString(R.string.exp_label2));
        filter3.setHint(getString(R.string.exp_label3));

        initServerCall("rocketExpTrans");
    }

    private void initSalPage() {
        label.setText(getString(R.string.sal_list));
        label1.setText(getString(R.string.sal_label1));
        label2.setText(getString(R.string.sal_label2));
        label3.setText(getString(R.string.sal_label3));

        filter1.setHint(getString(R.string.sal_label1));
        filter2.setHint(getString(R.string.sal_label2));
        filter3.setHint(getString(R.string.sal_label3));

        initServerCall("emp_salary");
    }

    private void initServerCall(String table) {
        Map<String,String> map = new HashMap<String,String>();
        map.put("mobile", appGlobals.sharedPref.getLoginMobile());
        map.put("table", table);

        serverCall(map, "", FETCH_DET);
    }

    private void setOnClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.showTransBtn:

                        String trans = "";
                        if(setId == EXP_ID) {
                            trans = FilterTransaction.EXPENSE;
                        } else {
                            trans = FilterTransaction.SALARY;
                        }

                        Intent filterTrans = new Intent(context, FilterTransaction.class);
                        filterTrans.putExtra(FilterTransaction.FILTER_TRANS, trans);
                        startActivity(filterTrans);

                        break;
                    case R.id.clearBtn:
                        filter1.setText("");
                        filter2.setText("");
                        filter3.setText("");
                        clubAdapter.getFilter().filter("na");
                        break;
                }
            }
        };
        btnClear.setOnClickListener(clickListener);
        showTransBtn.setOnClickListener(clickListener);
    }

    private void loadFilter() {
        ArrayAdapter<String> expTypeAdapter1 = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, typeList1);

        ArrayAdapter<String> expTypeAdapter2 = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, typeList2);

        ArrayAdapter<String> expTypeAdapter3 = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, typeList3);

        filter1.setAdapter(expTypeAdapter1);
        filter2.setAdapter(expTypeAdapter2);
        filter3.setAdapter(expTypeAdapter3);
    }

    private void serverCall(final Map<String,String> params, final String task, String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(TextUtils.isEmpty(response)) {
                            return;
                        }
                        Gson gson = new Gson();

                        if(setId == EXP_ID) {
                            ExpTrans[] expTrans = gson.fromJson(response, ExpTrans[].class);
                            if(expTrans.length <= 0) {
                                return;
                            }
                            loadExpDet(expTrans);
                        } else if(setId == SAL_ID) {
                            EmpDetails[] empSalaryList = gson.fromJson(response, EmpDetails[].class);
                            if(empSalaryList.length <= 0) {
                                return;
                            }
                            loadSalaryDet(empSalaryList);
                        }

                        if(detailList.length > 0) {
                            clubAdapter = new ClubDetailsAdapter(detailList, context, ClubDetails.this, setId);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                            clubDetailsView.setLayoutManager(mLayoutManager);
                            clubDetailsView.setItemAnimator(new DefaultItemAnimator());
                            clubDetailsView.setAdapter(clubAdapter);
                        }

                        loadFilter();
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

    private void loadExpDet(ExpTrans[] expTrans) {

        detailList = new DetailsListClass[expTrans.length];
        int pos = 0;

        typeList1.clear();
        typeList2.clear();
        typeList3.clear();

        for(ExpTrans transItem : expTrans) {
            DetailsListClass tempClass = new DetailsListClass();
            String month = AppGlobals.getMonthYear(Long.parseLong(transItem.getTimeStamp()));
            tempClass.setItems(transItem.getExpType(), transItem.getExpAmount(), month);
            tempClass.setExtraItems(transItem.getDescription(), transItem.getAdded_by(), "");
            detailList[pos++] = tempClass;

            if(!typeList1.contains(tempClass.getItem1()))
                typeList1.add(tempClass.getItem1());
            if(!typeList2.contains(tempClass.getItem2()))
                typeList2.add(tempClass.getItem2());
            if(!typeList3.contains(month))
                typeList3.add(month);
        }
    }

    private void loadSalaryDet(EmpDetails[] empSalDet) {

        detailList = new DetailsListClass[empSalDet.length];
        int pos = 0;

        typeList1.clear();
        typeList2.clear();
        typeList3.clear();

        for(EmpDetails salItem : empSalDet) {
            DetailsListClass tempClass = new DetailsListClass();

            tempClass.setItems(salItem.getEmpId(), salItem.getSalary(), salItem.getMonth());
            tempClass.setExtraItems(salItem.getPayType(), salItem.getTimeStamp(), salItem.getCashier_mob());
            detailList[pos++] = tempClass;

            if(!typeList1.contains(tempClass.getItem1()))
                typeList1.add(tempClass.getItem1());
            if(!typeList2.contains(tempClass.getItem2()))
                typeList2.add(tempClass.getItem2());
            if(!typeList3.contains(tempClass.getItem3()))
                typeList3.add(tempClass.getItem3());
        }
    }
}

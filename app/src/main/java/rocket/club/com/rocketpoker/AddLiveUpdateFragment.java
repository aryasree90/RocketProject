package rocket.club.com.rocketpoker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.adapter.InfoListAdapter;
import rocket.club.com.rocketpoker.classes.InfoDetails;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public class AddLiveUpdateFragment extends Fragment {

    Context context;
    AppGlobals appGlobals = null;
    View.OnClickListener clickListener = null;
    ConnectionDetector connectionDetector = null;
    private static final String TAG = "AddLiveUpdateFragment";

    MaterialBetterSpinner updateTypeSpinner = null;
    ArrayAdapter<String> updateTypeAdapter = null;
    EditText header, text1, text2, text3, comments;
    Button save, clear;

    final String VALIDATION_URL = AppGlobals.SERVER_URL + "liveUpdate.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_live_update, container, false);

        initializeWidgets(view);
        setClickListener();

        return view;
    }

    private void initializeWidgets(View view) {

        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        String[] UPDATE_LIST = getResources().getStringArray(R.array.live_update_list);

        updateTypeAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, UPDATE_LIST);
        updateTypeSpinner = (MaterialBetterSpinner) view.findViewById(R.id.updateType);
        updateTypeSpinner.setAdapter(updateTypeAdapter);

        header = (EditText) view.findViewById(R.id.updateHeader);
        text1 = (EditText) view.findViewById(R.id.updateText1);
        text2 = (EditText) view.findViewById(R.id.updateText2);
        text3 = (EditText) view.findViewById(R.id.updateText3);
        comments = (EditText) view.findViewById(R.id.updateComments);

        save = (Button) view.findViewById(R.id.saveBtn);
        clear = (Button) view.findViewById(R.id.clearBtn);

    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.saveBtn:
                        Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.clearBtn:
                        Toast.makeText(context, "Clear", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        save.setOnClickListener(clickListener);
        clear.setOnClickListener(clickListener);
    }

}

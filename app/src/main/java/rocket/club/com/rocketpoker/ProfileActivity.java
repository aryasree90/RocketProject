package rocket.club.com.rocketpoker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public class ProfileActivity extends ActionBarActivity {

    Context context = null;
    AppGlobals appGlobals = null;
    Button gotoHome, clear;
    ImageView profileImage;
    EditText fullName, email, nickName, dob;
    ConnectionDetector connectionDetector = null;
    MaterialBetterSpinner gameTypeSpinner = null, genderSpinner = null;
    ArrayAdapter<String> gameTypeAdapter = null, genderAdapter = null;
    int year, month, day;

    View.OnClickListener clickListener = null;
    private static final int CAMERA = 0;
    private static final int GALLERY = 1;
    static final int DATE_DIALOG_ID = 1111;
    private static final String TAG = "Profile Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeWidgets();
        setClickListener();
    }

    private void initializeWidgets() {
        context = getApplicationContext();
        appGlobals = AppGlobals.getInstance();
        connectionDetector = new ConnectionDetector(context);

        gotoHome = (Button) findViewById(R.id.btn_toHome);
        clear = (Button) findViewById(R.id.btn_clear);
        fullName = (EditText) findViewById(R.id.fullName);
        email = (EditText) findViewById(R.id.email);
        nickName = (EditText) findViewById(R.id.nickName);
        dob = (EditText) findViewById(R.id.DOB);
        profileImage = (ImageView) findViewById(R.id.userProfilePic);

        String[] GAME_LIST = getResources().getStringArray(R.array.game_list);

        gameTypeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, GAME_LIST);
        gameTypeSpinner = (MaterialBetterSpinner)
                findViewById(R.id.gameType);
        gameTypeSpinner.setAdapter(gameTypeAdapter);

        String[] GENDER_LIST = getResources().getStringArray(R.array.gender_list);

        genderAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, GENDER_LIST);
        genderSpinner = (MaterialBetterSpinner)
                findViewById(R.id.gender);
        genderSpinner.setAdapter(genderAdapter);
    }

    private void setClickListener() {
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btn_toHome:
                        if(connectionDetector.isConnectingToInternet())
                            saveProfileDetails();
                        else
                            Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                        break;
                    case R.id.btn_clear:
                        clearFields();
                        break;
                    case R.id.userProfilePic:
                        Toast.makeText(context, "TODO", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.DOB:
                        showDialog(DATE_DIALOG_ID);
                        break;
                }
            }
        };

        gotoHome.setOnClickListener(clickListener);
        clear.setOnClickListener(clickListener);
        profileImage.setOnClickListener(clickListener);
        dob.setOnClickListener(clickListener);
    }

    private void saveProfileDetails() {

        final String userFullName = fullName.getText().toString();
        final String userEmail = email.getText().toString();
        final String userNickName = nickName.getText().toString();
        final String mobile = appGlobals.sharedPref.getLoginMobile();
        final String userGameType = gameTypeSpinner.getText().toString();
        final String userGender = genderSpinner.getText().toString();

        if(validateFields(userFullName, userEmail, userNickName, userGender, userGameType))
            updateProfile(userFullName, userEmail, userNickName, userGender, userGameType, mobile);

    }

    private boolean validateFields(String userFullName, String userEmail, String userNickName,
                                   String userGender, String userGameType) {

        boolean validation = true;

        if(userFullName.isEmpty() || userEmail.isEmpty() || userNickName.isEmpty() ||
                userGender.isEmpty() || userGameType.isEmpty()) {
            validation = false;
        }

        if(validation) {
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(userEmail);
            boolean matchFound = m.matches();
            if (!matchFound) {
                validation = false;
                Toast.makeText(context, getString(R.string.invalid_email), Toast.LENGTH_LONG).show();
            } else {
                Calendar cur = Calendar.getInstance();
                cur.setTimeInMillis(System.currentTimeMillis());

                int curYear = cur.get(Calendar.YEAR);
                int curMonth = cur.get(Calendar.MONTH) + 1;

                int yearDiff = curYear - year;
                if(yearDiff < 18)
                    validation = false;
                else if(yearDiff == 18) {
                    if(curMonth < month)
                        validation = false;
                }
            }
        } else {
            Toast.makeText(context, getString(R.string.enter_all), Toast.LENGTH_LONG).show();
        }
        return validation;
    }

    private void updateProfile(final String userFullName, final String userEmail,
                               final String userNickName, final String userGender, final String userGameType, final String mobile) {
        final String VALIDATION_URL = AppGlobals.SERVER_URL + "userProfile.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            Toast.makeText(context, getString(R.string.profile_update_success),Toast.LENGTH_LONG).show();
                            gotoHomeActivity();
                        }else{
                            Toast.makeText(context, getString(R.string.profile_update_failed),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, getString(R.string.profile_update_failed),Toast.LENGTH_LONG).show();
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("fullName", userFullName);
                map.put("email", userEmail);
                map.put("nickName", userNickName);
                map.put("gender", userGender);
                map.put("gameType", userGameType);
                map.put("mobile", mobile);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth + 1;
            day = selectedDay;

            // set selected date into textview
            dob.setText(new StringBuilder().append(month)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));
        }
    };

    private void gotoHomeActivity() {
        Intent profileIntent = new Intent(context, LandingActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(profileIntent);
        this.finish();
    }

    private void clearFields() {
        profileImage.setImageResource(R.drawable.default_profile);
        fullName.setText("");
        email.setText("");
        nickName.setText("");
    }
}
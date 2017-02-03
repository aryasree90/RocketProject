package rocket.club.com.rocketpoker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import rocket.club.com.rocketpoker.async.ContactAsync;
import rocket.club.com.rocketpoker.classes.ProfileDetailsClass;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;
import rocket.club.com.rocketpoker.utils.MultiSelectionSpinner;

public class ProfileActivity extends ActionBarActivity {

    Context context = null;
    AppGlobals appGlobals = null;
    Button gotoHome, clear;
    ImageView profileImage;
    EditText fullName, email, nickName, dob;
    TextView skipProfile, rocketId, emptyImage;
    ConnectionDetector connectionDetector = null;
    MaterialBetterSpinner gameTypeSpinner = null, genderSpinner = null;
    ArrayAdapter<String> gameTypeAdapter = null, genderAdapter = null;
    MultiSelectionSpinner gameTypeMultiSpinner = null;
    int year, month, day;
    String imagePath = "";
    final String VALIDATION_URL = AppGlobals.SERVER_URL + "userProfile.php";
    final String GAME_LIST_URL = AppGlobals.SERVER_URL + AppGlobals.FETCH_FROM_TABLE;

    Dialog dialog = null;
    View.OnClickListener clickListener = null;
    private static final int CAMERA = 0;
    private static final int GALLERY = 1;
    static final int DATE_DIALOG_ID = 1111;
    private static final String TAG = "Profile Activity";

    ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeWidgets();
        setClickListener();
        checkPermission();

        if(connectionDetector.isConnectingToInternet())
            fetchProfile();
        else
            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
    }

    private void initializeWidgets() {
        context = getApplicationContext();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        gotoHome = (Button) findViewById(R.id.btn_toHome);
        clear = (Button) findViewById(R.id.btn_clear);
        fullName = (EditText) findViewById(R.id.fullName);
        email = (EditText) findViewById(R.id.email);
        nickName = (EditText) findViewById(R.id.nickName);
        dob = (EditText) findViewById(R.id.DOB);
        profileImage = (ImageView) findViewById(R.id.userProfilePic);
        emptyImage = (TextView) findViewById(R.id.emptyImage);
        skipProfile = (TextView) findViewById(R.id.skipProfile);
        rocketId = (TextView) findViewById(R.id.rocketId);

        if(appGlobals.sharedPref.isInitLoad()) {
            skipProfile.setVisibility(View.GONE);
        }

//        String[] GAME_LIST = getResources().getStringArray(R.array.game_list);
        loadGameNameSpinner();

        String[] GENDER_LIST = getResources().getStringArray(R.array.gender_list);

        genderAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, GENDER_LIST);
        genderSpinner = (MaterialBetterSpinner)
                findViewById(R.id.gender);
        genderSpinner.setAdapter(genderAdapter);

        Map<String,String> map = new HashMap<String,String>();
        map.put("mobile", appGlobals.sharedPref.getLoginMobile());
        map.put("table", "rocketGames");

        serverCall(map, "", GAME_LIST_URL);
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
                            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                        break;
                    case R.id.btn_clear:
                        clearFields();
                        break;
                    /*case R.id.userProfilePic:
                        createMediaDialog();
                        break;*/
                    case R.id.emptyImage:
                        profileImage.setImageResource(R.drawable.default_profile);
                        imagePath = "";
                        break;
                    case R.id.DOB:
                        showDialog(DATE_DIALOG_ID);
                        break;
                    case R.id.skipProfile:
                        gotoHomeActivity();
                        break;
                    case R.id.btnCamera:

                        String imgFileName = appGlobals.sharedPref.getLoginMobile() + ".jpg";

                        imagePath = appGlobals.getRocketsPath(context) + "/" + imgFileName;

                        Uri mImageCaptureUri = Uri.fromFile(new File(imagePath));
                        File file = new File(imagePath);
                        try {
                            file.createNewFile();
                        } catch (IOException e) {

                        }

                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        startActivityForResult(cameraIntent, CAMERA);
                        resetMediaDialog();
                        break;
                    case R.id.btnGallery:
                    case R.id.userProfilePic:
                        /*Intent galleryIntent = new Intent();
                        galleryIntent.setType("image*//*");
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);*/
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(Intent.createChooser(galleryIntent, "Select File"),GALLERY);

                        resetMediaDialog();
                        break;
                }
            }
        };

        gotoHome.setOnClickListener(clickListener);
        clear.setOnClickListener(clickListener);
        profileImage.setOnClickListener(clickListener);
        dob.setOnClickListener(clickListener);
        skipProfile.setOnClickListener(clickListener);
        emptyImage.setOnClickListener(clickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        appGlobals.logClass.setLogMsg(TAG, "onActivityResult " + requestCode + " " + resultCode, LogClass.DEBUG_MSG);

        if (resultCode == Activity.RESULT_OK) {

            appGlobals.logClass.setLogMsg(TAG, "onActivityResult Reached in ", LogClass.DEBUG_MSG);


            if(requestCode == GALLERY) {
                Uri selectedImage = data.getData();

                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);

                if(cursor == null)
                    return;

                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath = cursor.getString(columnIndex);
                cursor.close();

                appGlobals.logClass.setLogMsg(TAG, "onActivityResult Gallery " + imagePath, LogClass.DEBUG_MSG);

            } else if(requestCode == CAMERA) {
                appGlobals.logClass.setLogMsg(TAG, "onActivityResult Camera " + imagePath, LogClass.DEBUG_MSG);
            }

            if(!TextUtils.isEmpty(imagePath) && new File(imagePath).exists()) {

                new AsyncTask<String, Void, Bitmap>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = appGlobals.showDialog(ProfileActivity.this, "Load Image");
                    }

                    @Override
                    protected Bitmap doInBackground(String... params) {

                        String imgFileName = appGlobals.getRocketsPath(context) + "/" +
                                appGlobals.sharedPref.getLoginMobile() + ".jpg";

                        File curFile = new File(imgFileName);
                        if(curFile.exists()) {
                            curFile.delete();
                        }

                        if(appGlobals.compressImage(imagePath, imgFileName)) {
                            imagePath = imgFileName;
                            Bitmap bm = BitmapFactory.decodeFile(imagePath);
                            return bm;
//                            return Uri.fromFile(new File(imagePath));
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        if(bitmap != null)
                            profileImage.setImageBitmap(bitmap);
//                            profileImage.setImageURI(uri);

                        appGlobals.cancelDialog(progressDialog);
                    }
                }.execute(imagePath);
            }
        }
    }

    private void saveProfileDetails() {
        String tempFullName = fullName.getText().toString();
        final String userEmail = email.getText().toString();
        final String userNickName = nickName.getText().toString();
        final String userGameType = gameTypeMultiSpinner.getSelectedItemsAsString();
        final String userGender = genderSpinner.getText().toString();
        final String userDob = dob.getText().toString();
        final String userFullName = tempFullName.substring(0, 1).toUpperCase() +
                tempFullName.substring(1);

        String userImage = "", thumbImage = "";

        if(!TextUtils.isEmpty(imagePath) && new File(imagePath).exists()) {
            userImage = appGlobals.convertImageToBase64(imagePath);
            thumbImage = appGlobals.thumbnailImage(imagePath);
        }


        if(validateFields(userFullName, userEmail, userNickName, userGender, userGameType))
            updateProfile(userFullName, userEmail, userNickName, userGender, userGameType, userDob,
                    userImage, thumbImage);

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
                appGlobals.toastMsg(context, getString(R.string.invalid_email), appGlobals.LENGTH_LONG);
            } else {
                Calendar cur = Calendar.getInstance();
                cur.setTimeInMillis(System.currentTimeMillis());

                int curYear = cur.get(Calendar.YEAR);
                int curMonth = cur.get(Calendar.MONTH) + 1;

                int yearDiff = curYear - year;
                boolean isAdult = true;
                if(yearDiff < 18)
                    isAdult = false;
                else if(yearDiff == 18) {
                    if(curMonth < month)
                        isAdult = false;
                }

                if(!isAdult) {
                    validation = false;
                    appGlobals.toastMsg(context, getString(R.string.below_age), appGlobals.LENGTH_LONG);
                }
            }
        } else {
            appGlobals.toastMsg(context, getString(R.string.enter_all), appGlobals.LENGTH_LONG);
        }
        return validation;
    }

    private void updateProfile(final String userFullName, final String userEmail,
                               final String userNickName, final String userGender,
                               final String userGameType, final String userDob,
                               final String userImage, String thumbImage) {

        progressDialog = appGlobals.showDialog(this, getString(R.string.save_profile));

        String imageName = appGlobals.sharedPref.getLoginMobile() + appGlobals.IMG_FILE_EXTENSION;
        String thumbName = appGlobals.thumbImageName(imageName);

        Map<String,String> map = new HashMap<String,String>();
        map.put("fullName", userFullName);
        map.put("email", userEmail);
        map.put("nickName", userNickName);
        map.put("gender", userGender);
        map.put("gameType", userGameType);
        map.put("dob", userDob);
        map.put("image", userImage);
        map.put("thumbImage", thumbImage);
        map.put("mobile", appGlobals.sharedPref.getLoginMobile());
        map.put("task", appGlobals.UPDATE_PROFILE);
        map.put("imageName", imageName);
        map.put("thumbName", thumbName);

        serverCall(map, appGlobals.UPDATE_PROFILE, VALIDATION_URL);
    }

    private void fetchProfile() {

        progressDialog = appGlobals.showDialog(this, getString(R.string.fetch_profile));

        Map<String,String> map = new HashMap<String,String>();
        map.put("mobile", appGlobals.sharedPref.getLoginMobile());
        map.put("task", appGlobals.FETCH_PROFILE);

        serverCall(map, appGlobals.FETCH_PROFILE, VALIDATION_URL);
    }

    private void serverCall(final Map<String,String> params, final String task, String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(task.equals(appGlobals.UPDATE_PROFILE)) {
                            if (response.trim().equals("success")) {

                                String uName = fullName.getText().toString();
                                appGlobals.sharedPref.setUserName(uName);

                                appGlobals.toastMsg(context, getString(R.string.profile_update_success), appGlobals.LENGTH_LONG);
                                gotoHomeActivity();
                            } else {
                                appGlobals.toastMsg(context, getString(R.string.profile_update_failed), appGlobals.LENGTH_LONG);
                            }
                        } else if(task.equals(appGlobals.FETCH_PROFILE)) {
                            if (!response.trim().isEmpty()) {
                                setProfileDetails(response);
                            }
                        } else if(TextUtils.isEmpty(task)) {
                            JsonParser parser = new JsonParser();
                            JsonArray jsonArr = parser.parse(response).getAsJsonArray();

                            DBHelper db = new DBHelper(context);
                            for(JsonElement jsonElem : jsonArr) {
                                JsonObject jsonObj = jsonElem.getAsJsonObject();
                                String gameName = jsonObj.get("gameName").getAsString();

                                db.insertNewGameDetails(gameName);
                            }
                            loadGameNameSpinner();
                        }
                        appGlobals.cancelDialog(progressDialog);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = "";
                        appGlobals.cancelDialog(progressDialog);
                        if(task.equals(appGlobals.UPDATE_PROFILE))
                            msg = getString(R.string.profile_update_failed);
                        else if(task.equals(appGlobals.FETCH_PROFILE))
                            msg = getString(R.string.profile_fetch_error);

                        appGlobals.toastMsg(context, msg, appGlobals.LENGTH_LONG);
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

    private void loadGameNameSpinner() {
        DBHelper db = new DBHelper(context);
        String[] GAME_LIST = db.getRocketsGameList();

        gameTypeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, GAME_LIST);
        /*gameTypeSpinner = (MaterialBetterSpinner)
                findViewById(R.id.gameType);
        gameTypeSpinner.setAdapter(gameTypeAdapter);*/

        gameTypeMultiSpinner = (MultiSelectionSpinner)
                findViewById(R.id.gameType);

        if(GAME_LIST.length > 0)
            gameTypeMultiSpinner.setItems(GAME_LIST);

    }

    private void setProfileDetails(String response) {

        Gson profileJson = new Gson();
        ProfileDetailsClass profileDetails = profileJson.fromJson(response, ProfileDetailsClass[].class)[0];

        rocketId.setText(getString(R.string.rocket_id) + " : " + profileDetails.getUserId());
        appGlobals.sharedPref.setRocketId(profileDetails.getUserId());
        fullName.setText(profileDetails.getName());
        fullName.setEnabled(true);
        appGlobals.sharedPref.setUserName(profileDetails.getName());
        email.setText(profileDetails.getEmail());
        nickName.setText(profileDetails.getNickname());
        dob.setText(profileDetails.getDob());
//        gameTypeSpinner.setText(profileDetails.getGametype());
        String selected[] = profileDetails.getGametype().split(", ");
        gameTypeMultiSpinner.setSelection(selected);

        genderSpinner.setText(profileDetails.getGender());

        String imgFileName = appGlobals.sharedPref.getLoginMobile() + ".jpg";
        String imgPath = appGlobals.getRocketsPath(context) + "/" + imgFileName;
        File imageFile = new File(imgPath);

        if(!imageFile.exists() && !TextUtils.isEmpty(profileDetails.getUser_pic())) {
            HashMap<String, String> params = new HashMap<>();
            params.put("mobile", appGlobals.sharedPref.getLoginMobile());
            params.put("frndMob", appGlobals.sharedPref.getLoginMobile());

            appGlobals.searchUpdatedImage(context, params, imgPath, profileImage);
        }
        if(imageFile.exists())
            profileImage.setImageURI(Uri.fromFile(imageFile));
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
                    .append("-").append(day).append("-").append(year));
        }
    };

    private void gotoHomeActivity() {
        Intent profileIntent = new Intent(context, LandingActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(profileIntent);
        this.finish();
    }

    private void clearFields() {
        //profileImage.setImageResource(R.drawable.default_profile);
        fullName.setText("");
        email.setText("");
        nickName.setText("");
        dob.setText("");
        genderSpinner.setText("");
//        gameTypeSpinner.setText("");
    }

    private void createMediaDialog() {
        dialog=new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.media_option_layout);

        dialog.setTitle(getString(R.string.select_media));

        Button btnCamera = (Button) dialog.findViewById(R.id.btnCamera);
        Button btnGallery = (Button) dialog.findViewById(R.id.btnGallery);

        btnCamera.setOnClickListener(clickListener);
        btnGallery.setOnClickListener(clickListener);

        dialog.show();

        appGlobals.setDialogLayoutParams(dialog, context, false, true);
    }

    private void resetMediaDialog() {
        if(dialog != null && dialog.isShowing())
            dialog.cancel();
    }


    private void checkPermission() {
        if(!appGlobals.checkLocationPermission(context, AppGlobals.READ_EXTERNAL_STORAGE)
                || !appGlobals.checkLocationPermission(context, AppGlobals.WRITE_EXTERNAL_STORAGE)
                || !appGlobals.checkLocationPermission(context, AppGlobals.READ_CONTACTS)) {
            ActivityCompat.requestPermissions(ProfileActivity.this,
                    new String[]{AppGlobals.READ_EXTERNAL_STORAGE,
                            AppGlobals.WRITE_EXTERNAL_STORAGE, AppGlobals.READ_CONTACTS},
                    AppGlobals.REQUEST_EXTERNAL_STORAGE);
        } else {
            startContactSync();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case AppGlobals.REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    appGlobals.logClass.setLogMsg(TAG, "Permission Granted", LogClass.DEBUG_MSG);
                    startContactSync();
                }
                return;
            }
        }
    }

    private void startContactSync() {
        if(!appGlobals.sharedPref.isContactInit()) {
            ContactAsync contactAsync = new ContactAsync(context);
            contactAsync.execute();
        }
    }
}
package rocket.club.com.rocketpoker;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gcm.GCMRegistrar;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

/**
 * Created by Admin on 11/23/2016.
 */
public class AddEventFragment extends Fragment {

    Context context = null;
    AppGlobals appGlobals = null;
    View.OnClickListener onClickListener = null;

    String activityType = "", imagePath = "";
    final String TAG = "AddEventFragment";
    ConnectionDetector connectionDetector = null;
    final String VALIDATION_URL = AppGlobals.SERVER_URL + AppGlobals.EDITORS_URL;

    EditText headerText, summaryText;
    ImageView eventImage;
    Button save, clear;
    ProgressDialog progressDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_event_fragment, container, false);

        initializeWidgets(view);
        setOnClickListener();

        return view;
    }

    private void initializeWidgets(View view) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);
        connectionDetector = new ConnectionDetector(context);

        Bundle bundle = getArguments();
        activityType = bundle.getString(EventDetailActivity.ACTIVITY_TYPE);

        eventImage = (ImageView) view.findViewById(R.id.eventImage);
        headerText = (EditText) view.findViewById(R.id.headerText);
        summaryText = (EditText) view.findViewById(R.id.summaryText);

        TextView actTypeText = (TextView) view.findViewById(R.id.actType);

        if (activityType.equals(AppGlobals.EVENT_INFO)) {
            headerText.setHint(getString(R.string.eventHeader));
            summaryText.setHint(getString(R.string.eventSummary));
            actTypeText.setText(getString(R.string.addEvent));
        } else {
            headerText.setHint(getString(R.string.serviceHeader));
            summaryText.setHint(getString(R.string.serviceSummary));
            actTypeText.setText(getString(R.string.addService));
        }

        save = (Button) view.findViewById(R.id.saveBtn);
        clear = (Button) view.findViewById(R.id.clearBtn);
    }

    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.saveBtn:
                        if(connectionDetector.isConnectingToInternet()) {

                            String header = headerText.getText().toString();
                            String summary = summaryText.getText().toString();

                            if(TextUtils.isEmpty(header) || TextUtils.isEmpty(summary)) {
                                appGlobals.toastMsg(context, getString(R.string.enter_all), appGlobals.LENGTH_LONG);
                                return;
                            }

                            String timeStamp = String.valueOf(System.currentTimeMillis());

                            String message = "";
                            if (activityType.equals(AppGlobals.EVENT_INFO)) {
                                message = getString(R.string.save_new_event);
                            } else {
                                message = getString(R.string.save_new_service);
                            }
                            progressDialog = appGlobals.showDialog(context, message);

                            String userImage = "";

                            if(!TextUtils.isEmpty(imagePath) && new File(imagePath).exists())
                                userImage = appGlobals.convertImageToBase64(imagePath);

                            String imageName = header.replaceAll(" ", "") + "_" + timeStamp + appGlobals.IMG_FILE_EXTENSION;

                            Map<String,String> map = new HashMap<String,String>();
                            map.put("image", userImage);
                            map.put("header", header);
                            map.put("summary", summary);
                            map.put("timeStamp", timeStamp);
                            map.put("imageName", imageName);
                            map.put("task", activityType);
                            map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                            serverCall(map);
                        } else {
                            appGlobals.toastMsg(context, getString(R.string.no_internet), appGlobals.LENGTH_LONG);
                        }
                        break;
                    case R.id.clearBtn:
                        clearFields();
                        break;
                    case R.id.eventImage:
/*                        Intent galleryIntent = new Intent();
                        galleryIntent.setType("image*//*");
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);*/
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(Intent.createChooser(galleryIntent, "Select File"), 1);
                        break;
                }
            }
        };
        save.setOnClickListener(onClickListener);
        clear.setOnClickListener(onClickListener);
        eventImage.setOnClickListener(onClickListener);
    }

    private void clearFields() {
        headerText.setText("");
        summaryText.setText("");
        eventImage.setImageResource(R.drawable.upload_images);
    }

    private void serverCall(final Map<String,String> params) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Received " + response, LogClass.INFO_MSG);
                        String msg = "";
                        if (activityType.equals(AppGlobals.EVENT_INFO)) {
                            msg = getString(R.string.event_success);
                        } else {
                            msg = getString(R.string.service_success);
                        }

                        appGlobals.toastMsg(context, msg, appGlobals.LENGTH_LONG);
                        CommonUtilities.getRocketsInfo(context, response);
                        clearFields();
                        appGlobals.cancelDialog(progressDialog);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
                        String msg = "";
                        if (activityType.equals(AppGlobals.EVENT_INFO)) {
                            msg = getString(R.string.event_failure);
                        } else {
                            msg = getString(R.string.service_failure);
                        }

                        appGlobals.toastMsg(context, msg, appGlobals.LENGTH_LONG);
                        appGlobals.cancelDialog(progressDialog);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        appGlobals.logClass.setLogMsg(TAG, "onActivityResult " + requestCode + " " + resultCode, LogClass.DEBUG_MSG);

        if (resultCode == Activity.RESULT_OK) {
            appGlobals.logClass.setLogMsg(TAG, "onActivityResult Reached in ", LogClass.DEBUG_MSG);

            if(requestCode == 1) {
                Uri selectedImage = data.getData();

                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = context.getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);

                if(cursor == null)
                    return;

                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath = cursor.getString(columnIndex);
                cursor.close();

                appGlobals.logClass.setLogMsg(TAG, "onActivityResult Gallery " + imagePath, LogClass.DEBUG_MSG);
            }

            if(!TextUtils.isEmpty(imagePath) && new File(imagePath).exists()) {

                new AsyncTask<String, Void, Bitmap>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = appGlobals.showDialog(context, "Load Image");
                    }

                    @Override
                    protected Bitmap doInBackground(String... params) {

                        String type = "";
                        if (activityType.equals(AppGlobals.EVENT_INFO)) {
                            type = appGlobals.EVENTS;
                        } else {
                            type = appGlobals.SERVICES;
                        }

                        String imgFileName = appGlobals.getRocketsPath(context) + "/" +
                                AppGlobals.CLUB_INFO + "_" + type + appGlobals.IMG_FILE_EXTENSION;

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
                            eventImage.setImageBitmap(bitmap);
//                            profileImage.setImageURI(uri);

                        appGlobals.cancelDialog(progressDialog);
                    }
                }.execute(imagePath);
            }
        }
    }

}

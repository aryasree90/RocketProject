package rocket.club.com.rocketpoker;

import android.*;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.LocationClass;
import rocket.club.com.rocketpoker.database.DBHelper;
import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LogClass;

public class LocationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context context = null;
    AppGlobals appGlobals = null;

    private GoogleMap googleMap = null;
    private GoogleApiClient mLocationClient = null;

    private static final String TAG = "Location Fragment";

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(1000 * 60) // 1 minute
            .setFastestInterval(16) // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location, container, false);
        initializeWidgets(view);
        return view;
    }


    private void initializeWidgets(View v) {
        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);

        serverCall();

        SupportMapFragment fm = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        fm.getMapAsync(this);
   }

    @Override
    public void onMapReady(GoogleMap gMap) {

        // Getting GoogleMap object from the fragment
        googleMap = gMap;

        if(!AppGlobals.checkLocationPermission(context, AppGlobals.ACCESS_COARSE_LOC))
            return;
        if(!AppGlobals.checkLocationPermission(context, AppGlobals.ACCESS_FINE_LOC))
            return;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);

//        startLocationShare();     //TODO : not used as of now
    }

    private void serverCall() {

        final String VALIDATION_URL = AppGlobals.SERVER_URL + "locationDetails.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VALIDATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        appGlobals.logClass.setLogMsg(TAG, "Received Location Details from server" + response, LogClass.DEBUG_MSG);

                        LoadNewMarker newMarker = new LoadNewMarker(response,
                                LocationFragment.this);
                        newMarker.execute();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        appGlobals.logClass.setLogMsg(TAG, error.toString(), LogClass.ERROR_MSG);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("mobile", appGlobals.sharedPref.getLoginMobile());
                map.put("task", appGlobals.FETCH_FRIENDS_LOCATION);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    class LoadNewMarker extends AsyncTask<Void, String, Void> {

        String respString;
        LocationFragment mapActivity;
        LatLngBounds.Builder builder = null;

        public LoadNewMarker(String respString, LocationFragment mapActivity) {

            this.respString = respString;
            this.mapActivity = mapActivity;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                JSONArray arr = new JSONArray(respString);
                int size = arr.length();

                for(int i=0; i<size; i++) {

                    JSONObject obj = arr.getJSONObject(i);
                    String regMob = obj.getString("reg_mob");

                    DBHelper db = new DBHelper(context);
                    ContactClass contact = db.getContacts(regMob);

                    publishProgress(obj.getString("user_location"), contact.getContactName());
                }

            } catch (Exception e) {
                appGlobals.logClass.setLogMsg(TAG, "Exception in AsyncTask " + e.toString(), LogClass.ERROR_MSG);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            try {
                Gson gson = new Gson();
                LocationClass locClass = gson.fromJson(values[0], LocationClass.class);

                if(mapActivity != null && mapActivity.isVisible()) {
                    LatLng loc = new LatLng(Double.valueOf(locClass.getLat()), Double.valueOf(locClass.getLng()));

                    if(builder == null)
                        builder = new LatLngBounds.Builder();

                    builder.include(loc);
                    String snippet = values[1] + " " + AppGlobals.convertTime(locClass.getTimeStamp());
                    mapActivity.drawMarker(loc, locClass.getLoc_name(), snippet);
                }
            } catch(Exception e) {
                appGlobals.logClass.setLogMsg(TAG, "Exception in Progress Update " + e.toString(), LogClass.ERROR_MSG);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (builder != null) {
                mapActivity.moveCamera(builder);
            }

        }
    }

    private void moveCamera(LatLngBounds.Builder builder) {
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);
    }

    private void drawMarker(final LatLng markerLatLng, String locName, String snippet) {

/*        if (dismissDialog)
            dismissProgressDialog();*/

        googleMap.addMarker(new MarkerOptions()
                .position(markerLatLng)
                .title(locName));

    }

    private void startLocationShare() {
        mLocationClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        mLocationClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(!AppGlobals.checkLocationPermission(context, AppGlobals.ACCESS_COARSE_LOC))
            return;
        if(!AppGlobals.checkLocationPermission(context, AppGlobals.ACCESS_FINE_LOC))
            return;

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mLocationClient, REQUEST, this); // LocationListener
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMyLocationChange(Location location) {
        appGlobals.logClass.setLogMsg(TAG, "Reached onMyLocationChange " + location, LogClass.DEBUG_MSG);
    }


    @Override
    public void onStop() {
        super.onStop();

        if(mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }
}
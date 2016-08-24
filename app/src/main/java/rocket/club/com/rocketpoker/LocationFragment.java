package rocket.club.com.rocketpoker;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.HashMap;
import java.util.Map;

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

        if(!AppGlobals.checkLocationPermission(context))
            return;

        // Getting GoogleMap object from the fragment
        googleMap = gMap;

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
                        appGlobals.logClass.setLogMsg(TAG, "Received Location Details from server", LogClass.DEBUG_MSG);
                        appGlobals.logClass.setLogMsg(TAG, response, LogClass.DEBUG_MSG);
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

    private void startLocationShare() {
        mLocationClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        mLocationClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(AppGlobals.checkLocationPermission(context)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mLocationClient, REQUEST, this); // LocationListener
        }
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
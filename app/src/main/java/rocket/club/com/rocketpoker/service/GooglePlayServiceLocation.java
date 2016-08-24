package rocket.club.com.rocketpoker.service;

import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LocationUtils;
import rocket.club.com.rocketpoker.utils.LogClass;

public class GooglePlayServiceLocation implements LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationService {

	private Context context;
	// A request to connect to Location Services
	private LocationRequest mLocationRequest;

	private LocationResult locationResult;

	// Stores the current instantiation of the location client in this object
	private GoogleApiClient mLocationClient;
	//private LocationClient mLocationClient;
	private AppGlobals appGlobals = null;
	private final String TAG = "Google Play Service Location";

	/*
	 * Note if updates have been turned on. Starts out as "false"; is set to
	 * "true" in the method handleRequestSuccess of LocationUpdateReceiver.
	 */
	// boolean mUpdatesRequested = false;

	private static GooglePlayServiceLocation googlePlayServiceLocation = null;

	public static GooglePlayServiceLocation getInstance() {
		if (googlePlayServiceLocation == null)
			googlePlayServiceLocation = new GooglePlayServiceLocation();
		return googlePlayServiceLocation;
	}

	private GooglePlayServiceLocation() {

		// Create a new global location parameters object
		mLocationRequest = new LocationRequest();//LocationRequest.create();
		appGlobals = AppGlobals.getInstance(AppGlobals.appContext);
		/*
		 * Set the update interval
		 */
		mLocationRequest
				.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the interval ceiling to one minute
		mLocationRequest
				.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
		// Note that location updates are off until the user turns them on
		// mUpdatesRequested = true;

	}

	public boolean getLocation(Context context, LocationResult locationResult) {
		appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient:: getLocation() : Entry", LogClass.DEBUG_MSG);
		this.context = context;
		this.locationResult = locationResult;
		if (mLocationClient == null)
			mLocationClient = new GoogleApiClient.Builder(context)
					        .addApi(LocationServices.API)
					        .addConnectionCallbacks(this)
					        .addOnConnectionFailedListener(this)
					        .build();
			//mLocationClient = new LocationClient(context, this, this);

		stopPeriodicUpdates();
		mLocationClient.connect();
		appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::getLocation():mLocationClient connect: calling startPeriodicUpdates()", LogClass.DEBUG_MSG);
		// startPeriodicUpdates();
		// getLocation();
		return false;
	}

	/**
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	public boolean servicesConnected(Context context) {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		// If Google Play services is available
		appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::servicesConnected():: GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) return resultCode: "
							+ resultCode, LogClass.DEBUG_MSG);
		if (ConnectionResult.SUCCESS == resultCode) {
			appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::servicesConnected():: "
								+ "  ConnectionResult.SUCCESS: ", LogClass.DEBUG_MSG);

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::servicesConnected():: "
								+ "else condition ", LogClass.DEBUG_MSG);
			return false;
		}
	}

	public void getLocation() {

		appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::getLocation():: "
							+ "entry ", LogClass.DEBUG_MSG);
		// If Google Play Services is available
		if (servicesConnected(context)) {
			appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::getLocation():: "
								+ "servicesConnected:: true ", LogClass.DEBUG_MSG);
			if(AppGlobals.checkLocationPermission(context)) {
				// Get the current location
				Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);//mLocationClient.getLastLocation();

				appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::getLocation():: "
						+ "currentLocation::" + currentLocation, LogClass.DEBUG_MSG);
				if (currentLocation != null) {
				}
			}
		}
	}

	/**
	 * Invoked by the "Stop Updates" button Sends a request to remove location
	 * updates request them.
	 *
	 */
	public void stopLocationUpdates() {
		appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::stopUpdates():: ENTRY", LogClass.DEBUG_MSG);

		appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::stopUpdates::servicesConnected:: true", LogClass.DEBUG_MSG);

		appGlobals.logClass.setLogMsg(TAG, "Reached stopLocationUpdates of GooglePlayServiceLocation " + AppGlobals.getCurrentTime(), LogClass.DEBUG_MSG);

		stopPeriodicUpdates();
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle bundle) {
		appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::onConnected():: mUpdatesRequested:: ", LogClass.DEBUG_MSG);

		appGlobals.logClass.setLogMsg(TAG, "Reached onConnected of GooglePlayServiceLocation " + AppGlobals.getCurrentTime(), LogClass.DEBUG_MSG);

		startPeriodicUpdates();
	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	/*@Override
	public void onDisconnected() {
		if (Engine.IS_INTERNAL_RELEASE) {// This log is not print now.
			Engine.engObj.eLogger
					.info("GooglePlayServiceLocationClient::onDisconnected()::ENTRY:: ");
			
			Engine.engObj.eLogger.info("Reached onDisconntected of GooglePlayServiceLocation " + ConfigurationReader.getCurrentTime());	
		}
	}*/

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::onConnectionFailed()::connectionResult:: "
							+ connectionResult, LogClass.DEBUG_MSG);
		if (connectionResult.hasResolution()) {
			try {
				appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::onConnectionFailed()::connectionResult.hasResolution() block:: ", LogClass.DEBUG_MSG);

				connectionResult.startResolutionForResult(null,
						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::onConnectionFailed()::IntentSender.SendIntentException:: "
									+ e, LogClass.ERROR_MSG);

			}
		} else {
			appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::onConnectionFailed()::has no resolution:: ", LogClass.DEBUG_MSG);

		}
	}

	/**
	 * Report location updates to the UI.
	 * 
	 * @param location
	 *            The updated location.
	 */
	@Override
	public void onLocationChanged(Location location) {
		try {
			appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::onLocationChanged()::location: "
								+ location, LogClass.DEBUG_MSG);
			appGlobals.logClass.setLogMsg(TAG, "Reached onLocationChanged of GooglePlayServiceLocation " + AppGlobals.getCurrentTime(), LogClass.DEBUG_MSG);

			if (location != null) {
				stopPeriodicUpdates();
				appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::onLocationChanged()::location:: latitude:"
						+ location.getLatitude()
						+ ",longitude: "
						+ location.getLongitude(), LogClass.DEBUG_MSG);
				// locationResult.gotLocation(location);
				findLocationNameAsync(location);

			}
		} catch (Exception ex) {
			appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::onLocationChanged():: "
					+ ex, LogClass.ERROR_MSG);
		}

	}

	/**
	 * In response to a request to start updates, send a request to Location
	 * Services
	 */
	private void startPeriodicUpdates() {
		appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::startPeriodicUpdates()::ENTRY", LogClass.DEBUG_MSG);
		if (mLocationClient != null) {
			if(AppGlobals.checkLocationPermission(context)) {
				appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient Permission allowed", LogClass.DEBUG_MSG);
				LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
				appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient FusedLocationApi triggered", LogClass.DEBUG_MSG);
			} else {
				appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient Permission is not allowed", LogClass.DEBUG_MSG);
			}
		} else {
			appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient mLocationClient is null", LogClass.DEBUG_MSG);
		}
		//mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	/**
	 * In response to a request to stop updates, send a request to Location
	 * Services
	 */
	private void stopPeriodicUpdates() {
		try {
			appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::stopPeriodicUpdates()::ENTRY", LogClass.DEBUG_MSG);

			if (mLocationClient != null && mLocationClient.isConnected()) {
				LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
				//mLocationClient.removeLocationUpdates(this);
				mLocationClient.disconnect();
			}
		} catch (Exception ex) {
			appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::stopPeriodicUpdates()::Exception: "
								+ ex, LogClass.ERROR_MSG);
		}
	}

	public Location getLastKnownLocation() {
		if (mLocationClient != null) {
			if(AppGlobals.checkLocationPermission(context))
				return LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
		}
		return null;
	}

	private void findLocationNameAsync(final Location location) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					locationResult.gotLocation(location);
				} catch (Exception ex) {
					appGlobals.logClass.setLogMsg(TAG, "GooglePlayServiceLocationClient::findLocationNameAsync():: "
							+ ex, LogClass.ERROR_MSG);
				}
				return null;
			}
		}.execute();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
}

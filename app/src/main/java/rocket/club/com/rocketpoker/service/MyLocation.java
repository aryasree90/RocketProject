package rocket.club.com.rocketpoker.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import rocket.club.com.rocketpoker.utils.AppGlobals;
import rocket.club.com.rocketpoker.utils.LocationUtils;
import rocket.club.com.rocketpoker.utils.LogClass;

public class MyLocation implements LocationService{

	LocationManager lm = null;
	boolean gps_enabled = false;
	boolean network_enabled = false;
	LocationListener locationListenerGps = null;
	LocationListener locationListenerNetwork = null;
	LocationResult locationResult;

	AppGlobals appGlobals = null;
	private final String TAG = "My Location";

	 private static MyLocation myLocation=null;
	    
	    public static MyLocation getInstance(){
	    	if(myLocation==null)
	    		myLocation=new MyLocation();
	    	return myLocation;
	    }
	    
	public boolean getLocation(Context context, LocationResult result) {
		this.locationResult = result;
		// use LocationResult callback class to pass location value from
		// MyLocation to user code.
		AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::getLocation() : Entry", LogClass.DEBUG_MSG);
		if (lm == null) {
			lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if (lm == null) {
				return false;
			}
		}

		try {
			network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (network_enabled) {
				AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::getLocation() : Network provider is enabled", LogClass.DEBUG_MSG);

				// create listener, if it is null
				if (locationListenerNetwork == null) {
					locationListenerNetwork = new LocationListenerNetwork();
				}else{
					if(!AppGlobals.checkLocationPermission(context, AppGlobals.ACCESS_COARSE_LOC))
						return false;
					if(!AppGlobals.checkLocationPermission(context, AppGlobals.ACCESS_FINE_LOC))
						return false;

					lm.removeUpdates(locationListenerNetwork);
				}
				
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS, 0, locationListenerNetwork);
				return true;
			}
		} catch (Exception ex) {
			AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::getLocation() : Exception in checking is network ProviderEnabled : "
								+ ex, LogClass.ERROR_MSG);
		}

		// exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			// don't start listeners if no provider is enabled
			if (gps_enabled) {
				AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::getLocation() : GPS is enabled", LogClass.DEBUG_MSG);

				// create listener, if it is null
				if (locationListenerGps == null) {
					locationListenerGps = new LocationListenerGps();
				}else{
					lm.removeUpdates(locationListenerGps);					
				}
				
				
				
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS, 0, locationListenerGps);
				return true;
			}
		} catch (Exception ex) {
			AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::getLocation() : Exception in checking is GPS ProviderEnabled : "
								+ ex, LogClass.ERROR_MSG);
		}
		AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::getLocation() : GPS & network both are not enabled", LogClass.DEBUG_MSG);
		return false;
	}

	public void stopLocationUpdates() {
		/*
		 * if(timer1!=null) timer1.cancel();
		 */
		if (lm != null) {
			if(!AppGlobals.checkLocationPermission(AppGlobals.appContext, AppGlobals.ACCESS_COARSE_LOC))
				return;
			if(!AppGlobals.checkLocationPermission(AppGlobals.appContext, AppGlobals.ACCESS_FINE_LOC))
				return;

			if (locationListenerGps != null) {
				lm.removeUpdates(locationListenerGps);
				locationListenerGps = null;
			}
			if (locationListenerNetwork != null) {
				lm.removeUpdates(locationListenerNetwork);
				locationListenerNetwork = null;
			}
			lm = null;
		}
	}

	

	class LocationListenerNetwork implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			
			try{
				AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::onLocationChanged() : locationListenerNetwork ::   "+location, LogClass.DEBUG_MSG);
				AppGlobals.appGlobals.logClass.setLogMsg(TAG, "Reached onLocationChanged of network listener " + AppGlobals.getCurrentTime(), LogClass.DEBUG_MSG);

				if(location==null)return;
				if(!AppGlobals.checkLocationPermission(AppGlobals.appContext, AppGlobals.ACCESS_COARSE_LOC))
					return;
				if(!AppGlobals.checkLocationPermission(AppGlobals.appContext, AppGlobals.ACCESS_FINE_LOC))
					return;

				lm.removeUpdates(this);
				AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::onLocationChanged() : locationListenerNetwork ::  latitude "
					+ location.getLatitude()
					+ " longitude :: "
					+ location.getLongitude(), LogClass.DEBUG_MSG);
				// timer1.cancel();
				//locationResult.gotLocation(location);
				findLocationNameAsync(location);

				appGlobals.sendLocationToServer(location);

			}catch(Exception ex){
				ex.printStackTrace();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::locationListenerNetwork : onProviderDisabled() Provider disabled", LogClass.DEBUG_MSG);
		}

		@Override
		public void onProviderEnabled(String provider) {
			AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::locationListenerNetwork : onProviderEnabled() Provider enables", LogClass.DEBUG_MSG);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::locationListenerNetwork : onStatusChanged() Status changed", LogClass.DEBUG_MSG);
		}
	}

	class LocationListenerGps implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			// timer1.cancel();
			try{
				AppGlobals.appGlobals.logClass.setLogMsg(TAG, "Reached onLocationChanged of gps listener " + AppGlobals.getCurrentTime(), LogClass.DEBUG_MSG);

				if(location==null)return;
				if(!AppGlobals.checkLocationPermission(AppGlobals.appContext, AppGlobals.ACCESS_COARSE_LOC))
					return;
				if(!AppGlobals.checkLocationPermission(AppGlobals.appContext, AppGlobals.ACCESS_FINE_LOC))
					return;
				lm.removeUpdates(this);
				AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::onLocationChanged() : locationListenerGps ::  latitude "
						+ location.getLatitude()
						+ " longitude :: "
						+ location.getLongitude(), LogClass.DEBUG_MSG);
				//locationResult.gotLocation(location);
				findLocationNameAsync(location);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::locationListenerGps : onProviderDisabled() Status changed provider: "+provider, LogClass.DEBUG_MSG);
		}

		@Override
		public void onProviderEnabled(String provider) {
			AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::locationListenerGps : onProviderEnabled() Status changed provider: "+provider, LogClass.DEBUG_MSG);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::locationListenerGps : onStatusChanged() Status changed provider: "+provider, LogClass.DEBUG_MSG);
		}
	}
	
	 public boolean servicesConnected(Context context){
		 return true;
	 }
	 
	 
	 public Location getLastKnownLocation(){
		 
		 Location location=null;
		 if(lm!=null){

			 if(!AppGlobals.checkLocationPermission(AppGlobals.appContext, AppGlobals.ACCESS_COARSE_LOC))
				 return null;
			 if(!AppGlobals.checkLocationPermission(AppGlobals.appContext, AppGlobals.ACCESS_FINE_LOC))
				 return null;

			 if (network_enabled) {
				 location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			 } else if (gps_enabled) {
				 location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			 }
		 }

		 AppGlobals.appGlobals.logClass.setLogMsg(TAG, "MyLocation::getLastKnownLocation: return ::last known location "+location, LogClass.DEBUG_MSG);
		 return location;
		 
	 }
	 
	 private void findLocationNameAsync(final Location location){
		 new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				try{
					locationResult.gotLocation(location);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				return null;
			}			 
		}.execute();
	 }
}

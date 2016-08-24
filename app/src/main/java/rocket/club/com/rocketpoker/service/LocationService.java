package rocket.club.com.rocketpoker.service;

import android.content.Context;
import android.location.Location;

public interface LocationService {
    public boolean getLocation(Context context,LocationResult locationResult) ;
    public void stopLocationUpdates();
    public boolean servicesConnected(Context context);
    public Location getLastKnownLocation();
    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }
}

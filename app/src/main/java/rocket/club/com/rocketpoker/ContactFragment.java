package rocket.club.com.rocketpoker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import rocket.club.com.rocketpoker.utils.AppGlobals;


public class ContactFragment extends Fragment implements OnMapReadyCallback {

    AppGlobals appGlobals = null;
    Context context = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        context = getActivity();
        appGlobals = AppGlobals.getInstance(context);

        SupportMapFragment fm = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map_locate_us);
        fm.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {

        LatLng latLng = new LatLng(12.965276, 77.641724);

        gMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng, 14.0f) );
        gMap.addMarker(new MarkerOptions().position(latLng));
    }
}

package wisc.madison.cs.cs707scale;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class Map extends FragmentActivity implements OnMarkerClickListener {
	private List<ScaleObject> scaleItems = new ArrayList<ScaleObject>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.map);
		SupportMapFragment mf = (SupportMapFragment)f;
        GoogleMap map = mf.getMap();
        map.setMyLocationEnabled(true);
        scaleItems.add(new ScaleObject(map.addMarker(new MarkerOptions()
        .position(new LatLng(43.08244, -89.3757))
        .title("Point1")),"Text1"));
        scaleItems.add(new ScaleObject(map.addMarker(new MarkerOptions()
        .position(new LatLng(43.0747, -89.3844))
        .title("Capitol")),"Text2"));
        map.setOnMarkerClickListener(this);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		for (ScaleObject so : scaleItems)
		{
			if (marker.equals(so.marker))
			{
				Intent intent = new Intent(this, Popup.class);
				intent.putExtra("text", so.text);
				startActivity(intent);
				break;
			}
		}
		return true;
	}


}

package wisc.madison.cs.cs707scale;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle; 
import android.widget.TextView;

public class ScaleLocationListener implements LocationListener {
	
	private final double EDULAT = 43.072041;
	private final double EDULON = -89.403732; 
	private TextView display;
	
	public ScaleLocationListener(Activity activity) {
		display = (TextView)activity.findViewById(R.id.text);
	}
	
	
	public void onLocationChanged(Location loc) {
		double dist = MapUtils.getDistance(loc.getLatitude(), loc.getLongitude(), EDULAT, EDULON);
		String Text = "My current location is:\n" + "Latitud = " + loc.getLatitude() + "\nLongitud = " + loc.getLongitude();
		String Text2 = ("\nMy current distance from education (in meters)= " + dist);
		
		String outText;
		if(dist < 30) {
			outText = "You are in education building. Distance from exact point is " + dist;
		} else {
			outText = (Text + Text2);
		}
		 
		display.setText(outText);
	}
	
	public void onProviderDisabled(String provider) {
		display.setText("GPS is disabled");
	
	}
	
	public void onProviderEnabled(String provider) {
		
	} 
	
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if(status == 0) {
			display.setText("Location is temporarily unavailable");
		}
	}
}

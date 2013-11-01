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
		double dist = getDistance(loc.getLatitude(), loc.getLongitude());
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
		display.setText("GPS is enabled");
	} 
	
	public void onStatusChanged(String provider, int status, Bundle extras) {
		display.setText(status);
		if(status == 0) {
			display.setText("Location is temporarily unavailable");
		}
	}
	
	private double getDistance(double lat, double lon) {
		int radius = 6371000; //earth's radius in meters
		double diffLat = Math.toRadians(EDULAT - lat);
		double diffLon = Math.toRadians(EDULON - lon);
		//haversine formula
		double dist = 
				Math.pow(Math.sin(diffLat / 2), 2) + 
				Math.cos(Math.toRadians(EDULAT)) * 
				Math.cos(Math.toRadians(lat)) * 
				Math.pow(Math.sin(diffLon / 2), 2); 
		dist =  radius * 2 * Math.atan2(Math.sqrt(dist), Math.sqrt(1-dist));
		return dist;
	}

}

package wisc.madison.cs.cs707scale;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle; 

public class ScaleLocationListener implements LocationListener {
	private LatLng previousPoint;
	private Map mapActivity;
	
	public ScaleLocationListener(Activity activity) {
		mapActivity = (Map)activity;
	}
	
	
	public void onLocationChanged(Location loc) {
		if (!mapActivity.pathStarted)
		{
			if (MapUtils.getDistance(loc.getLatitude(), loc.getLongitude(), mapActivity.startingPoint.latitude, mapActivity.startingPoint.longitude) < 30)
			{
				mapActivity.pathStarted = true;
				Intent intent = new Intent(mapActivity, Popup.class);
				intent.putExtra("text", "started");
				mapActivity.startActivity(intent);
			}
		}
		else
		{
			mapActivity.distanceTraveled += MapUtils.getDistance(loc.getLatitude(), loc.getLongitude(), previousPoint.latitude, previousPoint.longitude);
			for (ScaleObject so : mapActivity.scaleItemList)
			{
				if (!so.opened && Math.abs(mapActivity.distanceTraveled - so.distance) < mapActivity.distanceInterval && MapUtils.getDistance(so.position.latitude, so.position.longitude, loc.getLatitude(), loc.getLongitude()) < 30) {
					so.opened = true;
					Intent intent = new Intent(mapActivity, Popup.class);
					intent.putExtra("title", so.name);
					intent.putExtra("image", so.image);
					intent.putExtra("text", so.text);
					mapActivity.startActivity(intent);
				}
			}
		}
		previousPoint = new LatLng(loc.getLatitude(), loc.getLongitude());
	}
	
	public void onProviderDisabled(String provider) {
	
	}
	
	public void onProviderEnabled(String provider) {
		
	} 
	
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
}

package wisc.madison.cs.cs707scale;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class Map extends FragmentActivity implements OnMarkerClickListener {
	private List<ScaleObject> scaleItems = new ArrayList<ScaleObject>();
	private GoogleMap map;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.map);
		SupportMapFragment mf = (SupportMapFragment)f;
        map = mf.getMap();
        map.setMyLocationEnabled(true);
        scaleItems.add(new ScaleObject(map.addMarker(new MarkerOptions()
        .position(new LatLng(43.08244, -89.3757))
        .title("Point1")),"Text1"));
        scaleItems.add(new ScaleObject(map.addMarker(new MarkerOptions()
        .position(new LatLng(43.0747, -89.3844))
        .title("Capitol")),"Text2"));
        map.setOnMarkerClickListener(this);
        LocationManager locationMan = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location location = locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null)
        {
	        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
	        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
        new DownloadPathTask().execute("http://pages.cs.wisc.edu/~jcall/samplePath.kml");

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
	private class DownloadPathTask extends AsyncTask<String, Void, Document> {

		@Override
		protected Document doInBackground(String... arg0) {
			try {
	            InputStream inputStream = new URL(arg0[0]).openStream();
	            DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
	            return docBuilder.parse(inputStream);
	        }
	        catch(Exception e)
	        {
	        	System.out.print(e.toString());
	        }
			return null;
		}
		protected void onPostExecute(Document doc)
		{
            if (doc != null) {
            	PolylineOptions path = new PolylineOptions();
	            NodeList coordinates = doc.getElementsByTagName("gx:coord");
	            for (int i = 0; i < coordinates.getLength(); i++) {
	                String coordText = coordinates.item(i).getFirstChild().getNodeValue().trim();
	                String[] coordinate = coordText.split(" ");
	                path.add(new LatLng(Double.parseDouble(coordinate[1]), Double.parseDouble(coordinate[0])));
	            }
	            map.addPolyline(path);
            }
		}
	}

}

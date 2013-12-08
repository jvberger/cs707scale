package wisc.madison.cs.cs707scale;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.CameraUpdate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.util.DisplayMetrics;

import android.view.Menu;

public class Map extends FragmentActivity implements OnMarkerClickListener {
	public List<ScaleObject> scaleItemList = new ArrayList<ScaleObject>();
	private GoogleMap map;
	public LatLng startingPoint;
	public boolean pathStarted = false;
	public double distanceTraveled = 0;
	public double distanceInterval;
	public Intent intent;
	private String pathItem, scaleItem;
	private ScaleLocationListener locationLis;
	private LocationManager locationMan;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		intent = getIntent();
		pathItem = intent.getStringExtra("pathItem");
		scaleItem = intent.getStringExtra("scaleItem");
		populate();
	}
	
	private void populate() {

		Fragment f = getSupportFragmentManager().findFragmentById(R.id.map);
		SupportMapFragment mf = (SupportMapFragment)f;
        map = mf.getMap();
        map.setMyLocationEnabled(true);
        
        try {
			DocumentBuilder docBuilder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			Document docPath = docBuilder.parse(new ByteArrayInputStream(pathItem.getBytes()));
        	PolylineOptions path = new PolylineOptions();
            NodeList coordinates = docPath.getElementsByTagName("gx:coord");
        	List<Double> steps = new ArrayList<Double>();
        	List<LatLng> points = new ArrayList<LatLng>();
            double prevLat = 0;
            double prevLon = 0;
            double totalDist = 0;
            for (int i = 0; i < coordinates.getLength(); i++) {
                String coordText = coordinates.item(i).getFirstChild().getNodeValue().trim();
                String[] coordinate = coordText.split(" ");
                double currLat = Double.parseDouble(coordinate[1]);
                double currLon = Double.parseDouble(coordinate[0]);
                if (prevLat != 0 && prevLon != 0)
                {
                	double step = MapUtils.getDistance(prevLat, prevLon, currLat, currLon);
                	totalDist += step;
                	steps.add(step);
                }
            	prevLat = currLat;
            	prevLon = currLon;
                LatLng point = new LatLng(currLat, currLon);
                points.add(point);
                path.add(point);
            }
            startingPoint = points.get(0);
            distanceInterval = totalDist * 0.05;
            map.addPolyline(path);
			Document docScale = docBuilder.parse(new ByteArrayInputStream(scaleItem.getBytes()));
            NodeList scaleItems = docScale.getElementsByTagName("scaleItem");
            for (int i = 0; i < scaleItems.getLength(); i++) {
            	NodeList children = scaleItems.item(i).getChildNodes();
            	ScaleObject so = new ScaleObject();
            	for (int j = 0; j < children.getLength(); j++)
            	{
            		Node child = children.item(j);
            		String nodeName = child.getNodeName();
            		if (nodeName.equals("name")) {
            			so.name = child.getTextContent();
            		}
            		else if (nodeName.equals("description")) {
            			so.text = child.getTextContent();
            		}
            		else if (nodeName.equals("percentage")) {
            			so.percentage = Double.parseDouble(child.getTextContent());
            		}
            		else if (nodeName.equals("picture")) {
            			so.imageLocation = child.getTextContent();
            			new LoadImageTask().execute(so);
            		}
            	}
            	if (so.name == null || so.text == null || so.percentage == null)
            	{
            		AlertDialog.Builder alert = new AlertDialog.Builder(this);

            		alert.setTitle("Invalid scale");
            		alert.setMessage("A scale item is missing a name, text, or percentage tag.");
            		alert.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int whichButton) {
            				finish();
            			}
            		});
            		alert.show();
            	}
            	else
            	{
            		double dist = so.percentage * totalDist;
            		so.distance = dist;
            		double cummDist = 0;
            		int step = 0;
            		while (step < steps.size() && cummDist + steps.get(step) < dist)
            		{
            			cummDist += steps.get(step);
            			step++;
            		}
            		if (step != steps.size())
            		{
            			double percentageOfStep = (dist - cummDist)/steps.get(step);
            			double lat = points.get(step).latitude + (points.get(step+1).latitude - points.get(step).latitude)*percentageOfStep;
            			double lon = points.get(step).longitude + (points.get(step+1).longitude - points.get(step).longitude)*percentageOfStep;
            			so.position = new LatLng(lat, lon);
            			so.marker = map.addMarker(new MarkerOptions().position(so.position).title(so.name));
            		}
            		else
            		{
            			so.marker = map.addMarker(new MarkerOptions().position(points.get(i)).title(so.name));
            			so.position = points.get(i);
            		}
            		scaleItemList.add(so);
            	}
            	
            }
        }
        catch (Exception e) {
        	System.out.print(e.toString());
        }
        
        LatLngBounds.Builder calcBounds = new LatLngBounds.Builder();
        for (ScaleObject so : scaleItemList) {
        	calcBounds.include(new LatLng(so.marker.getPosition().latitude, so.marker.getPosition().longitude));  
        }
     
        map.setOnMarkerClickListener(this);
        locationMan = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location location = locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
        	calcBounds.include(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(calcBounds.build(), width, height, 30);
		map.moveCamera(camUpdate);
       
		locationLis = new ScaleLocationListener(this);
		locationMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2, locationLis);
		

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		for (ScaleObject so : scaleItemList)
		{
			if (marker.equals(so.marker))
			{
				if (so.distance - distanceTraveled < distanceInterval)
				{
					Intent intent = new Intent(this, Popup.class);
					intent.putExtra("title", so.name);
					intent.putExtra("image", so.image);
					intent.putExtra("text", so.text);
					startActivity(intent);
					return true;
				}
				break;
			}
		}
		return false;
	}
	
	private class LoadImageTask extends
		AsyncTask<ScaleObject, Void, String> {
	
	@Override
	protected String doInBackground(ScaleObject... arg0) {
		try {
			URL url = new URL(arg0[0].imageLocation);
			InputStream in = url.openStream();
			BufferedInputStream buf = new BufferedInputStream(in);
	        Bitmap myBitmap = BitmapFactory.decodeStream(buf);
            if (in != null) {
                in.close();
            }
            if (buf != null) {
                buf.close();
            }
	        arg0[0].image = myBitmap;
		} catch (Exception e) {
		}
		return "";
	}
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 1, "New Directory");
		menu.add(0, 2, 2, "New Path");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent newIntent;
		switch (item.getItemId()) {
		case 1 :
			getIntent().removeExtra("pathItem"); 
			getIntent().removeExtra("scaleItem"); 
			newIntent = new Intent(this, ScaleChooser.class);
			newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(newIntent);
			return true;
		case 2 :
			finish();
			return true;
		}
		return true;
	}
	
	@Override
	protected void onPause(){
		locationMan.removeUpdates(locationLis);
		locationLis = null;
	    super.onPause();
	} 
	
	@Override
	protected void onResume() {
		locationLis = new ScaleLocationListener(this);
		locationMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2, locationLis);
	    super.onResume();
	}
	
}

package wisc.madison.cs.cs707scale;

import java.io.ByteArrayInputStream;
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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class Map extends FragmentActivity implements OnMarkerClickListener {
	private List<ScaleObject> scaleItemList = new ArrayList<ScaleObject>();
	private GoogleMap map;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Intent intent = getIntent();
		String pathItem = intent.getStringExtra("pathItem");
		String scaleItem = intent.getStringExtra("scaleItem");
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
            			so.image = child.getTextContent();
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
            			so.marker = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(so.name));
            		}
            		else
            		{
            			so.marker = map.addMarker(new MarkerOptions().position(points.get(i)).title(so.name));
            		}
            		scaleItemList.add(so);
            	}
            }
        }
        catch (Exception e) {
        	System.out.print(e.toString());
        }
        map.setOnMarkerClickListener(this);
        LocationManager locationMan = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location location = locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null)
        {
	        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
	        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
        //new DownloadPathTask().execute("http://pages.cs.wisc.edu/~jcall/samplePath.kml");

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		for (ScaleObject so : scaleItemList)
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

package wisc.madison.cs.cs707scale;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.location.LocationManager;


public class Scale extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		LocationManager locationMan = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		ScaleLocationListener locationLis = new ScaleLocationListener(this);
		locationMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationLis);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scale, menu);
		return true;
	}
	
	public void Button1Clicked(View view) {
		Intent intent = new Intent(this, Popup.class);
		intent.putExtra("text", "Sample Popup");
		startActivity(intent);
	}
	
	public void Button2Clicked(View view) {
		Intent intent = new Intent(this, ScaleChooser.class);
		startActivity(intent);
	}
}

package wisc.madison.cs.cs707scale;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
//import android.view.Menu;
import android.widget.TextView;

public class Map extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.map);
		SupportMapFragment mf = (SupportMapFragment)f;
        GoogleMap map = mf.getMap();
        map.setMyLocationEnabled(true);
		//TextView display = (TextView)findViewById(R.id.text);
		//display.setText("Second Screen Text");
	}


}

package wisc.madison.cs.cs707scale;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class Scale extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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

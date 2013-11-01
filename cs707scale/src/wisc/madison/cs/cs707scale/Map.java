package wisc.madison.cs.cs707scale;

import android.app.Activity;
import android.os.Bundle;
//import android.view.Menu;
import android.widget.TextView;

public class Map extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		TextView display = (TextView)findViewById(R.id.text);
		display.setText("Second Screen Text");
	}

	//@Override
	//public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.scale, menu);
	//	return true;
	//}
}

package wisc.madison.cs.cs707scale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

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
		intent.putExtra("title", "Error");
		intent.putExtra("text", "Sample Popup");
		startActivity(intent);
	}
	
	public void Button2Clicked(View view) {
		Intent intent = new Intent(this, DirChooser.class);
		startActivity(intent);
	}
	
	public void Button3Clicked(View view) {
		Intent intent = new Intent(this, RecordPath.class);
		startActivity(intent);
	}

}

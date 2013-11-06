package wisc.madison.cs.cs707scale;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Popup extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup);
		
	    Button dismissbutton = (Button) findViewById(R.id.dismiss_btn);
	    dismissbutton.setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        Popup.this.finish();
	      }
	    });
	}


}

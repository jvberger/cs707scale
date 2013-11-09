package wisc.madison.cs.cs707scale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Popup extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup);
		Intent intent = getIntent();
		String text = intent.getStringExtra("text");
		TextView textView = (TextView) findViewById(R.id.popup_txt);
		textView.setText(text);
		
	    Button dismissbutton = (Button) findViewById(R.id.dismiss_btn);
	    dismissbutton.setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        Popup.this.finish();
	      }
	    });
	}


}

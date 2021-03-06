package wisc.madison.cs.cs707scale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Popup extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup);
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String text = intent.getStringExtra("text");
		Parcelable imageParcel = intent.getParcelableExtra("image");
		Bitmap image = null;
		if (imageParcel != null)
		{
			image = (Bitmap)imageParcel;
		}
		if (image != null)
		{
			ImageView imageView = (ImageView) findViewById(R.id.popup_image);
			imageView.setImageBitmap(image);
		}
		if (title != null)
		{
			TextView textView = (TextView) findViewById(R.id.popup_title);
			textView.setText(title);
		}
		if (text != null)
		{
			TextView textView = (TextView) findViewById(R.id.popup_txt);
			textView.setText(text);
		}
	    Button dismissbutton = (Button) findViewById(R.id.dismiss_btn);
	    dismissbutton.setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        Popup.this.finish();
	      }
	    });
	}


}

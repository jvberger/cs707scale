package wisc.madison.cs.cs707scale;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ScaleChooser extends Activity implements OnItemClickListener {
	private ListView scales;
	private ScaleChooser ref;
	private String currentDirectory;
	private static final String SETTINGSNAME = "ScaleSettings";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);
		ref = this;
		scales = (ListView)findViewById(R.id.listView1);
		scales.setOnItemClickListener(this);
		SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
		currentDirectory = settings.getString("scaleDirectory", "");
		if (currentDirectory != "")
		{
			new LoadScalesTask().execute(currentDirectory);
		}
	}
	
	public void ChangeDirectoryClicked(View view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
	
		alert.setTitle("Change Directory");
		alert.setMessage("Input the location to search for scale files.");
	
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);
	
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  currentDirectory = input.getText().toString();
	      SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString("scaleDirectory", currentDirectory);
	      editor.commit();

		  new LoadScalesTask().execute(currentDirectory);
		  }
		});
	
		alert.setNegativeButton("Cancel", null);
	
		alert.show();
	}
	

	private class LoadScalesTask extends AsyncTask<String, Void, List<String>> {

		@Override
		protected List<String> doInBackground(String... arg0) {
			List<String> scaleList = new ArrayList<String>();
			try {
				String path = arg0[0];
				if (!path.endsWith("/"))
				{
					path += "/";
				}
				path += "Scales.txt";
			    BufferedReader in = new BufferedReader(new InputStreamReader(new URL(path).openStream()));
			    String str;
			    while ((str = in.readLine()) != null) {
			    	scaleList.add(str);
			    }
			    in.close();
	        }
	        catch(Exception e)
	        {
	        }
			return scaleList;
		}
		protected void onPostExecute(List<String> scaleList)
		{
		    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ref,
		            android.R.layout.simple_list_item_1, scaleList);
		    scales.setAdapter(adapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String path = currentDirectory;
		if (!path.endsWith("/"))
		{
			path += "/";
		}
		path += (String)scales.getItemAtPosition(arg2);
		path += ".xml";
		new LoadIndividualScaleTask().execute(path);
	}
	
	private class LoadIndividualScaleTask extends AsyncTask<String, Void, Document> {

		@Override
		protected Document doInBackground(String... arg0) {
			try {
	            InputStream inputStream = new URL(arg0[0]).openStream();
	            DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
	            return docBuilder.parse(inputStream);
	        }
	        catch(Exception e)
	        {
	        }
			return null;
		}
		protected void onPostExecute(Document scaleItem)
		{
			if (scaleItem != null)
			{
				// Pass scale item.
				Intent intent = new Intent(ref, Map.class);
				startActivity(intent);
			}
			else {
				Intent intent = new Intent(ref, Popup.class);
				intent.putExtra("text", "The requested scale does not exist.");
				startActivity(intent);
			}
		}
	}
}

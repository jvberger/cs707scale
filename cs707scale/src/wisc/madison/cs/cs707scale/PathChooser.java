package wisc.madison.cs.cs707scale;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class PathChooser extends Activity implements OnItemClickListener {
	private ListView paths;
	private PathChooser ref;
	private String scaleItem;
	private String currentDirectory;
	private static final String SETTINGSNAME = "ScaleSettings";
	private HashMap<String,String> fileName = new HashMap<String,String>();
	private boolean local;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);
		ref = this;
		Intent intent = getIntent();
		scaleItem = intent.getStringExtra("scaleItem");
		paths = (ListView) findViewById(R.id.listView1);
		paths.setOnItemClickListener(this);
		SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
		currentDirectory = settings.getString("pathDirectory", "");
		if (currentDirectory != "") {
			new LoadPathsTask().execute(currentDirectory);
		}
		else
		{
			LoadLocalPath();
		}
	}
	
	private void LoadLocalPath()
	{
		try {
			local = true;
			List<String> pathList = new ArrayList<String>();
			String str;
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(ref.getFilesDir(), "Paths.txt"))));
			
			while ((str = in.readLine()) != null) {
				int split = str.lastIndexOf('\t');
				if (split != -1)
				{
					String first = str.substring(0, split);
					String second = str.substring(split+1);
					pathList.add(first);
					fileName.put(first, second);
				}
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(ref,
					android.R.layout.simple_list_item_1, pathList);
			paths.setAdapter(adapter);
			in.close();
		} catch (Exception e) {
		}	
	}

	public void ChangeDirectoryClicked(View view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Change Directory");
		alert.setMessage("Input the location to search for path files.");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(currentDirectory);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				currentDirectory = input.getText().toString();
				SharedPreferences settings = getSharedPreferences(SETTINGSNAME,
						0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("pathDirectory", currentDirectory);
				editor.commit();
				if (currentDirectory != "")
				{
					new LoadPathsTask().execute(currentDirectory);
				}
				else
				{
					LoadLocalPath();
				}
			}
		});

		alert.setNegativeButton("Cancel", null);

		alert.show();
	}

	public class LoadPathsTask extends AsyncTask<String, Void, List<String>> {

		@Override
		protected List<String> doInBackground(String... arg0) {
			List<String> pathList = new ArrayList<String>();
			fileName.clear();
			try {
				String path = arg0[0];
				if (!path.endsWith("/")) {
					path += "/";
				}
				path += "Paths.txt";
				BufferedReader in = new BufferedReader(new InputStreamReader(
						new URL(path).openStream()));
				String str;
				while ((str = in.readLine()) != null) {
					int split = str.lastIndexOf('\t');
					if (split != -1)
					{
						String first = str.substring(0, split);
						String second = str.substring(split+1);
						pathList.add(first);
						fileName.put(first, second);
					}
				}
				in.close();
			} catch (Exception e) {
			}
			return pathList;
		}

		protected void onPostExecute(List<String> pathList) {
			local = false;
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(ref,
					android.R.layout.simple_list_item_1, pathList);
			paths.setAdapter(adapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String path = "";
		if (!local)
		{
			path += currentDirectory;
			if (!path.endsWith("/")) {
				path += "/";
			}
		}
		path += fileName.get((String) paths.getItemAtPosition(arg2));
		Intent intent = new Intent(ref, Map.class);
		intent.putExtra("path", path);
		intent.putExtra("scaleItem", scaleItem);
		intent.putExtra("localPath", local);
		startActivity(intent);
	}
}

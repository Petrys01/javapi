package com.ste0067.gpstracker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ste0067.gpstracker.R;
import com.ste0067.gpstracker.KMLReader;
//import com.ste0067.gpstracker.MapsActivity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
//import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FileBrowserActivity extends ListActivity {

	//private static final int ACTIVITY_INTENT_RETURNTEXT = 1;

	private enum DISPLAYMODE {
		ABSOLUTE, RELATIVE;
	}

	private final DISPLAYMODE displayMode = DISPLAYMODE.ABSOLUTE;
	private List<String> directoryEntries = new ArrayList<String>();
	private File currentDirectory = new File("/");

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		browseToRoot();
	}

	private void browseToRoot() {
		String path = Environment.getExternalStorageDirectory().getPath();
		browseTo(new File( path + "/GPS Tracker/"));
		
	}

	private void upOneLevel() {
		if (this.currentDirectory.getParent() != null) {
			this.browseTo(this.currentDirectory.getParentFile());
		}
	}

	private void browseTo(final File aDirectory) {
		if (aDirectory.isDirectory()) {
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		} 
		else {
			OnClickListener okButtonListener = new OnClickListener() {
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
					try {
						String path = aDirectory.getAbsolutePath().toString();
						
						KMLReader kmlreader = new KMLReader();
						kmlreader.read(FileBrowserActivity.this, path);
						
						File file = new File(path);
						Intent target = new Intent(Intent.ACTION_VIEW);
			            target.setDataAndType(Uri.fromFile(file), "text/plain");
			            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			            
			            Intent intent = Intent.createChooser(target, "Otevøít soubor");
			            try {
			                startActivity(intent);
			            } 
			            catch (ActivityNotFoundException e) {
			            	Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			            }

						finish();
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			OnClickListener cancelButtonListener = new OnClickListener() {
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {

				}
			};

			new AlertDialog.Builder(this)
					.setTitle("Otevøení KML")
					.setMessage(
							"Chcete otevøít soubor\n" + aDirectory.getName()
									+ " ?")
					.setPositiveButton("OK", okButtonListener)
					.setNegativeButton("Zrušit", cancelButtonListener).show();

		}
	}

	private void fill(File[] files) {

		this.directoryEntries.clear();

		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		this.directoryEntries.add(".");

		if (this.currentDirectory.getParent() != null) {
			this.directoryEntries.add("..");
		}

		switch (this.displayMode) {
		case ABSOLUTE:
			for (File file : files) {
				this.directoryEntries.add(file.getPath());
			}
			break;
		case RELATIVE:
			int currentPathStringLenght = this.currentDirectory
					.getAbsolutePath().length();
			for (File file : files) {
				this.directoryEntries.add(file.getAbsolutePath().substring(
						currentPathStringLenght));
			}
			break;
		}

		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				R.layout.file_browser, this.directoryEntries);
		this.setListAdapter(directoryList);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int selectionRowID = position;
		String selectedFileString = this.directoryEntries.get(selectionRowID);
		if (selectedFileString.equals(".")) {
			this.browseTo(this.currentDirectory);
		} else if (selectedFileString.equals("..")) {
			this.upOneLevel();
		} else {
			File clickedFile = null;
			switch (this.displayMode) {
			case RELATIVE:
				clickedFile = new File(this.currentDirectory.getAbsolutePath()
						+ this.directoryEntries.get(selectionRowID));
				break;
			case ABSOLUTE:
				clickedFile = new File(
						this.directoryEntries.get(selectionRowID));
				break;
			}

			if (clickedFile != null)
				this.browseTo(clickedFile);

		}
	}
}
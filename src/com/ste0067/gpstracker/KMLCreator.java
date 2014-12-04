package com.ste0067.gpstracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.ste0067.gpstracker.DataHelper;

import android.content.Context;
import android.os.Environment;
import android.text.format.Time;
import android.widget.Toast;

public class KMLCreator {

	private DataHelper dh;
	private Context context;
	private String currentDateTime;
	public String finalKML;
	public String coordinates;

	public KMLCreator() {

		Time currentTime = new Time();
		currentTime.setToNow();
		currentDateTime = currentTime.toString();
		String year = currentDateTime.substring(2, 4);
		String month = currentDateTime.substring(4, 6);
		String day = currentDateTime.substring(6, 8);
		String hour = currentDateTime.substring(9, 11);
		String minute = currentDateTime.substring(11, 13);
		currentDateTime = day + month + year + hour + minute;

	}

	public void write(Context context) {

		this.context = context;
		createstring();

		File root = new File(Environment.getExternalStorageDirectory(), "GPS Tracker");
        if (!root.exists()) {
            root.mkdirs();
        }
        
		File logFile = new File(Environment.getExternalStorageDirectory(),
				"GPS Tracker/" + currentDateTime + ".kml");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				Toast.makeText(context, "Nemùžu uložit soubor: " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}

		try {
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));
			buf.write(finalKML);
			buf.close();

			Toast.makeText(context, "KML uloženo", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(context, "Nemùžu uložit soubor: " + e.getMessage(), Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
	}

	public void createstring() {
		
		StringBuffer start = new StringBuffer();
		start.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		start.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
		start.append("<Document>\n<name></name>\n");
		start.append("<description></description>\n<Style>\n");
		start.append("<LineStyle>\n<color>00cc00</color>\n");
		start.append("<width>6</width>\n</LineStyle>\n<PolyStyle>\n");
		start.append("<color>00ff00</color>\n</PolyStyle>\n</Style>\n");
		start.append("<Placemark>\n<name></name>\n");
		start.append("<description></description>\n<LineString>\n");
		start.append("<extrude>1</extrude>\n<tessellate>1</tessellate>\n");
		start.append("<altitudeMode>absolute</altitudeMode>\n<coordinates>\n");
		
		String startkml = start.toString();

		this.dh = new DataHelper(context);
		List<String> names = this.dh.selectAll();
		StringBuilder sb = new StringBuilder();
		for (String name : names) {
			sb.append(name + "\n");
		}

		coordinates = sb.toString();

		String endkml = "</coordinates>\n</LineString>\n</Placemark>\n</Document>\n</kml>";

		finalKML = startkml + coordinates + endkml;

	}
}
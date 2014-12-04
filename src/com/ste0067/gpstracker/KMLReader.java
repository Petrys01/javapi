package com.ste0067.gpstracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


import android.content.Context;
import android.widget.Toast;

public class KMLReader {

	private DataHelper dh;

	public void read(Context context, String FilePath) {

		this.dh = new DataHelper(context);
		File file = new File(FilePath);

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				if (line.contains("<coordinates>")) {
					line = br.readLine();
					while (line.contains(",")) {
						this.dh.insert(line);
						line = br.readLine();
					}

					List<String> names = this.dh.selectAll();
					StringBuilder sb = new StringBuilder();

					for (String name : names) {
						sb.append(name + "\n");
					}
					break;
				}
			}
			br.close();
		} 
		catch (IOException e) {
			Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
		}
	}
}

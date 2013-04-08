package ch.shimbawa.jorofi.tools.transformer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import ch.shimbawa.jorofi.common.GPXHelper;
import ch.shimbawa.jorofi.xml.GpxType;

public class GpxTransformerTool {

	public static void main(String[] args) throws FileNotFoundException {
		String src = "C:\\svn\\googlecode\\jorofi\\trunk\\data\\dutoitc-running\\";
		GpxType gpxIn = GPXHelper.unmarshall(new FileInputStream(src + "activity_232911589.gpx"));

		GpxType gpxOut = new GpxType();
		gpxOut.setCreator("Jorofi");
		gpxOut.setVersion("1.1");

//		GPXTransformer transformer = new SmoothTransformer();
		GPXTransformer transformer = new SimplifierTransformer();
		transformer.transform(gpxIn, gpxOut);

		// Write
		String filename = src + "activity_232911589-simplified.gpx";
		GPXHelper.marshall(gpxOut, new FileOutputStream(filename));
		System.out.println("Wrote " + filename);
	}

}

package ch.shimbawa.jorofi.tools.router4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.List;

import ch.shimbawa.jorofi.common.GPXHelper;
import ch.shimbawa.jorofi.io.GPXWriter;
import ch.shimbawa.jorofi.xml.GpxType;
import ch.shimbawa.jorofi.xml.TrkType;
import ch.shimbawa.jorofi.xml.TrksegType;
import ch.shimbawa.jorofi.xml.WptType;

// TODO: routes -> objet
public class GpxRoutesMergerTool {

	public static void main(String[] args) throws FileNotFoundException {
		String src = "C:\\svn\\googlecode\\jorofi\\trunk\\data\\dutoitc-running";
		RoutesMerger merger = new RoutesMerger();

		for (File file : new File(src).listFiles(new ActivityFilenameFilter())) {
			System.out.println("Reading " + file.getName());
			GpxType gpx = GPXHelper.unmarshall(new FileInputStream(file));
			addGpxData(merger, gpx);
		}

		String filename = src + "\\router4-out1.gpx";
		new GPXWriter().write(merger.getRoutes(), new FileOutputStream(filename));
		System.out.println("Wrote " + filename);

		merger.simplifyRoutes();

		filename = src + "\\router4-out2.gpx";
		new GPXWriter().write(merger.getRoutes(), new FileOutputStream(filename));
		System.out.println("Wrote " + filename);
	}

	static void addGpxData(RoutesMerger merger, GpxType gpx) {
		for (TrkType trk : gpx.getTrk()) {
			for (TrksegType trkSeg : trk.getTrkseg()) {
				List<WptType> trkPoints = trkSeg.getTrkpt();
				merger.addPointsToRoutes(trkPoints);
			}
		}
	}

	private static final class ActivityFilenameFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith("gpx") && name.startsWith("activity_") && !name.contains("-");
		}
	}

}

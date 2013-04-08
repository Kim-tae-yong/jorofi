package ch.shimbawa.jorofi.tools.router2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import ch.shimbawa.jorofi.common.GPXHelper;
import ch.shimbawa.jorofi.tools.data.LonLat;
import ch.shimbawa.jorofi.xml.GpxType;
import ch.shimbawa.jorofi.xml.TrkType;
import ch.shimbawa.jorofi.xml.TrksegType;
import ch.shimbawa.jorofi.xml.WptType;

/** Merge all GPX given to a unique one full of waypoints */
public class GpxRoutesMergerTool {

	public static void main(String[] args) throws FileNotFoundException {
		String src = "C:\\svn\\googlecode\\jorofi\\trunk\\data\\dutoitc-running";

		List<LonLat> points = new ArrayList<LonLat>();
		double lonMin = 180.0;
		double lonMax = -180.0;
		double latMin = 90.0;
		double latMax = -90.0;

		GpxType gpxOutPointsMerged = new GpxType();
		gpxOutPointsMerged.setCreator("Jorofi");
		gpxOutPointsMerged.setVersion("1.1");

		for (File file : new File(src).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith("gpx");
			}
		})) {
			GpxType gpx = GPXHelper.unmarshall(new FileInputStream(file));

			for (TrkType trk : gpx.getTrk()) {
				for (TrksegType trkSeg : trk.getTrkseg()) {
					for (WptType wpt : trkSeg.getTrkpt()) {
						lonMin = Math.min(wpt.getLon().doubleValue(), lonMin);
						lonMax = Math.max(wpt.getLon().doubleValue(), lonMax);
						latMin = Math.min(wpt.getLat().doubleValue(), latMin);
						latMax = Math.max(wpt.getLat().doubleValue(), latMax);

						gpxOutPointsMerged.getWpt().add(wpt);
						points.add(new LonLat(wpt.getLon().doubleValue(), wpt.getLat().doubleValue()));
					}
				}
			}
		}

		// Log
		System.out.println("Found boundaries: lon=" + lonMin + " - " + lonMax + ", lat=" + latMin + "-" + latMax);

		// Reduce
//		antReduce(src, points, 10, 5);
//		antReduce(src, points, 5, 5);
//		antReduce(src, points, 3, 3);

		// Write
		GPXHelper.marshall(gpxOutPointsMerged, new FileOutputStream(src + "\\extractor-points-merged.gpx"));
		System.out.println("Wrote " + src + "\\extractor-points-merged.gpx");
	}


}

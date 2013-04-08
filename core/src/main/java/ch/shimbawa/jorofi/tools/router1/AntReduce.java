package ch.shimbawa.jorofi.tools.router1;

import java.util.ArrayList;
import java.util.List;

public class AntReduce {

	private class AntDot extends LonLat {
		double intensity;

		public AntDot(double lon, double lat) {
			super(lon, lat);
			intensity = 0;
		}

		public void addIntensity(double d) {
			intensity += d;
		}
	}

	public List<LonLat> reduce(List<LonLat> points, double metersMax, int intensityMinThreshold) {
		// Init		
		List<AntDot> dots = new ArrayList<AntDot>(points.size());
		for (LonLat point : points) {
			dots.add(new AntDot(point.lon, point.lat));
		}
		System.out.println(" 10%");

		// Add intensity by distance < n meters
		int i=0;
		for (AntDot itDot : dots) {
			if (i%(dots.size()/10)==0) {
				System.out.println(" " +( 10 + ((80 * i++) / dots.size())) + "%");
			}
			for (AntDot itDot2 : dots) {
				double dist = itDot.distanceTo(itDot2);
				if (dist < metersMax) {
					itDot.addIntensity(1 - dist / metersMax);
				}
			}
		}
		
		// Reduce
		List<LonLat> filteredPoints = new ArrayList<LonLat>();
		i=0;
		for (AntDot dot: dots) {
			System.out.print(" " + 90 + (10 * i++ / dots.size()));
			if (dot.intensity>intensityMinThreshold) {
				filteredPoints.add(dot);
			}
		}
		
		return filteredPoints;
	}

}

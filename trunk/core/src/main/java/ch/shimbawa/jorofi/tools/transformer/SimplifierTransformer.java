package ch.shimbawa.jorofi.tools.transformer;

import java.util.List;

import ch.shimbawa.jorofi.xml.GpxType;
import ch.shimbawa.jorofi.xml.TrkType;
import ch.shimbawa.jorofi.xml.TrksegType;
import ch.shimbawa.jorofi.xml.WptType;

/** Simplify lines by removing unnecessary point */
public class SimplifierTransformer implements GPXTransformer {

	private static final int ANGLE_THRESHOLD_DEGREE = 3;

	public void transform(GpxType gpxIn, GpxType gpxOut) {
		TrkType trkType = new TrkType();

		for (TrkType trk : gpxIn.getTrk()) {
			for (TrksegType trkSeg : trk.getTrkseg()) {
				// In
				List<WptType> wptIn = trkSeg.getTrkpt();

				// Out
				gpxOut.getTrk().add(trkType);
				TrksegType trkSegType = new TrksegType();
				trkType.getTrkseg().add(trkSegType);
				List<WptType> wptOut = trkSegType.getTrkpt();

				wptOut.add(wptIn.get(0));
				for (int i = 1; i < wptIn.size()-1; i++) {
					boolean select = true;

					WptType p0 = wptIn.get(i - 1);
					WptType p1 = wptIn.get(i);
					WptType p2 = wptIn.get(i + 1);
					double angle1 = angle(p0, p1);
					double angle2 = angle(p1, p2);
					double diff = Math.abs(angle1 - angle2);
					if (diff > 180.0) {
						diff = 360 - diff;
					} else if (diff < -180) {
						diff += 360;
					}
//					System.out.println(diff);
					select = diff > ANGLE_THRESHOLD_DEGREE;

					if (select) {
						wptOut.add(wptIn.get(i));
					}
				}
				wptOut.add(wptIn.get(wptIn.size() - 1));
			}
		}
	}

	private double angle(WptType p0, WptType p1) {
		double lon0 = p0.getLon().doubleValue();
		double lat0 = p0.getLat().doubleValue();
		double lon1 = p1.getLon().doubleValue();
		double lat1 = p1.getLat().doubleValue();
		double dlon = lon1 - lon0;
		double dlat = lat1 - lat0;
		double angle = Math.atan2(dlat, dlon) * 180 / Math.PI;
		return angle;
	}

}

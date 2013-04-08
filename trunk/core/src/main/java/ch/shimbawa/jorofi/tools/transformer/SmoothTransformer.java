package ch.shimbawa.jorofi.tools.transformer;

import java.math.BigDecimal;
import java.util.List;

import ch.shimbawa.jorofi.xml.GpxType;
import ch.shimbawa.jorofi.xml.TrkType;
import ch.shimbawa.jorofi.xml.TrksegType;
import ch.shimbawa.jorofi.xml.WptType;

/** Smooth lines by Savitzky-Golay */
public class SmoothTransformer implements GPXTransformer {

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

				for (int i = 0; i < wptIn.size(); i++) {
					int div = 0;
					double lon = 0.0;
					double lat = 0.0;
					double alt = 0.0;

					int fac;
					if (i > 1) {
						fac = -3;
						div += fac;
						WptType point = wptIn.get(i - 2);
						lon += point.getLon().doubleValue() * fac;
						lat += point.getLat().doubleValue() * fac;
						alt += point.getEle().doubleValue() * fac;
					}
					if (i > 0) {
						fac = 12;
						div += fac;
						WptType point = wptIn.get(i - 1);
						lon += point.getLon().doubleValue() * fac;
						lat += point.getLat().doubleValue() * fac;
						alt += point.getEle().doubleValue() * fac;
					}
					{
						fac = 17;
						div += fac;
						WptType point = wptIn.get(i);
						lon += point.getLon().doubleValue() * fac;
						lat += point.getLat().doubleValue() * fac;
						alt += point.getEle().doubleValue() * fac;
					}
					if (i < wptIn.size() - 1) {
						fac = 12;
						div += fac;
						WptType point = wptIn.get(i + 1);
						lon += point.getLon().doubleValue() * fac;
						lat += point.getLat().doubleValue() * fac;
						alt += point.getEle().doubleValue() * fac;
					}
					if (i < wptIn.size() - 2) {
						fac = -3;
						div += fac;
						WptType point = wptIn.get(i + 2);
						lon += point.getLon().doubleValue() * fac;
						lat += point.getLat().doubleValue() * fac;
						alt += point.getEle().doubleValue() * fac;
					}

					WptType newPoint = new WptType();
					newPoint.setLon(BigDecimal.valueOf(lon / div));
					newPoint.setLat(BigDecimal.valueOf(lat / div));
					newPoint.setEle(BigDecimal.valueOf(alt / div));
					wptOut.add(newPoint);
				}
			}
		}

	}

}

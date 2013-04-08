package ch.shimbawa.jorofi.tools.router3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ch.shimbawa.jorofi.common.GPXHelper;
import ch.shimbawa.jorofi.tools.common.TypeCodec;
import ch.shimbawa.jorofi.tools.data.Arrete;
import ch.shimbawa.jorofi.tools.data.Point;
import ch.shimbawa.jorofi.xml.GpxType;
import ch.shimbawa.jorofi.xml.TrkType;
import ch.shimbawa.jorofi.xml.TrksegType;
import ch.shimbawa.jorofi.xml.WptType;

/** Merge all GPX given to a unique one with common routes merged. */
// TODO: merge by triangulation
public class GpxRoutesMergerTool {

	public static void main(String[] args) throws FileNotFoundException {
		String src = "C:\\svn\\googlecode\\jorofi\\trunk\\data\\dutoitc-running";
		MergeData data = loadData(src);
		mergeSamePoints(data);
		save(data, src + "router3-step1.gpx");

		// Merging
		merge(data);
		mergeSamePoints(data);
		save(data, src + "router3-step2.gpx");

		// Count point use
		Map<Point, Long> pointUse = countPoints(data);
		countOrpheans(pointUse);

		// Help orpheans
		linkOrpheanToNearest(data, pointUse);
		pointUse = countPoints(data);
		countOrpheans(pointUse);
		save(data, src + "router3-step3.gpx");

		// Create arretes
		List<LinkedList<Arrete>> graph = graph2Arretes(data, pointUse);

		save(graph, src + "\\extractor-points-merged3.gpx");
	}

	private static void merge(MergeData data) {
		int nb = 0;
		ListIterator<Arrete> it1 = data.getArretes().listIterator();
		Set<Arrete> trash = new HashSet<Arrete>();
		while (it1.hasNext()) {
			Arrete arrete1 = it1.next();
			if (trash.contains(arrete1)) {
				continue;
			}

			ListIterator<Arrete> it2 = data.getArretes().listIterator();
			while (it2.hasNext()) {
				Arrete arrete2 = it2.next();
				if (!trash.contains(arrete2) && (!arrete1.equals(arrete2)) && (!arrete1.equals(arrete2.reverse()))
						&& arrete1.getMiddlePoint().distanceTo(arrete2.getMiddlePoint()) < 8) {
					double angle1 = arrete1.getAngle12();
					double angle2 = arrete2.getAngle12();
					if (Math.abs(angle1 - angle2) < 45) {
						arrete1.getPoint1().moveMiddleDistanceTo(arrete2.getPoint1());
						arrete1.getPoint2().moveMiddleDistanceTo(arrete2.getPoint2());
						if (it2.hasPrevious()) {
							it2.previous().setPoint2(arrete1.getPoint1());
							it2.next();
						}
						if (it2.hasNext()) {
							it2.next().setPoint1(arrete1.getPoint2());
							it2.previous();
						}

						// trash.add(arrete2);
						// trash.add(arrete2.reverse());

						nb++;
					} else if (180 - Math.abs(angle1 - angle2) < 45) {
						arrete1.getPoint1().moveMiddleDistanceTo(arrete2.getPoint2());
						arrete1.getPoint2().moveMiddleDistanceTo(arrete2.getPoint1());
						if (it2.hasPrevious()) {
							it2.previous().setPoint2(arrete1.getPoint1());
							it2.next();
						}
						if (it2.hasNext()) {
							it2.next().setPoint1(arrete1.getPoint2());
							it2.previous();
						}

						// trash.add(arrete2);
						// trash.add(arrete2.reverse());

						nb++;
					}
				}
			}
		}
		System.out.println("Found " + nb + " / " + data.getArretes().size() * data.getArretes().size());

		it1 = data.getArretes().listIterator();
		while (it1.hasNext()) {
			Arrete arrete = it1.next();
			if (trash.contains(arrete)) {
				it1.remove();
			}
		}
	}

	private static void linkOrpheanToNearest(MergeData data, Map<Point, Long> pointUse) {
		for (Arrete arrete : data.getArretes()) {
			if (pointUse.get(arrete.getPoint1()) == 1) {
				arrete.setPoint1(findNearestNotOrphean(arrete.getPoint1(), pointUse));
			}
			if (pointUse.get(arrete.getPoint2()) == 1) {
				arrete.setPoint2(findNearestNotOrphean(arrete.getPoint2(), pointUse));
			}
		}
	}
	
	private static void mergeSamePoints(MergeData data) {
		List<Point> points = data.getPoints();
		Map<Point, Point> replacements = new HashMap<Point, Point>();
		
		for (int i=0; i<points.size(); i++) {
			for (int j=i+1; j<points.size();j++) {
				if (!replacements.containsKey(points.get(j)) && points.get(i).distanceTo(points.get(j))<1) {
					replacements.put(points.get(j), points.get(i));
				}
			}
		}
				
		for (Arrete arrete: data.getArretes()) {
			if (replacements.containsKey(arrete.getPoint1())) {
				arrete.setPoint1(replacements.get(arrete.getPoint1()));
			}
			if (replacements.containsKey(arrete.getPoint2())) {
				arrete.setPoint1(replacements.get(arrete.getPoint2()));
			}
		}
		
		System.out.println("Merged " + replacements.size() + " same points ");
		
		// TODO: remove orphean poitns in MergeData
	}

	private static List<LinkedList<Arrete>> graph2Arretes(MergeData data, Map<Point, Long> pointUse) {
		List<LinkedList<Arrete>> graph = new ArrayList<LinkedList<Arrete>>();
		for (Arrete arrete : data.getArretes()) {
			boolean addAsNew = true;

			if (pointUse.get(arrete.getPoint1()) == 2) {
				// Left = 2 -> merge
				for (LinkedList<Arrete> arreteList : graph) {
					if (arreteList.get(arreteList.size() - 1).getPoint2() == arrete.getPoint1()) {
						arreteList.addLast(arrete);
						addAsNew = false;
						break;
					}
					if (arreteList.get(arreteList.size() - 1).getPoint2() == arrete.getPoint2()) {
						arreteList.addLast(arrete.reverse());
						addAsNew = false;
						break;
					}
					if (arreteList.get(0).getPoint1() == arrete.getPoint2()) {
						arreteList.addFirst(arrete);
						addAsNew = false;
						break;
					}
					if (arreteList.get(0).getPoint1() == arrete.getPoint1()) {
						arreteList.addFirst(arrete.reverse());
						addAsNew = false;
						break;
					}
				}
			}

			if (addAsNew) {
				LinkedList<Arrete> arreteList = new LinkedList<Arrete>();
				arreteList.add(arrete);
				graph.add(arreteList);
			}

		}
		return graph;
	}

	private static void save(List<LinkedList<Arrete>> graph, String filename) throws FileNotFoundException {
		// Write out
		GpxType gpxOut = new GpxType();
		gpxOut.setCreator("Jorofi");
		gpxOut.setVersion("1.1");
		for (List<Arrete> listArrete : graph) {
			TrkType trk = new TrkType();
			TrksegType trkSeg = new TrksegType();
			trk.getTrkseg().add(trkSeg);
			gpxOut.getTrk().add(trk);

			trkSeg.getTrkpt().add(TypeCodec.buildWpt(listArrete.get(0).getPoint1()));
			for (Arrete arrete : listArrete) {
				trkSeg.getTrkpt().add(TypeCodec.buildWpt(arrete.getPoint2()));
			}
		}

		// Write
		GPXHelper.marshall(gpxOut, new FileOutputStream(filename));
		System.out.println("Wrote " + filename);
	}

	private static void save(MergeData data, String filename) throws FileNotFoundException {
		// Write out
		GpxType gpxOut = new GpxType();
		gpxOut.setCreator("Jorofi");
		gpxOut.setVersion("1.1");

		for (Arrete arrete : data.getArretes()) {
			TrkType trk = new TrkType();
			TrksegType trkSeg = new TrksegType();
			trk.getTrkseg().add(trkSeg);
			gpxOut.getTrk().add(trk);
			trkSeg.getTrkpt().add(TypeCodec.buildWpt(arrete.getPoint1()));
			trkSeg.getTrkpt().add(TypeCodec.buildWpt(arrete.getPoint2()));
		}

		// Write
		GPXHelper.marshall(gpxOut, new FileOutputStream(filename));
		System.out.println("Wrote " + filename);
	}

	private static Point findNearestNotOrphean(Point point1, Map<Point, Long> pointUse) {
		Point bestPoint = null;
		double bestDistance = 100000;
		for (Entry<Point, Long> entry : pointUse.entrySet()) {
			if (entry.getValue() > 1) {
				double dist = point1.distanceTo(entry.getKey());
				if (bestPoint == null || dist < bestDistance) {
					bestPoint = entry.getKey();
					bestDistance = dist;
				}
			}
		}
		return bestPoint;
	}

	private static void countOrpheans(Map<Point, Long> pointUse) {
		int nb = 0;
		for (Entry<Point, Long> entry : pointUse.entrySet()) {
			if (entry.getValue() == 1) {
				nb++;
			}
		}
		System.out.println("Found " + nb + " orpheans");
	}

	private static Map<Point, Long> countPoints(MergeData data) {
		Map<Point, Long> pointUse = new HashMap<Point, Long>(data.getPoints().size() * 4 / 3);
		for (Arrete arrete : data.getArretes()) {
			long value = 0;
			Point point = arrete.getPoint1();
			if (pointUse.containsKey(point)) {
				value = pointUse.get(point);
			}
			pointUse.put(point, ++value);

			value = 0;
			point = arrete.getPoint2();
			if (pointUse.containsKey(point)) {
				value = pointUse.get(point);
			}
			pointUse.put(point, ++value);
		}
		return pointUse;
	}


	private static MergeData loadData(String src) throws FileNotFoundException {
		MergeData data = new MergeData();
		for (File file : new File(src).listFiles(new ActivityFilenameFilter())) {
			System.out.println("Reading " + file.getName());
			GpxType gpx = GPXHelper.unmarshall(new FileInputStream(file));

			for (TrkType trk : gpx.getTrk()) {
				for (TrksegType trkSeg : trk.getTrkseg()) {
					Point latestPoint = null;
					for (WptType wpt : trkSeg.getTrkpt()) {
						Point point = data.getOrCreate(wpt.getLon().doubleValue(), wpt.getLat().doubleValue(), wpt
								.getEle().doubleValue());
						if (latestPoint == null) {
							latestPoint = point;
						} else if (latestPoint.distanceTo(point) > 3.5) {
							data.addArrete(latestPoint, point);
							latestPoint = point;
						}
					}
				}
			}
		}
		return data;
	}

	private static final class ActivityFilenameFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith("gpx") && name.startsWith("activity_") && !name.contains("-");
		}
	}

}

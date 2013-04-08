package ch.shimbawa.jorofi.tools.router3;

import java.util.ArrayList;
import java.util.List;

import ch.shimbawa.jorofi.tools.data.Arrete;
import ch.shimbawa.jorofi.tools.data.Point;

public class MergeData {

	List<Point> points = new ArrayList<Point>();
	List<Arrete> arretes = new ArrayList<Arrete>();

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public List<Arrete> getArretes() {
		return arretes;
	}

	public void setArretes(List<Arrete> arretes) {
		this.arretes = arretes;
	}

	public Point getOrCreate(double lon, double lat, double ele) {
		Point point = new Point(lon, lat, ele);
		// for (Point itPoint : points) {
		// double dist = point.distanceTo(itPoint);
		// if (dist <3) {
		// System.out.println("Merging point for dist=" + dist);
		// return itPoint;
		// }
		// }
		points.add(point);
		return point;
	}

	public void addArrete(Point point1, Point point2) {
		Arrete arrete = new Arrete(point1, point2);
		assert point1.distanceTo(point2)>1;
		arretes.add(arrete);
	}

}

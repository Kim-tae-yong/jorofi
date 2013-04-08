package ch.shimbawa.jorofi.data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** A route composed by some points */
public class Route {

	private LinkedList<Point> points = new LinkedList<Point>();
	private BigDecimal distance = BigDecimal.ZERO;

	private static long nextId = 1;
	private long id;

	public Route() {
		id = nextId++;
	}

	public long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public boolean isSamePoints(Route other) {
		if (other.points.size() != points.size()) {
			return false;
		}

		Iterator<Point> it1 = points.iterator();
		Iterator<Point> it2 = other.points.iterator();
		while (it1.hasNext()) {
			if (it1.next().id != it2.next().id) {
				return false;
			}
		}
		return true;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void addPoint(Point point) {
		if (!points.isEmpty()) {
			double deltaDistance = getLastPoint().distanceTo(point);
			if (points.size()>1 && deltaDistance<0.1) {
				throw new RuntimeException("Distance is too small; data bug ? " + getRepr());
			}
			distance = distance.add(BigDecimal.valueOf(deltaDistance));
			
		}
		points.add(point);
	}

	public void insertPointFirst(Point point) {
		if (!points.isEmpty()) {
			distance = distance.add(BigDecimal.valueOf(getFirstPoint().distanceTo(point)));
		}
		points.addFirst(point);
	}

	public Route reverse() {
		Route route = new Route();
		LinkedList<Point> newPoints = new LinkedList<Point>(points);
		Collections.reverse(newPoints);
		route.points = newPoints;
		route.distance = distance;
		return route;
	}

	public BigDecimal getDistance() {
		if (distance.equals(BigDecimal.ZERO)) {
			String sb = getRepr();			
			throw new RuntimeException("Distance zero is impossible (data bug); " + sb.toString());
		}
		return distance;
	}

	String getRepr() {
		StringBuffer sb = new StringBuffer();
		sb.append("Route #" + id + ", points=");
		for (Point point : points) {
			sb.append("P(");
			sb.append(point.getLongitude());
			sb.append(",");
			sb.append(point.getLatitude());
			if (point instanceof NamedPoint) {
				sb.append(",");
				sb.append(((NamedPoint) point).getName());					
			}
			sb.append(") ");
		}
		return sb.toString();
	}

	public Point getLastPoint() {
		return points.getLast();
	}

	public boolean hasPoint(Point point) {
		return points.contains(point);
	}

	@Override
	public String toString() {
		return "Route [id=" + id + ", distance=" + distance + ", points=" + points + "]";
	}

	/**
	 * Break route at given point, return a new route with the rest. the given
	 * point will be used twice
	 */
	public Route splitAt(Point point) {
		int pos = points.indexOf(point);
		assert pos > -1;

		// First or last -> no split
		if (pos == 0 || pos == points.size() - 1) {
			return null;
		}

		// Build new route
		Route newRoute = new Route();
		for (int i = pos; i < points.size(); i++) {
			newRoute.addPoint(points.get(i));
		}

		// Keep left part of current route (tips: empty list and add point by
		// point to keep distance calculation at insertion)
		LinkedList<Point> savedPoints = points;
		points = new LinkedList<Point>();
		distance = BigDecimal.ZERO;
		for (int i = 0; i <= pos; i++) {
			addPoint(savedPoints.get(i));
		}

		return newRoute;

	}

	public Point getFirstPoint() {
		return points.get(0);
	}


}

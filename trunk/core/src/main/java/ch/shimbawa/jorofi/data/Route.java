package ch.shimbawa.jorofi.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A route composed by some points */
public class Route {

	private List<Point> points = new ArrayList<Point>();
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

	public List<Point> getPoints() {
		return points;
	}

	public void addPoint(Point point) {
		if (!points.isEmpty()) {
			distance = distance.add(BigDecimal.valueOf(getLastPoint()
					.distanceTo(point.getLongitude(), point.getLatitude())));
		}
		points.add(point);
	}

	public Route reverse() {
		Route route = new Route();
		List<Point> newPoints = new ArrayList<Point>(points);
		Collections.reverse(newPoints);
		route.points = newPoints;
		route.distance = distance;
		return route;
	}

	public BigDecimal getDistance() {
		if (distance.equals(BigDecimal.ZERO)) {
			StringBuffer sb = new StringBuffer();
			sb.append("Route #" + id + ", points=");
			for (Point point : points) {
				sb.append(point.getName());
				sb.append(",");
			}
			throw new RuntimeException("Distance zero is impossible (data bug); " + sb.toString());
		}
		return distance;
	}

	public Point getLastPoint() {
		return points.get(points.size() - 1);
	}

	public boolean hasPoint(Point point) {
		for (Point itPoint : points) {
			if (itPoint.getId() == point.getId()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Route [id=" + id + ", distance=" + distance + ", points="
				+ points + "]";
	}

}

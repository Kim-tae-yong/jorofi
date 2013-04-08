package ch.shimbawa.jorofi.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Internal (input) data set */
public class Dataset {

	private List<NamedPoint> points = new ArrayList<NamedPoint>();
	private List<Route> routes = new ArrayList<Route>();

	public NamedPoint findOrAddNearestPoint(BigDecimal lon, BigDecimal lat,
			BigDecimal ele, String name) {
		NamedPoint bestPoint = null;
		double bestDist = 10000;
		for (NamedPoint point : points) {
			double dist = point.distanceTo(lon, lat);
			if (dist < 1 && (bestPoint == null || bestDist > dist)) {
				bestPoint = point;
				bestDist = dist;
			}
		}
		if (bestPoint != null) {
			return bestPoint;
		}

		NamedPoint point = new NamedPoint();
		point.setLongitude(lon);
		point.setLatitude(lat);
		point.setAltitude(ele);
		point.setName(name);
		points.add(point);
		return point;
	}

	public void addPoint(NamedPoint point) {
		points.add(point);
	}

	public void addRoute(Route route) {
		routes.add(route);
	}

	public String getStats() {
		return points.size() + " points and " + routes.size() + " routes";
	}

	/** @return all points with name starting with "start-" */
	public List<NamedPoint> getStartPoints() {
		List<NamedPoint> selectedPoints = new ArrayList<NamedPoint>();
		for (NamedPoint point : points) {
			String pointName = point.getName();
			if (pointName != null && pointName.startsWith("start-")) {
				selectedPoints.add(point);
			}
		}
		return selectedPoints;
	}

	public List<Route> getRoutesFromPoint(Point itPoint) {
		List<Route> selectedRoutes = new ArrayList<Route>();
		for (Route route : routes) {
			if (route.getPoints().get(0).getId() == itPoint.getId()) {
				selectedRoutes.add(route);
			}
		}
		return selectedRoutes;
	}

	public void dump() {
		for (NamedPoint point : points) {
			System.out.println("Point " + point.getId() + ", name="
					+ point.getName());
		}

		for (Route route : routes) {
			System.out.println("Route " + route.getId() + ", dist="
					+ route.getDistance());
			for (Point point : route.getPoints()) {
				System.out.println(point);
			}
		}
	}

	public List<NamedPoint> getPoints() {
		return points;
	}

}

package ch.shimbawa.jorofi.tools.router4;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ch.shimbawa.jorofi.data.Pair;
import ch.shimbawa.jorofi.data.Point;
import ch.shimbawa.jorofi.data.Route;
import ch.shimbawa.jorofi.xml.WptType;

public class RoutesMerger {

	private static final int DISTANCE_MAX_TO_CAPTURE_NEAREST_POINT = 15;
	private static final int DISTANCE_FOR_SAME_ROUTE = 40;

	private List<Route> routes = new ArrayList<Route>();

	public RoutesMerger() {

	}

	public RoutesMerger(List<Route> routes) {
		this.routes = routes;
	}

	void addPointsToRoutes(List<WptType> gpxPoints) {
		Route route = new Route();
		for (WptType wpt : gpxPoints) {
			Point point = new Point(wpt.getLon(), wpt.getLat(), wpt.getEle());

			// Move to existing point ?
			Pair<Point, Route> nearPointRoute = findNearestPoint(point, DISTANCE_MAX_TO_CAPTURE_NEAREST_POINT);
			if (nearPointRoute == null) {
				if (!route.hasPoint(point)) {
					route.addPoint(point);
				}
			} else {
				nearPointRoute.a.moveMiddleDistTo(point);
				if (!route.getPoints().contains(nearPointRoute.a)) {
					route.addPoint(nearPointRoute.a);
				}

				// Break other route (near) at (point)
				splitRoute(nearPointRoute.b, nearPointRoute.a);
				assert route != nearPointRoute.b;

				// Break current route, if not repetition of another
				// route
				if (route.getPoints().size() > 1 && route.getDistance().floatValue() > 0.1
						&& !existsDirectOrInverse(route)) {
					routes.add(route);
				}
				route = new Route();
				route.addPoint(nearPointRoute.a);

			}

		}

		if (route.getPoints().size() > 1 && route.getDistance().floatValue() > 0.1) {
			routes.add(route);
		}
	}

	/**
	 * Merge routes when they can be merged: common point used by only the two
	 * routes
	 */
	void simplifyRoutes() throws FileNotFoundException {
		int nbRoutesBefore = routes.size();

		Map<Point, ArrayList<Route>> routesByPoint = buildPointsByRoute();

		Set<Route> routesMerged = new HashSet<Route>();
		Iterator<java.util.Map.Entry<Point, ArrayList<Route>>> iterator = routesByPoint.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Point, ArrayList<Route>> entry = iterator.next();
			int nbRoutes = entry.getValue().size();
			if (nbRoutes != 2) {
				continue;
			}

			Route route1 = entry.getValue().get(0);
			Route route2 = entry.getValue().get(1);

			// Already merged ? try reverse route
			if (routesMerged.contains(route1) || routesMerged.contains(route1.reverse())) {
				route1 = entry.getValue().get(1);
				route2 = entry.getValue().get(0);
			}
			if (routesMerged.contains(route1) || routesMerged.contains(route1.reverse())) {
				continue;
			}

			if (route1.getLastPoint().getId() == route2.getFirstPoint().getId()) {
				assert route2 != route1;
				Iterator<Point> it = route2.getPoints().iterator();
				it.next(); // Skip first
				while (it.hasNext()) {
					route1.addPoint(it.next());
				}
				routesByPoint.get(route2.getLastPoint()).remove(route2);
				routesByPoint.get(route2.getLastPoint()).add(route1);
			} else if (route1.getFirstPoint() == route2.getLastPoint()) {
				for (int i = route2.getPoints().size() - 2; i >= 0; i--) {
					route1.insertPointFirst(route2.getPoints().get(i));
				}
				routesByPoint.get(route2.getFirstPoint()).remove(route2);
				routesByPoint.get(route2.getFirstPoint()).add(route1);
			} else if (route1.getLastPoint() == route2.getLastPoint()) {
				Route route3 = route2.reverse();
				Iterator<Point> it = route3.getPoints().iterator();
				it.next(); // Skip first
				while (it.hasNext()) {
					route1.addPoint(it.next());
				}
				routesByPoint.get(route2.getFirstPoint()).remove(route2);
				routesByPoint.get(route2.getFirstPoint()).add(route1);
			} else if (route1.getFirstPoint() == route2.getFirstPoint()) {
				for (int i = 1; i < route2.getPoints().size(); i++) {
					route1.insertPointFirst(route2.getPoints().get(i));
				}
				routesByPoint.get(route2.getLastPoint()).remove(route2);
				routesByPoint.get(route2.getLastPoint()).add(route1);
			}
			routes.remove(route2);
			routesMerged.add(route2);
		}

		int nbRoutesAfter = routes.size();
		System.out.println("Simplified " + nbRoutesBefore + " to " + nbRoutesAfter);
	}

	/** Build a map of {point -> Route[]}: all existing routes including (point) */
	Map<Point, ArrayList<Route>> buildPointsByRoute() {
		Map<Point, ArrayList<Route>> routesByPoint = new HashMap<Point, ArrayList<Route>>();
		for (Route route : routes) {
			for (Point point : route.getPoints()) {
				if (routesByPoint.get(point) == null) {
					routesByPoint.put(point, new ArrayList<Route>(2));
				}
				routesByPoint.get(point).add(route);
			}
		}
		return routesByPoint;
	}

	/**
	 * Check if the given route exist in all routes (direct or reverse). Or test
	 * assume same route if they both have same start, end points and a distance
	 * < xx m (parallel routes)
	 */
	boolean existsDirectOrInverse(Route route) {
		Route routeInverse = route.reverse();
		for (Route itRoute : routes) {
			if (route.isSamePoints(itRoute) || routeInverse.isSamePoints(itRoute)) {
				return true;
			}

			// Parallele routes with low distance but not the same number of
			// points
			if (route.getFirstPoint().equals(itRoute.getFirstPoint())
					&& route.getLastPoint().equals(itRoute.getLastPoint())
					&& route.getDistance().intValue() < DISTANCE_FOR_SAME_ROUTE
					&& itRoute.getDistance().intValue() < DISTANCE_FOR_SAME_ROUTE) {
				return true;
			}
			if (routeInverse.getFirstPoint().equals(itRoute.getFirstPoint())
					&& routeInverse.getLastPoint().equals(itRoute.getLastPoint())
					&& routeInverse.getDistance().intValue() < DISTANCE_FOR_SAME_ROUTE
					&& itRoute.getDistance().intValue() < DISTANCE_FOR_SAME_ROUTE) {
				return true;
			}
		}
		return false;
	}

	/** Split (route) in two routes at (point) */
	void splitRoute(Route route, Point point) {
		Route newRoute = route.splitAt(point);
		if (route.getPoints().size() < 2) {
			System.out.println("Removing route of size<2");
			routes.remove(route);
		} else if (route.getDistance().floatValue() < 0.1f) {
			System.out.println("Removing route of dist<0.1");
			routes.remove(route);
		}

		if (newRoute != null && newRoute.getPoints().size() > 1 && newRoute.getDistance().floatValue() > 0.1
				&& !existsDirectOrInverse(newRoute)) {
			// >1 = not end of lines
			routes.add(newRoute);
		}
	}

	/** Find the nearest point of (point), but at max (maxDistance), in (routes) */
	private Pair<Point, Route> findNearestPoint(Point point, int maxDistance) {
		Point bestPoint = null;
		Route bestRoute = null;
		double bestDist = 1000000;

		for (Route route : routes) {
			for (Point itPoint : route.getPoints()) {
				if (itPoint.getId() != point.getId()) {
					double dist = itPoint.distanceTo(point);
					if (bestPoint == null || dist < bestDist) {
						bestDist = dist;
						bestPoint = itPoint;
						bestRoute = route;
					}
				}
			}
		}

		if (bestDist < maxDistance) {
			return new Pair<Point, Route>(bestPoint, bestRoute);
		}
		return null;
	}

	public List<Route> getRoutes() {
		return routes;
	}

}

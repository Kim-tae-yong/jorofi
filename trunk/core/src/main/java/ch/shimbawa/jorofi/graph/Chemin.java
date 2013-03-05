package ch.shimbawa.jorofi.graph;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ch.shimbawa.jorofi.data.Point;
import ch.shimbawa.jorofi.data.Route;

/** A chemin: list of routes */
public class Chemin {

	private List<Route> routes = new ArrayList<Route>();
	BigDecimal totalDistance = BigDecimal.ZERO;

	private static long nextId = 1;
	private long id;

	public Chemin() {
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
		Chemin other = (Chemin) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public BigDecimal getTotalDistance() {
		return totalDistance;
	}

	public void addRoute(Route route) {
		routes.add(route);
		totalDistance = totalDistance.add(route.getDistance());
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public Point getLastPoint() {
		return routes.get(routes.size() - 1).getLastPoint();
	}

	public boolean hasRoute(Route route) {
		for (Route itRoute : routes) {
			if (itRoute.getId() == route.getId()) {
				return true;
			}
		}
		return false;
	}
	

	public boolean hasRouteInverse(Route route) {
		long routeFirstPointId = route.getPoints().get(0).getId();
		long routeLastPointId =route.getLastPoint().getId(); 
		for (Route itRoute : routes) {
			long itRouteFirstPointId = itRoute.getPoints().get(0).getId();
			long itRouteLastPointId =itRoute.getLastPoint().getId();
			
			if (routeFirstPointId == itRouteLastPointId && routeLastPointId==itRouteFirstPointId) {
				return true;
			}
			
		}
		return false;
	}

	public boolean isLooped() {
		Point firstPoint = routes.get(0).getPoints().get(0);
		Point lastPoint = getLastPoint();
		return firstPoint.getId() == lastPoint.getId();
	}

	public Chemin cloneObject() {
		Chemin chemin = new Chemin();
		chemin.totalDistance = new BigDecimal(totalDistance.toBigInteger());
		chemin.routes = new ArrayList<Route>(routes);
		return chemin;
	}

	@Override
	public String toString() {
		return "Chemin [id=" + id + ", totalDistance=" + totalDistance
				+ ", routes=" + routes + "]";
	}
	
	

}

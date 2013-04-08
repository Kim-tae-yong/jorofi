package ch.shimbawa.jorofi.tools.router4;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ch.shimbawa.jorofi.data.Point;
import ch.shimbawa.jorofi.data.Route;
import ch.shimbawa.jorofi.xml.GpxType;
import ch.shimbawa.jorofi.xml.TrkType;
import ch.shimbawa.jorofi.xml.TrksegType;
import ch.shimbawa.jorofi.xml.WptType;

public class GpxRoutesMergerToolTest {

	Point p1 = new Point(BigDecimal.valueOf(6.50000), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));
	Point p2 = new Point(BigDecimal.valueOf(6.50001), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));
	Point p3 = new Point(BigDecimal.valueOf(6.50002), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));
	Point p4 = new Point(BigDecimal.valueOf(6.50003), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));
	Point p5 = new Point(BigDecimal.valueOf(6.50004), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));
	Point p6 = new Point(BigDecimal.valueOf(6.50005), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));

	@Test
	public void testSimplify2() throws FileNotFoundException {
		Point p1 = new Point(BigDecimal.valueOf(6.559200), BigDecimal.valueOf(46.716216), BigDecimal.valueOf(450));
		Point p2 = new Point(BigDecimal.valueOf(6.559300), BigDecimal.valueOf(46.716133), BigDecimal.valueOf(450));
		Point p3 = new Point(BigDecimal.valueOf(6.559433), BigDecimal.valueOf(46.716033), BigDecimal.valueOf(450));
		Point p4 = new Point(BigDecimal.valueOf(6.559583), BigDecimal.valueOf(46.715933), BigDecimal.valueOf(450));
		Point p5 = new Point(BigDecimal.valueOf(6.559633), BigDecimal.valueOf(46.715883), BigDecimal.valueOf(450));
		Point p6 = new Point(BigDecimal.valueOf(6.559717), BigDecimal.valueOf(46.715833), BigDecimal.valueOf(450));

		Route route1 = buildRoute(p1, p2, p3);
		Route route2 = buildRoute(p3, p4);
		Route route3 = buildRoute(p4, p5, p6);

		List<Route> routes = buildRoutes(route1, route2,route3);
		GpxRoutesMergerTool.simplifyRoutes(routes);
		Assert.assertEquals(1, routes.size());
		List<Point> points = routes.get(0).getPoints();
		Assert.assertEquals(Arrays.asList(p1, p2, p3, p4, p5, p6), points);
	}

	@Test
	public void testSimplifyRoutesRoutesNormal() throws FileNotFoundException {
		Route route1 = buildRoute(p1, p2);
		Route route2 = buildRoute(p2, p3);
		List<Route> routes = buildRoutes(route1, route2);
		GpxRoutesMergerTool.simplifyRoutes(routes);
		Assert.assertEquals(1, routes.size());
		List<Point> points = routes.get(0).getPoints();
		Assert.assertEquals(Arrays.asList(p1, p2, p3), points);
	}

	@Test
	public void testSimplifyRoutesRoutesNormalMany() throws FileNotFoundException {
		Route route1 = buildRoute(p1, p2, p3);
		Route route2 = buildRoute(p3, p4, p5);
		List<Route> routes = buildRoutes(route1, route2);
		GpxRoutesMergerTool.simplifyRoutes(routes);
		Assert.assertEquals(1, routes.size());
		List<Point> points = routes.get(0).getPoints();
		Assert.assertEquals(Arrays.asList(p1, p2, p3, p4, p5), points);
	}

	@Test
	public void testRoutes2Inversed() throws FileNotFoundException {
		Route route1 = buildRoute(p1, p2, p3);
		Route route2 = buildRoute(p5, p4, p3);
		List<Route> routes = buildRoutes(route1, route2);
		GpxRoutesMergerTool.simplifyRoutes(routes);
		Assert.assertEquals(1, routes.size());
		List<Point> points = routes.get(0).getPoints();
		Assert.assertEquals(Arrays.asList(p1, p2, p3, p4, p5), points);
	}

	@Test
	public void testRoutes1Inversed() throws FileNotFoundException {
		Route route1 = buildRoute(p3, p2, p1);
		Route route2 = buildRoute(p3, p4, p5);
		List<Route> routes = buildRoutes(route1, route2);
		GpxRoutesMergerTool.simplifyRoutes(routes);
		Assert.assertEquals(1, routes.size());
		List<Point> points = routes.get(0).getPoints();
		Assert.assertEquals(Arrays.asList(p5, p4, p3, p2, p1), points);
	}

	@Test
	public void testRoutes12Inversed() throws FileNotFoundException {
		Route route1 = buildRoute(p3, p2, p1);
		Route route2 = buildRoute(p5, p4, p3);
		List<Route> routes = buildRoutes(route1, route2);
		GpxRoutesMergerTool.simplifyRoutes(routes);
		Assert.assertEquals(1, routes.size());
		List<Point> points = routes.get(0).getPoints();
		Assert.assertEquals(Arrays.asList(p5, p4, p3, p2, p1), points);
	}

	@Test
	public void testBuildPointsByRoute() {
		Route route1 = buildRoute(p1, p2);
		Route route2 = buildRoute(p3, p2);
		Map<Point, ArrayList<Route>> map = GpxRoutesMergerTool.buildPointsByRoute(buildRoutes(route1, route2));
		Assert.assertEquals(Arrays.asList(route1), map.get(p1));
		Assert.assertEquals(Arrays.asList(route1, route2), map.get(p2));
		Assert.assertEquals(Arrays.asList(route2), map.get(p3));
	}

	@Test
	public void testExistsDirectOrInverse() {
		Route route1 = buildRoute(p1, p2);
		Route route2 = buildRoute(p3, p2);
		Route route3 = buildRoute(p3, p4);
		List<Route> routes = buildRoutes(route1, route2, route3);
		Assert.assertTrue(GpxRoutesMergerTool.existsDirectOrInverse(routes, route1));
		Assert.assertTrue(GpxRoutesMergerTool.existsDirectOrInverse(routes, route1.reverse()));
		Assert.assertTrue(GpxRoutesMergerTool.existsDirectOrInverse(routes, route2));
		Assert.assertTrue(GpxRoutesMergerTool.existsDirectOrInverse(routes, route2.reverse()));
		Assert.assertTrue(GpxRoutesMergerTool.existsDirectOrInverse(routes, route3));
		Assert.assertTrue(GpxRoutesMergerTool.existsDirectOrInverse(routes, route3.reverse()));
		Assert.assertFalse(GpxRoutesMergerTool.existsDirectOrInverse(routes, buildRoute(p1, p3)));
		Assert.assertFalse(GpxRoutesMergerTool.existsDirectOrInverse(routes, buildRoute(p1, p4)));
	}

	@Test
	public void testBreakRoute() {
		Route route1 = buildRoute(p1, p2, p3);
		List<Route> routes = buildRoutes(route1);
		GpxRoutesMergerTool.splitRoute(routes, route1, p2);
		Assert.assertEquals(2, routes.size());
		Assert.assertEquals(Arrays.asList(p1, p2), routes.get(0).getPoints());
		Assert.assertEquals(Arrays.asList(p2, p3), routes.get(1).getPoints());
	}

	@Test
	public void testAddGpxDataSimple() throws FileNotFoundException {
		GpxType gpx = new GpxType();
		gpx.getTrk().add(buildGpxTrk(buildWpt(6.5000, 46.5), buildWpt(6.5005, 46.5), buildWpt(6.5010, 46.5)));

		List<Route> routes = new ArrayList<Route>();
		GpxRoutesMergerTool.addGpxData(routes, gpx);
		Assert.assertEquals(1, routes.size());
		Assert.assertEquals(3, routes.get(0).getPoints().size());
		Assert.assertEquals(76, routes.get(0).getDistance().intValue());
		Assert.assertEquals("P(6.5, 46.5) P(6.5005, 46.5) P(6.501, 46.5) ", buildRouteStr(routes.get(0)));
	}

	@Test
	public void testAddGpxDataOnePointSplitInFour() throws FileNotFoundException {
		GpxType gpx = new GpxType();
		gpx.getTrk().add(buildGpxTrk(buildWpt(6.5000, 46.50), buildWpt(6.5005, 46.50), buildWpt(6.5010, 46.50)));
		gpx.getTrk().add(buildGpxTrk(buildWpt(6.5000, 46.49), buildWpt(6.5006, 46.50), buildWpt(6.5010, 46.51)));

		List<Route> routes = new ArrayList<Route>();
		GpxRoutesMergerTool.addGpxData(routes, gpx);
		Assert.assertEquals(4, routes.size());
		Assert.assertEquals("P(6.5, 46.5) P(6.50055, 46.50) ", buildRouteStr(routes.get(0)));
		Assert.assertEquals("P(6.50055, 46.50) P(6.501, 46.5) ", buildRouteStr(routes.get(1)));
		Assert.assertEquals("P(6.5, 46.49) P(6.50055, 46.50) ", buildRouteStr(routes.get(2)));
		Assert.assertEquals("P(6.50055, 46.50) P(6.501, 46.51) ", buildRouteStr(routes.get(3)));
	}

	@Test
	public void testAddGpxDataAdjacent() throws FileNotFoundException {
		GpxType gpx = new GpxType();
		gpx.getTrk().add(
				buildGpxTrk(buildWpt(6.5000, 46.50), buildWpt(6.5005, 46.50), buildWpt(6.5010, 46.50),
						buildWpt(6.5015, 46.50), buildWpt(6.5020, 46.50)));
		gpx.getTrk().add(
				buildGpxTrk(buildWpt(6.5000, 46.49000), buildWpt(6.5004, 46.49999), buildWpt(6.5011, 46.49999),
						buildWpt(6.5015, 46.49000), buildWpt(6.5020, 46.50000)));

		List<Route> routes = new ArrayList<Route>();
		GpxRoutesMergerTool.addGpxData(routes, gpx);
		Assert.assertEquals("P(6.5, 46.5) P(6.50045, 46.499995) ", buildRouteStr(routes.get(0)));
		Assert.assertEquals("P(6.50045, 46.499995) P(6.50105, 46.499995) ", buildRouteStr(routes.get(1)));
		Assert.assertEquals("P(6.5, 46.49) P(6.50045, 46.499995) ", buildRouteStr(routes.get(2)));
		Assert.assertEquals("P(6.50105, 46.499995) P(6.5015, 46.5) P(6.5020, 46.50) ", buildRouteStr(routes.get(3)));
		Assert.assertEquals("P(6.50105, 46.499995) P(6.5015, 46.49) P(6.5020, 46.50) ", buildRouteStr(routes.get(4)));
		Assert.assertEquals(5, routes.size());
	}

	@Test
	public void testAddGpxDataAdjacentInverse() throws FileNotFoundException {
		GpxType gpx = new GpxType();
		gpx.getTrk().add(
				buildGpxTrk(buildWpt(6.5000, 46.50), buildWpt(6.5005, 46.50), buildWpt(6.5010, 46.50),
						buildWpt(6.5015, 46.50), buildWpt(6.5020, 46.50)));
		gpx.getTrk().add(
				buildGpxTrk(buildWpt(6.5020, 46.50000), buildWpt(6.5015, 46.49000), buildWpt(6.5011, 46.49999),
						buildWpt(6.5004, 46.49999), buildWpt(6.5000, 46.49000)));

		List<Route> routes = new ArrayList<Route>();
		GpxRoutesMergerTool.addGpxData(routes, gpx);
		Assert.assertEquals("P(6.5, 46.5) P(6.50045, 46.499995) ", buildRouteStr(routes.get(0)));
		Assert.assertEquals("P(6.50105, 46.499995) P(6.5015, 46.5) P(6.5020, 46.50) ", buildRouteStr(routes.get(1)));
		Assert.assertEquals("P(6.5020, 46.50) P(6.5015, 46.49) P(6.50105, 46.499995) ", buildRouteStr(routes.get(2)));
		Assert.assertEquals("P(6.50045, 46.499995) P(6.50105, 46.499995) ", buildRouteStr(routes.get(3)));
		Assert.assertEquals("P(6.50045, 46.499995) P(6.5, 46.49) ", buildRouteStr(routes.get(4)));
		Assert.assertEquals(5, routes.size());
	}

	private String buildRouteStr(Route route) {
		StringBuilder sb = new StringBuilder();
		for (Point point : route.getPoints()) {
			sb.append("P(");
			sb.append(point.getLongitude());
			sb.append(", ");
			sb.append(point.getLatitude());
			sb.append(") ");
		}
		return sb.toString();
	}

	private TrkType buildGpxTrk(WptType... wptList) {
		TrksegType trkSeg = new TrksegType();
		for (WptType wpt : wptList) {
			trkSeg.getTrkpt().add(wpt);
		}
		TrkType trk = new TrkType();
		trk.getTrkseg().add(trkSeg);
		return trk;
	}

	private WptType buildWpt(double lon, double lat) {
		WptType wpt = new WptType();
		wpt.setLon(BigDecimal.valueOf(lon));
		wpt.setLat(BigDecimal.valueOf(lat));
		return wpt;
	}

	private Route buildRoute(Point... points) {
		Route route = new Route();
		for (Point point : points) {
			route.addPoint(point);
		}
		return route;
	}

	private List<Route> buildRoutes(Route... routes) {
		// Arrays.asList() does not work as AbstractList does not implement
		// remove()
		List<Route> routeList = new ArrayList<Route>(routes.length);
		for (Route route : routes) {
			routeList.add(route);
		}
		return routeList;
	}

}

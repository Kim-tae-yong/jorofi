package ch.shimbawa.jorofi.data;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

public class RouteTest {

	Point p1 = new Point(BigDecimal.valueOf(6.50000), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));
	Point p2 = new Point(BigDecimal.valueOf(6.50001), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));
	Point p3 = new Point(BigDecimal.valueOf(6.50002), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));
	Point p4 = new Point(BigDecimal.valueOf(6.50003), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));
	Point p5 = new Point(BigDecimal.valueOf(6.50004), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));
	Point p6 = new Point(BigDecimal.valueOf(6.50005), BigDecimal.valueOf(46.5), BigDecimal.valueOf(450));

	@Test
	public void testIsSamePoints() {
		Route route1 = buildRoute(p1, p2, p3);
		Route route2 = buildRoute(p1, p2, p3);
		Route route3 = buildRoute(p1, p3, p2);
		Route route4 = buildRoute(p3, p2, p1);
		Route route5 = buildRoute(p1, p2);
		Assert.assertTrue(route1.isSamePoints(route1));
		Assert.assertTrue(route1.isSamePoints(route2));
		Assert.assertTrue(route1.isSamePoints(route4.reverse()));
		Assert.assertFalse(route1.isSamePoints(route3));
		Assert.assertFalse(route1.isSamePoints(route4));
		Assert.assertFalse(route1.isSamePoints(route5));
		Assert.assertFalse(route5.isSamePoints(route1));
	}

	@Test
	public void testHasPoint() {
		Route route1 = buildRoute(p1, p2, p3);
		Assert.assertTrue(route1.hasPoint(p1));
		Assert.assertTrue(route1.hasPoint(p2));
		Assert.assertTrue(route1.hasPoint(p3));
		Assert.assertFalse(route1.hasPoint(p4));
	}

	@Test
	public void testSplitAt() {
		Route route1 = buildRoute(p1, p2, p3, p4, p5);
		Route newRoute = route1.splitAt(p3);
		Assert.assertTrue(route1.isSamePoints(buildRoute(p1, p2, p3)));
		Assert.assertTrue(newRoute.isSamePoints(buildRoute(p3, p4, p5)));
	}

	@Test
	public void testSplitAtStart() {
		Route route1 = buildRoute(p1, p2, p3, p4, p5);
		Route newRoute = route1.splitAt(p1);
		Assert.assertTrue(route1.isSamePoints(buildRoute(p1, p2, p3, p4, p5)));
		Assert.assertNull(newRoute);
	}

	@Test
	public void testSplitAtEnd() {
		Route route1 = buildRoute(p1, p2, p3, p4, p5);
		Route newRoute = route1.splitAt(p5);
		Assert.assertTrue(route1.isSamePoints(buildRoute(p1, p2, p3, p4, p5)));
		Assert.assertNull(newRoute);
	}

	@Test
	public void testInsertPointFirst() {
		Route route1 = buildRoute(p3, p4, p5);
		Assert.assertTrue(route1.isSamePoints(buildRoute(p3, p4, p5)));
		route1.insertPointFirst(p2);
		Assert.assertTrue(route1.isSamePoints(buildRoute(p2, p3, p4, p5)));
		route1.insertPointFirst(p1);
		Assert.assertTrue(route1.isSamePoints(buildRoute(p1, p2, p3, p4, p5)));
	}

	@Test
	public void testGetFirstPoint() {
		Route route1 = buildRoute(p1, p2, p3, p4, p5);
		Assert.assertEquals(p1, route1.getFirstPoint());
	}

	@Test
	public void testGetLastPoint() {
		Route route1 = buildRoute(p1, p2, p3, p4, p5);
		Assert.assertEquals(p5, route1.getLastPoint());
	}

	@Test
	public void testGetDistance() {
		Route route1 = buildRoute(p1, p2);
		Assert.assertEquals(0.7676, route1.getDistance().doubleValue(), 0.0001);
		route1.addPoint(p3);
		Assert.assertEquals(0.7676 * 2, route1.getDistance().doubleValue(), 0.0001);
	}

	private Route buildRoute(Point... points) {
		Route route = new Route();
		for (Point point : points) {
			route.addPoint(point);
		}
		return route;
	}

}

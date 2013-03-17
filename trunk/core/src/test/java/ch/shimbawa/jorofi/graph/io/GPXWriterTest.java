package ch.shimbawa.jorofi.graph.io;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import ch.shimbawa.jorofi.ConsoleLogListener;
import ch.shimbawa.jorofi.data.CheminsFinderResponse;
import ch.shimbawa.jorofi.data.ClientRequest;
import ch.shimbawa.jorofi.data.Point;
import ch.shimbawa.jorofi.data.Route;
import ch.shimbawa.jorofi.graph.Chemin;
import ch.shimbawa.jorofi.io.GPXWriter;

public class GPXWriterTest {
	
	@Test
	public void testWrite() {
		Point point1 = createPoint("point1", 6.65,46.65,444);
		Point point2 = createPoint("point2", 6.75,46.75,432);
		Route route1 = new Route();
		route1.addPoint(createPoint("p3", 6.00,46.00,400));
		route1.addPoint(createPoint("p4", 6.10,46.20,400));
		Route route2 = new Route();
		route2.addPoint(createPoint("p5", 6.20,46.00,400));
		route2.addPoint(createPoint("p6", 6.30,46.20,400));
		Route route3 = new Route();
		route3.addPoint(createPoint("p7", 6.40,46.00,400));
		route3.addPoint(createPoint("p8", 6.50,46.20,400));

		Chemin chemin1 = new Chemin();
		chemin1.addRoute(route1);
		chemin1.addRoute(route2);
		Chemin chemin2 = new Chemin();
		chemin2.addRoute(route3);
		
		CheminsFinderResponse response = new CheminsFinderResponse();
		response.setPoints(Arrays.asList(point1, point2));
		response.setChemins(Arrays.asList(chemin1, chemin2));
		
		ClientRequest clientRequest = new ClientRequest();
		clientRequest.setLogListener(new ConsoleLogListener());
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();		
		new GPXWriter().write(response, os, clientRequest);
		Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" version=\"1.1\" creator=\"Jorofi\"><wpt lon=\"6.65\" lat=\"46.65\"><ele>444</ele><name>point1</name></wpt><wpt lon=\"6.75\" lat=\"46.75\"><ele>432</ele><name>point2</name></wpt><rte><name>chemin-1</name><rtept lon=\"6.0\" lat=\"46.0\"><ele>400</ele><name>p3</name></rtept><rtept lon=\"6.1\" lat=\"46.2\"><ele>400</ele><name>p4</name></rtept><rtept lon=\"6.2\" lat=\"46.0\"><ele>400</ele><name>p5</name></rtept><rtept lon=\"6.3\" lat=\"46.2\"><ele>400</ele><name>p6</name></rtept></rte><rte><name>chemin-2</name><rtept lon=\"6.4\" lat=\"46.0\"><ele>400</ele><name>p7</name></rtept><rtept lon=\"6.5\" lat=\"46.2\"><ele>400</ele><name>p8</name></rtept></rte></gpx>", new String(os.toByteArray()));
	}
	
	private Point createPoint(String name, double lon, double lat, int alt) {
		Point point = new Point();
		point.setName(name);
		point.setLongitude(BigDecimal.valueOf(lon));
		point.setLatitude(BigDecimal.valueOf(lat));
		point.setAltitude(BigDecimal.valueOf(alt));
		return point;
	}

}

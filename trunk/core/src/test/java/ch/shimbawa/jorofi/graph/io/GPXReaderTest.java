package ch.shimbawa.jorofi.graph.io;

import org.junit.Assert;
import org.junit.Test;

import ch.shimbawa.jorofi.data.Dataset;
import ch.shimbawa.jorofi.data.Point;
import ch.shimbawa.jorofi.io.GPXReader;

public class GPXReaderTest {
	
	@Test
	public void testRead() {
		Dataset data = new GPXReader().read(getClass().getResourceAsStream("/small1.gpx"));
		Assert.assertEquals(5, data.getPoints().size());
		
		Point point = data.getPoints().get(0);
		Assert.assertEquals(1l, point.getId());
		Assert.assertEquals("6.5682824", point.getLongitude().toString());
		Assert.assertEquals("46.7100522", point.getLatitude().toString());
		Assert.assertEquals("447.0000000", point.getAltitude().toString());
		Assert.assertEquals("gare", point.getName().toString());
	}

}

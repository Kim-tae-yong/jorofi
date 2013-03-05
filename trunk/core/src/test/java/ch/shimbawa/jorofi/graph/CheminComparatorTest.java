package ch.shimbawa.jorofi.graph;

import java.math.BigDecimal;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import ch.shimbawa.jorofi.graph.Chemin;
import ch.shimbawa.jorofi.graph.CheminComparator;

public class CheminComparatorTest {
	
	@Test
	public void testX() {
		TreeSet<Chemin> chemins = new TreeSet<Chemin>(new CheminComparator());
		
		Chemin chemin1 = new Chemin();
		chemin1.totalDistance=BigDecimal.valueOf(10);
		Chemin chemin2 = new Chemin();
		chemin2.totalDistance=BigDecimal.valueOf(20);
		Chemin chemin3 = new Chemin();
		chemin3.totalDistance=BigDecimal.valueOf(5);
		chemins.add(chemin1);
		chemins.add(chemin2);
		chemins.add(chemin3);
		Assert.assertEquals(chemin3, chemins.pollFirst());
		Assert.assertEquals(chemin1, chemins.pollFirst());
		Assert.assertEquals(chemin2, chemins.pollFirst());
	}

}

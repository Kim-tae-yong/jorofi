package ch.shimbawa.jorofi.graph;

import java.util.Comparator;

/** Comparator for chemin: the shortest first. */
public class CheminComparator implements Comparator<Chemin> {

	public int compare(Chemin c1, Chemin c2) {
		int cmp = c1.getTotalDistance().compareTo(c2.getTotalDistance());
		if (cmp != 0) {
			return cmp;
		}
		return (int) (c1.getId() - c2.getId());
	}

}

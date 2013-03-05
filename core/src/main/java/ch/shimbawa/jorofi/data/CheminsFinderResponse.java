package ch.shimbawa.jorofi.data;

import java.util.List;

import ch.shimbawa.jorofi.graph.Chemin;

/** Chemins found */
public class CheminsFinderResponse {

	private List<Point> points;
	private List<Chemin> chemins;

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public List<Chemin> getChemins() {
		return chemins;
	}

	public void setChemins(List<Chemin> chemins) {
		this.chemins = chemins;
	}

}

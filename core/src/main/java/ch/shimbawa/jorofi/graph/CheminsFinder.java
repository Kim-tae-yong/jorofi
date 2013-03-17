package ch.shimbawa.jorofi.graph;

import java.util.ArrayList;
import java.util.TreeSet;

import ch.shimbawa.jorofi.data.CheminsFinderResponse;
import ch.shimbawa.jorofi.data.ClientRequest;
import ch.shimbawa.jorofi.data.Dataset;
import ch.shimbawa.jorofi.data.Point;
import ch.shimbawa.jorofi.data.Route;

/**
 * Core class to search for chemins. Principes:
 * <ul>
 * <li>Si un chemin est bouclé, il est terminé</li>
 * <li>Un chemin non bouclé ne peut contenir qu'une seule fois une même route</li>
 * </ul>
 * 
 * @author dutoitc
 */
public class CheminsFinder {

	public CheminsFinderResponse find(Dataset data, ClientRequest clientRequest) {
		if (clientRequest.isVerbose()) {
			clientRequest.getLogListener().message("Finding chemin");
		}
		TreeSet<Chemin> cheminsAParcourir = new TreeSet<Chemin>(
				new CheminComparator());
		TreeSet<Chemin> cheminsTermines = new TreeSet<Chemin>(
				new CheminComparator());

		// Initialisation:
		// Chemins = liste des chemins partant d'un point de départ (nom start-*
		// et toutes les routes liées)
		for (Point itPoint : data.getStartPoints()) {
			for (Route route : data.getRoutesFromPoint(itPoint)) {
				Chemin chemin = new Chemin();
				chemin.addRoute(route);
				cheminsAParcourir.add(chemin);
				// dumpChemin(chemin);
			}
		}
		if (clientRequest.isVerbose()) {
			clientRequest.getLogListener().message(
					"Found " + cheminsAParcourir.size()
							+ " chemins from starting points");
		}

		// Recherche:
		// Tant que (itChemin = CheminsAParcourir.pop(0)):
		Chemin itChemin;
		boolean authorizeInverseRoute = false; // Trop de bruit !?
		while ((itChemin = cheminsAParcourir.pollFirst()) != null
				&& cheminsTermines.size() < clientRequest.getNbLimits()) {
			if (clientRequest.isVerbose()) {
				clientRequest.getLogListener().message(
						"Continuing search with found="
								+ cheminsTermines.size() + ", todo="
								+ cheminsAParcourir.size());
			}
			// Create new chemins: add ll routes from last chemin's point
			for (Route route : data.getRoutesFromPoint(itChemin.getLastPoint())) {
				if (!itChemin.hasRoute(route)
						&& (authorizeInverseRoute || !itChemin
								.hasRouteInverse(route))) {
					Chemin cheminNew = itChemin.cloneObject();
					cheminNew.addRoute(route);
					if (cheminNew.isLooped()) {
						if (clientRequest.shouldKeepChemin(cheminNew)) {
							cheminsTermines.add(cheminNew);
						} // Else no selection
					} else {
						cheminsAParcourir.add(cheminNew);
					}

				}
			}
		}

		clientRequest.getLogListener().message(
				"Found " + cheminsTermines.size() + " chemins.");

		if (clientRequest.isVerbose()) {
			for (Chemin chemin : cheminsTermines) {
				clientRequest.getLogListener().message(
						"Found chemin with dist=" + chemin.getTotalDistance());
				dumpChemin(chemin, clientRequest);
			}
		}

		CheminsFinderResponse response = new CheminsFinderResponse();
		response.setPoints(data.getPoints());
		response.setChemins(new ArrayList<Chemin>(cheminsTermines));

		return response;
	}

	private void dumpChemin(Chemin chemin, ClientRequest clientRequest) {
		clientRequest.getLogListener().message(
				"Chemin " + chemin.getId() + ", dist="
						+ chemin.getTotalDistance());
		for (Route itRoute : chemin.getRoutes()) {
			clientRequest.getLogListener().message(
					"   route: " + itRoute.getId() + ", dist="
							+ itRoute.getDistance());
			for (Point itPoint : itRoute.getPoints()) {
				clientRequest.getLogListener().message(
						"    point: " + itPoint.getId() + ", name="
								+ itPoint.getName());
			}
		}
	}
}

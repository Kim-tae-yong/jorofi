package ch.shimbawa.jorofi.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import ch.shimbawa.jorofi.data.ClientRequest;
import ch.shimbawa.jorofi.data.Dataset;
import ch.shimbawa.jorofi.data.NamedPoint;
import ch.shimbawa.jorofi.data.Route;
import ch.shimbawa.jorofi.xml.GpxType;
import ch.shimbawa.jorofi.xml.RteType;
import ch.shimbawa.jorofi.xml.WptType;

/** Read GPX Data */
public class GPXReader {


	public Dataset read(ClientRequest clientRequest) throws FileNotFoundException {		
		if (clientRequest.isVerbose()) {
			clientRequest.getLogListener().message("Reading GPX file: " + clientRequest.getInputFilename());
		}
		return read(new FileInputStream(clientRequest.getInputFilename()));
	}
	
	public Dataset read(InputStream is) {
		Dataset data = new Dataset();
		GpxType gpx = unmarshall(is);
		readWaypoints(data, gpx);
		readRoutes(data, gpx);
		return data;
	}

	private GpxType unmarshall(InputStream is) {
		GpxType gpx = null;
		try {
			JAXBContext jc = JAXBContext.newInstance("ch.shimbawa.jorofi.xml");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			@SuppressWarnings("unchecked")
			JAXBElement<GpxType> root = (JAXBElement<GpxType>) unmarshaller.unmarshal(is);
			gpx = root.getValue();
		} catch (JAXBException ex) {
			throw new RuntimeException(ex);
		}
		return gpx;
	}

	private void readRoutes(Dataset data, GpxType gpx) {
		for (RteType route : gpx.getRte()) {
			Route myroute = new Route();
			for (WptType wp : route.getRtept()) {
				NamedPoint point = data.findOrAddNearestPoint(wp.getLon(), wp.getLat(), wp.getEle(), wp.getName());
				if (!myroute.hasPoint(point)) {
					// could be two points on one grouped by distance
					myroute.addPoint(point);
				}
			}

			// Check data quality: a route must have two points minimum.
			if (myroute.getPoints().size() > 1) {
				data.addRoute(myroute);
				data.addRoute(myroute.reverse());
			}

		}
	}

	private void readWaypoints(Dataset data, GpxType gpx) {
		for (WptType wp : gpx.getWpt()) {
			NamedPoint point = new NamedPoint();
			point.setLongitude(wp.getLon());
			point.setLatitude(wp.getLat());
			point.setAltitude(wp.getEle());
			point.setName(wp.getName());
			data.addPoint(point);
		}
	}

}

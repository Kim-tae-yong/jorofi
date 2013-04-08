package ch.shimbawa.jorofi.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;

import ch.shimbawa.jorofi.data.CheminsFinderResponse;
import ch.shimbawa.jorofi.data.ClientRequest;
import ch.shimbawa.jorofi.data.NamedPoint;
import ch.shimbawa.jorofi.data.Point;
import ch.shimbawa.jorofi.data.Route;
import ch.shimbawa.jorofi.graph.Chemin;
import ch.shimbawa.jorofi.xml.GpxType;
import ch.shimbawa.jorofi.xml.ObjectFactory;
import ch.shimbawa.jorofi.xml.RteType;
import ch.shimbawa.jorofi.xml.TrkType;
import ch.shimbawa.jorofi.xml.TrksegType;
import ch.shimbawa.jorofi.xml.WptType;

/** Write GPX data to file */
public class GPXWriter {

	public void write(CheminsFinderResponse response, ClientRequest clientRequest) throws FileNotFoundException {
		if (clientRequest.isVerbose()) {
			clientRequest.getLogListener().message("Writing GPX file...");
		}
		write(response, new FileOutputStream(clientRequest.getOutputFilename()), clientRequest);
		clientRequest.getLogListener().message("Wrote GPX file: " + clientRequest.getOutputFilename());
	}

	public void write(CheminsFinderResponse response, OutputStream os, ClientRequest clientRequest) {
		GpxType gpx = initGPX();
		writePoints(response, gpx);
		writeChemins(response, gpx);

		marshall(gpx, os, clientRequest);
	}

	private void marshall(GpxType gpx, OutputStream os, ClientRequest clientRequest) {
		JAXBElement<GpxType> gpxFile = new ObjectFactory().createGpx(gpx);
		try {
			JAXBContext jc = JAXBContext.newInstance("ch.shimbawa.jorofi.xml");
			Marshaller marshaller = jc.createMarshaller();

			ValidationEventCollector validationEventHandler = new ValidationEventCollector();
			marshaller.setEventHandler(validationEventHandler);
			marshaller.marshal(gpxFile, os);

			if (clientRequest != null) {
				for (ValidationEvent event : validationEventHandler.getEvents()) {
					clientRequest.getLogListener().message("Validation error: " + event.getMessage());
				}
			}
			os.close();

		} catch (JAXBException ex) {
			throw new RuntimeException(ex);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private GpxType initGPX() {
		GpxType gpx = new GpxType();
		gpx.setCreator("Jorofi");
		gpx.setVersion("1.1");
		// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		// xsi:schemaLocation="http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensions/v3/GpxExtensionsv3.xsd http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd"
		return gpx;
	}

	private void writeChemins(CheminsFinderResponse response, GpxType gpx) {
		for (Chemin chemin : response.getChemins()) {
			RteType gpxRoute = new RteType();
			gpxRoute.setName("chemin-" + chemin.getId());

			long lastPointId = -1;
			for (Route route : chemin.getRoutes()) {
				for (Point point : route.getPoints()) {
					if (point.getId() != lastPointId) {
						WptType gpxWpt = buildWptType(point);
						gpxRoute.getRtept().add(gpxWpt);
					}
					lastPointId = point.getId();
				}
			}
			gpx.getRte().add(gpxRoute);
		}
	}

	private WptType buildWptType(Point point) {
		WptType gpxWpt = new WptType();
		gpxWpt.setLon(point.getLongitude());
		gpxWpt.setLat(point.getLatitude());
		gpxWpt.setEle(point.getAltitude());
		if (point instanceof NamedPoint) {
			gpxWpt.setName(((NamedPoint) point).getName());
		}
		return gpxWpt;
	}

	private void writePoints(CheminsFinderResponse response, GpxType gpx) {
		for (NamedPoint point : response.getPoints()) {
			WptType gpxWpt = new WptType();
			gpxWpt.setLon(point.getLongitude());
			gpxWpt.setLat(point.getLatitude());
			gpxWpt.setEle(point.getAltitude());
			gpxWpt.setName(point.getName());
			gpx.getWpt().add(gpxWpt);
		}
	}

	public void write(List<Route> routes, OutputStream os) {
		write(routes, os, false);
	}

	public void write(List<Route> routes, OutputStream os, boolean writeNames) {
		GpxType gpx = initGPX();
		for (Route route: routes) {
			TrkType trk = new TrkType();
			if (writeNames) {
				trk.setName("R" + route.getId());
			}			
			gpx.getTrk().add(trk);
			
			TrksegType trkSeg = new TrksegType();
			trk.getTrkseg().add(trkSeg);
			
			List<WptType> trkpt = trkSeg.getTrkpt();
			for (Point point: route.getPoints()) {
				WptType gpxWpt = buildWptType(point);
				if (writeNames) {
					gpxWpt.setName("W" + point.getId());
				}
				trkpt.add(gpxWpt);
			}
		}

		marshall(gpx, os, null);	
	}

}

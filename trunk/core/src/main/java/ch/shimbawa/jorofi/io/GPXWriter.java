package ch.shimbawa.jorofi.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;

import ch.shimbawa.jorofi.data.CheminsFinderResponse;
import ch.shimbawa.jorofi.data.ClientRequest;
import ch.shimbawa.jorofi.data.Point;
import ch.shimbawa.jorofi.data.Route;
import ch.shimbawa.jorofi.graph.Chemin;
import ch.shimbawa.jorofi.xml.GpxType;
import ch.shimbawa.jorofi.xml.ObjectFactory;
import ch.shimbawa.jorofi.xml.RteType;
import ch.shimbawa.jorofi.xml.WptType;

/** Write GPX data to file */
public class GPXWriter {

	public void write(CheminsFinderResponse response,
			ClientRequest clientRequest) throws FileNotFoundException {
		if (clientRequest.isVerbose()) {
			clientRequest.getLogListener().message("Writing GPX file...");
		}
		write(response,
				new FileOutputStream(clientRequest.getOutputFilename()),
				clientRequest);
		clientRequest.getLogListener().message(
				"Wrote GPX file: " + clientRequest.getOutputFilename());
	}

	public void write(CheminsFinderResponse response, OutputStream os,
			ClientRequest clientRequest) {
		GpxType gpx = initGPX();
		writePoints(response, gpx);
		writeChemins(response, gpx);

		marshall(gpx, os, clientRequest);
	}

	private void marshall(GpxType gpx, OutputStream os,
			ClientRequest clientRequest) {
		JAXBElement<GpxType> gpxFile = new ObjectFactory().createGpx(gpx);
		try {
			JAXBContext jc = JAXBContext.newInstance("ch.shimbawa.jorofi.xml");
			Marshaller marshaller = jc.createMarshaller();

			ValidationEventCollector validationEventHandler = new ValidationEventCollector();
			marshaller.setEventHandler(validationEventHandler);
			marshaller.marshal(gpxFile, os);

			for (ValidationEvent event : validationEventHandler.getEvents()) {
				clientRequest.getLogListener().message(
						"Validation error: " + event.getMessage());
			}

		} catch (JAXBException ex) {
			throw new RuntimeException(ex);
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
						WptType gpxWpt = new WptType();
						gpxWpt.setLon(point.getLongitude());
						gpxWpt.setLat(point.getLatitude());
						gpxWpt.setEle(point.getAltitude());
						gpxWpt.setName(point.getName());
						gpxRoute.getRtept().add(gpxWpt);
					}
					lastPointId = point.getId();
				}
			}
			gpx.getRte().add(gpxRoute);
		}
	}

	private void writePoints(CheminsFinderResponse response, GpxType gpx) {
		for (Point point : response.getPoints()) {
			WptType gpxWpt = new WptType();
			gpxWpt.setLon(point.getLongitude());
			gpxWpt.setLat(point.getLatitude());
			gpxWpt.setEle(point.getAltitude());
			gpxWpt.setName(point.getName());
			gpx.getWpt().add(gpxWpt);
		}
	}

}

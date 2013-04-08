package ch.shimbawa.jorofi.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;

import ch.shimbawa.jorofi.xml.GpxType;
import ch.shimbawa.jorofi.xml.ObjectFactory;

public class GPXHelper {

	public static GpxType unmarshall(InputStream is) {
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

	public static void marshall(GpxType gpx, OutputStream os) {
		JAXBElement<GpxType> gpxFile = new ObjectFactory().createGpx(gpx);
		try {
			JAXBContext jc = JAXBContext.newInstance("ch.shimbawa.jorofi.xml");
			Marshaller marshaller = jc.createMarshaller();

			ValidationEventCollector validationEventHandler = new ValidationEventCollector();
			marshaller.setEventHandler(validationEventHandler);
			marshaller.marshal(gpxFile, os);

			for (ValidationEvent event : validationEventHandler.getEvents()) {
				System.out.println("Validation error: " + event.getMessage());
			}
			os.close();

		} catch (JAXBException ex) {
			throw new RuntimeException(ex);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

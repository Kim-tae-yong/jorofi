package ch.shimbawa.jorofi.tools.transformer;

import ch.shimbawa.jorofi.xml.GpxType;

public interface GPXTransformer {

	void transform(GpxType gpxIn, GpxType gpxOut);

}
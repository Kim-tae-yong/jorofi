package ch.shimbawa.jorofi.tools.common;

import java.math.BigDecimal;

import ch.shimbawa.jorofi.tools.data.Point;
import ch.shimbawa.jorofi.xml.WptType;

public class TypeCodec {

	public static WptType buildWpt(Point point) {
		WptType wpt1 = new WptType();
		wpt1.setLon(BigDecimal.valueOf(point.lon));
		wpt1.setLat(BigDecimal.valueOf(point.lat));
		wpt1.setEle(BigDecimal.valueOf(point.ele));
		return wpt1;
	}

}

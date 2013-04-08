package ch.shimbawa.jorofi.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ch.shimbawa.jorofi.common.GeoHelper;

public class Point {

	private static final BigDecimal HALF = BigDecimal.valueOf(0.5);
	protected BigDecimal longitude;
	protected BigDecimal latitude;
	protected BigDecimal altitude;

	protected static long nextId = 1;
	protected long id;

	// private static List<Point> dbgPoints = new ArrayList<Point>();

	public Point() {
		super();
		id = nextId++;
	}

	public Point(BigDecimal longitude, BigDecimal latitude, BigDecimal altitude) {
		super();
		id = nextId++;
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		// if (dbgPoints.contains(this)) {
		// throw new RuntimeException("Duplicate points: coding error !");
		// }
		// dbgPoints.add(this);
	}

	public long getId() {
		return id;
	}

	public double distanceTo(Point point) {
		return GeoHelper.distVincenty(point.latitude.doubleValue(), point.longitude.doubleValue(),
				this.latitude.doubleValue(), this.longitude.doubleValue());
	}

	public double distanceTo(BigDecimal lon, BigDecimal lat) {
		return GeoHelper.distVincenty(lat.doubleValue(), lon.doubleValue(), this.latitude.doubleValue(),
				this.longitude.doubleValue());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((altitude == null) ? 0 : altitude.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (altitude == null) {
			if (other.altitude != null)
				return false;
		} else if (!altitude.equals(other.altitude))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		return true;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getAltitude() {
		return altitude;
	}

	public void setAltitude(BigDecimal altitude) {
		this.altitude = altitude;
	}

	@Override
	public String toString() {
		return "Point [id=" + id + "]";
	}

	public void moveMiddleDistTo(Point point) {
		longitude = longitude.add(point.longitude.subtract(longitude).multiply(HALF));
		latitude = latitude.add(point.latitude.subtract(latitude).multiply(HALF));
		if (altitude != null && point.altitude != null) {
			altitude = altitude.add(point.altitude.subtract(altitude).multiply(HALF));
		}
		// if (dbgPoints.contains(this)) {
		// throw new RuntimeException("Duplicate points: coding error !");
		// }
	}

}
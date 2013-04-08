package ch.shimbawa.jorofi.tools.data;

public class Point {
	public double lon;
	public double lat;
	public double ele;

	public Point(double lon, double lat, double ele) {
		this.lon = lon;
		this.lat = lat;
		this.ele = ele;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(ele);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (Double.doubleToLongBits(ele) != Double.doubleToLongBits(other.ele))
			return false;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
			return false;
		return true;
	}

	public double distanceTo(Point other) {
		return distVincenty(other.lat, other.lon, this.lat, this.lon);
	}

	/**
	 * Calculates geodetic distance between two points specified by
	 * latitude/longitude using Vincenty inverse formula for ellipsoids
	 * 
	 * @param lat1
	 *            first point latitude in decimal degrees
	 * @param lon1
	 *            first point longitude in decimal degrees
	 * @param lat2
	 *            second point latitude in decimal degrees
	 * @param lon2
	 *            second point longitude in decimal degrees
	 * @returns distance in meters between points with 5.10<sup>-4</sup>
	 *          precision
	 * @see <a
	 *      href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">Originally
	 *      posted here</a>
	 */
	// taken from http://fr.softuses.com/11813
	private static double distVincenty(double lat1, double lon1, double lat2, double lon2) {
		double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563; // WGS-84
																		// ellipsoid
																		// params
		double L = Math.toRadians(lon2 - lon1);
		double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
		double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
		double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
		double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

		double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
		double lambda = L, lambdaP, iterLimit = 100;
		do {
			sinLambda = Math.sin(lambda);
			cosLambda = Math.cos(lambda);
			sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
					+ (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
			if (sinSigma == 0)
				return 0; // co-incident points
			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
			sigma = Math.atan2(sinSigma, cosSigma);
			sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
			cosSqAlpha = 1 - sinAlpha * sinAlpha;
			cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
			if (Double.isNaN(cos2SigmaM))
				cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (§6)
			double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
			lambdaP = lambda;
			lambda = L + (1 - C) * f * sinAlpha
					* (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
		} while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

		if (iterLimit == 0)
			return Double.NaN; // formula failed to converge

		double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
		double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
		double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
		double deltaSigma = B
				* sinSigma
				* (cos2SigmaM + B
						/ 4
						* (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
								* (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
		double dist = b * A * (sigma - deltaSigma);

		return dist;
	}

	public double angleTo(Point point2) {
		return Math.atan2(point2.lat-lat, point2.lon-lon)*180/Math.PI;
	}

	public void moveMiddleDistanceTo(Point point) {
		lon = lon*0.5 + point.lon*0.5;
		lat = lat*0.5 + point.lat*0.5;
		ele = ele*0.5 + point.ele*0.5;
	}

}

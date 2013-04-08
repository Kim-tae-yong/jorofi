package ch.shimbawa.jorofi.tools.data;

public class Arrete {

	private Point point1, point2;

	public Arrete(Point point1, Point point2) {
		super();
		this.point1 = point1;
		this.point2 = point2;
	}

	public Point getPoint1() {
		return point1;
	}

	public void setPoint1(Point point1) {
		this.point1 = point1;
	}

	public Point getPoint2() {
		return point2;
	}

	public void setPoint2(Point point2) {
		this.point2 = point2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((point1 == null) ? 0 : point1.hashCode());
		result = prime * result + ((point2 == null) ? 0 : point2.hashCode());
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
		Arrete other = (Arrete) obj;
		if (point1 == null) {
			if (other.point1 != null)
				return false;
		} else if (!point1.equals(other.point1))
			return false;
		if (point2 == null) {
			if (other.point2 != null)
				return false;
		} else if (!point2.equals(other.point2))
			return false;
		return true;
	}

	public Point getMiddlePoint() {
		return new Point(point1.lon * 0.5 + point2.lon * 0.5, point1.lat * 0.5 + point2.lat * 0.5, point1.ele * 0.5
				+ point2.ele * 0.5);
	}

	public Arrete reverse() {
		return new Arrete(point2, point1);
	}

	/** Get angle in degrees from point 1 to point 2 */
	public double getAngle12() {
		return point1.angleTo(point2);
	}

}

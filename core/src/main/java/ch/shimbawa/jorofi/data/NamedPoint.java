package ch.shimbawa.jorofi.data;

/** A point: name, geographic position */
public class NamedPoint extends Point {

	private String name;

	public NamedPoint() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamedPoint other = (NamedPoint) obj;
		if (!name.equals(other.name)) {
			return false;
		}
		return super.equals(obj);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "NamedPoint [id=" + id + ", name=" + name + "]";
	}

}

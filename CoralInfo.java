package coral;

import coral.BlockCoral.CORAL_TYPE;

public class CoralInfo {
	Point3D location;
	CORAL_TYPE type;
	
	public String toString() {
		return "i"+type+"@"+location.toPoint();
	}
}

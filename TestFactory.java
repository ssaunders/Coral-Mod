package coral;

import net.minecraft.util.Facing;
import coral.BlockCoral.CORAL_TYPE;

public class TestFactory {
	private static String facility;
	public static void setTestFacility(String fac) {
		facility = fac;
	}

	public static TestConfig getLineTest(int length, int eq, CORAL_TYPE type) {
		Point3D dims = CoralCommandBlock.getDims();
		int halfX = dims.x/2;
		int halfZ = dims.z/2;
		TestConfig t = new TestConfig("LIN", length, eq, "Testing equation "+eq+" for"+type.name());
		t.setFacility(facility);
		
		t.addSeed(halfX, halfZ-3, type);
		t.addSeed(halfX, halfZ-2, type);
		t.addSeed(halfX, halfZ-1, type);
		t.addSeed(halfX, halfZ,   type);
		t.addSeed(halfX, halfZ+1, type);
		t.addSeed(halfX, halfZ+2, type);
		t.addSeed(halfX, halfZ+3, type);
		
		return t;
	}

	public static TestConfig getScatteredTest(int length, int eq, CORAL_TYPE type) {
		TestConfig t = new TestConfig("SCA",length, eq, "Scattered test for "+type.name()); //do we need notes?
		t.setFacility(facility);
		
		int spacer = 10;
		//creates a grid of coral
		for(int stepX=1; stepX < 5; ++stepX) {
			for(int stepZ=1; stepZ < 5; ++stepZ) {
				t.addSeed(stepX*spacer, stepZ*spacer, type);
			}
		}
		
		return t;
	}
	
	public static TestConfig getScatteredMCTest(int length, int eq) {
		CORAL_TYPE[] types = {CORAL_TYPE.RED, CORAL_TYPE.BLUE, CORAL_TYPE.GREEN};
		TestConfig t = new TestConfig("CSCA",length, eq, "Scatter color test"); //do we need notes?
		t.setFacility(facility);
		int kind = 0, ln = types.length;

		int spacer = 10;
		//creates a grid of coral
		for(int stepX=1; stepX < 5; ++stepX) {
			for(int stepZ=1; stepZ < 5; ++stepZ) {
				t.addSeed(stepX*spacer, stepZ*spacer, types[++kind % ln]);
			}
		}
		
		return t;
	}
	
	public static TestConfig get4GroupTest(int length, int eq, CORAL_TYPE type ) {
		TestConfig t = new TestConfig("4GT",length, eq, "4 group test for "+type+" using threshold of "+(eq+3) );
		t.setFacility(facility);
		
		Point3D dims = CoralCommandBlock.getDims(); 
		int halfX = dims.x/2;
		int halfZ = dims.z/2;
		t.addSeed(halfX,   halfZ,   type);
		t.addSeed(halfX,   halfZ+1, type);
		t.addSeed(halfX+1, halfZ,   type);
		t.addSeed(halfX+1, halfZ+1, type);
		
		return t;
	}
	
	public static TestConfig getOneDirTest(int length, int eq, CORAL_TYPE type) {
		TestConfig t = new TestConfig("1DIR",length, eq, "Single direction spread test for "+type.name()+" using threshold of "+(eq+3));
		t.setFacility(facility);
		t.addSeed(1, 1, type);
		return t;
	}
	
	public static TestConfig getDiagonalTest(int length, int eq, CORAL_TYPE type) {
		int x = 1, z = 1;
		TestConfig t = new TestConfig("DIAG",length, eq, "Diagonal test for "+type.name());
		t.setFacility(facility);
		
		t.addSeed(x++, z++, type);
		t.addSeed(x++, z++, type);
		t.addSeed(x++, z++, type);
		t.addSeed(x++, z++, type);
		t.addSeed(x++, z++, type);
		t.addSeed(x++, z++, type);
		t.addSeed(x++, z++, type);
		t.addSeed(x++, z++, type);
		t.addSeed(x++, z++, type);
		
		return t;
	}
	
	public static TestConfig getFullTest(int length, int eq, CORAL_TYPE type) {
		TestConfig t = new TestConfig("FUL",length, eq, "Full test for "+type.name());
		t.setFacility(facility);
		
		Point3D dims = CoralCommandBlock.getDims();
		for(int x = dims.x; x > 0; --x) {			
			for(int z = dims.z; z > 0; --z) {
				t.addSeed(x, z, type);
			}
		}
		
		return t;
	}
}

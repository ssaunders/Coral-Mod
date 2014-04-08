package coral;

import java.awt.Dimension;

import net.minecraft.util.Facing;
import coral.BlockCoral.CORAL_TYPE;

public class TestFactory {
	private static String facility;
	public static void setTestFacility(String fac) {
		facility = fac;
	}
	private static Point3D dims;
	public static void setDims(int x, int y, int z) {
		if(x > 0 && z > 0) {
			dims = new Point3D(x,y,z);
		} else {
			System.err.println("!!!! Attempted to set TestFactory dimensions to 0");
		}
	}
	public static void setDims(Point3D d) {
		if(d.x > 0 && d.z> 0) {
			dims = d;
		} else {
			System.err.println("!!!! Attempted to set TestFactory dimensions to 0");
		}
	}

	public static TestConfig getLineTest(int length, int eq, CORAL_TYPE type) {
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
		for(int stepX=1; stepX < dims.x%spacer; ++stepX) 
		{
			for(int stepZ=1; stepZ < dims.z%spacer; ++stepZ) 
			{
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
		
		int oneGreen=1;
		int twoRed = 1;

		int spacer = 10;
		int stepZ, stepX,a=0,z=0;
		//creates a grid of coral
		for(stepX=1; stepX < dims.x%spacer; ++stepX) {
			for(stepZ=1; stepZ < dims.z%spacer; ++stepZ) {
//				if(stepX > 1 && types[kind % ln] == CORAL_TYPE.RED && twoRed != 0) {
//					t.addSeed(a-1, z, CORAL_TYPE.RED);
//					t.addSeed(a+1, z, CORAL_TYPE.RED);
//					--twoRed;
//				}
//				a = stepX*spacer;
//				z = stepZ*spacer;
				t.addSeed(stepX*spacer, stepZ*spacer, types[++kind % ln]);
			}
//			if(types[kind % ln] == CORAL_TYPE.GREEN && oneGreen != 0) {
//				System.out.println(" a-1 "+(a-1)+" z "+z);
//				t.addSeed(a-1, z, CORAL_TYPE.GREEN);
//				--oneGreen;
//			}
		}
		
		return t;
	}
	
	public static TestConfig get4GroupTest(int length, int eq, CORAL_TYPE type ) {
		TestConfig t = new TestConfig("4GT",length, eq, "4 group test for "+type+" using threshold of "+(eq+3) );	//!POF
		t.setFacility(facility);
		
		int halfX = dims.x/2;
		int halfZ = dims.z/2;
		t.addSeed(halfX,   halfZ,   type);
		t.addSeed(halfX,   halfZ+1, type);
		t.addSeed(halfX+1, halfZ,   type);
		t.addSeed(halfX+1, halfZ+1, type);
		
		return t;
	}
	
	public static TestConfig getOneDirTest(int length, int eq, CORAL_TYPE type) {
		TestConfig t = new TestConfig("1DIR",length, eq, "Single direction spread test for "+type.name()+" using threshold of "+(eq+3)); //!POF
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
		
		for(int x = dims.x; x > 0; --x) {			
			for(int z = dims.z; z > 0; --z) {
				t.addSeed(x, z, type);
			}
		}
		
		return t;
	}
}

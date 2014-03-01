package coral;

import coral.BlockCoral.CORAL_TYPE;

public class TestFactory {

	public static TestConfig getLineTest(int length, int eq, CORAL_TYPE type) {
		Point3D dims = CoralCommandBlock.getDims();
		int halfX = dims.x/2;
		int halfZ = dims.z/2;
		TestConfig t = new TestConfig(4, eq, "Testing equation "+eq+" for GREEN");
		
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
		TestConfig t = new TestConfig(20, eq, "Scattered test for "+type.name()); //do we need notes?
		
		int spacer = 10;
		//creates a grid of coral
		for(int stepX=1; stepX < 5; ++stepX) {
			for(int stepZ=1; stepZ < 5; ++stepZ) {
				t.addSeed(stepX*spacer, stepZ*spacer, type);
			}
		}
		
		return t;
	}
	
	public static TestConfig get4GroupTest(int length, int eq, CORAL_TYPE type ) {
		TestConfig t = new TestConfig(length, eq, "4 group test for "+type+" using threshold of "+(eq+3) );
		
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
		TestConfig t = new TestConfig(50, eq, "Single direction spread test for "+type.name()+" using threshold of "+(eq+3));
		t.addSeed(1, 1, type);
		return t;
	}
	
	public static TestConfig getDiagonalTest(int length, int eq, CORAL_TYPE type) {
		int x = 1, z = 1;
		TestConfig t = new TestConfig(length, eq, "Diagonal test for "+type.name());
		
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
		TestConfig t = new TestConfig(length, eq, "Full test for "+type.name());
		
		Point3D dims = CoralCommandBlock.getDims();
		for(int x = dims.x; x > 0; --x) {			
			for(int z = dims.z; z > 0; --z) {
				t.addSeed(x, z, type);
			}
		}
		
		return t;
	}
}

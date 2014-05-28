package coral;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.minecraft.world.World;

import org.apache.commons.lang3.ArrayUtils;

import coral.BlockCoral.CORAL_TYPE;

public class TestConfig {
	public static int getNewUniqueId() {
		return ((int)(Math.random()*99999) % 9000) + 1000;
	}
	
	ArrayList<SeedConfig> seeds =  new ArrayList<SeedConfig>();
	
	private String typeList = "";
		public String getColors() {
			if("".equals(typeList))
				getNumCoralTypes();
			return typeList; 
		}

	int numTypes = -1;
		/*** Creates the single-letter list of types as well */
		private int getNumCoralTypes() {
			if(numTypes < 0) {
				StringBuilder b = new StringBuilder();
				numTypes = 0;
				boolean[] typeCounter =  new boolean[CORAL_TYPE.getNumberOfCoral()];
				int siz = seeds.size();
				//Figure out the types contained in all the seeds
				for(int i = 0; i < siz; ++i) {
					typeCounter[seeds.get(i).type.ordinal()] = true;
				}
				//Count the number of types, and add each type to the string
				for(int i = 0; i < typeCounter.length; ++i) {
					if(typeCounter[i] == true){
						numTypes++;
						b.append(CORAL_TYPE.getCoralName(i).substring(0, 1));
					}
				}
				typeList = b.toString();
			}
			return numTypes;
		}

	/**Equation used for this test */
	private int equationNum = -1;
		public int getGrowthEq() { return equationNum; }
		
	/**Length of test in MS*/
	private int duration; //in milliseconds
		/** Returns the length of the test in MS */
		public int getDuration() { return duration; }
	
	/** A string of custom text. Notes are put here */
	private String details;
	private String testFacility="none";
		public void setFacility(String facility) {
			testFacility = facility;
		}
	private int errorCount=0;
	private int uniqueId;
		/** Returns the four-digit random number used in the folder name */
		public int getUniqueId() { return uniqueId; }
	
	private long startTime;
		public long getStartTime() {
			return startTime;
		}
		private void setStartTime() {
			startTime = System.currentTimeMillis();
		}
	
	private StringBuilder errors = null;
		private void addError(String string) {
			if(errors != null) {
				errors.append("\n"+string);
			}
			else {
				errors = new StringBuilder(string);
			}
			errorCount++;
		}

	private StringBuilder csv;
		public void appendToCsv(int[] pop, int[] cumHealth) {
			StringBuffer line = new StringBuffer(""+getTimeElapsed());
			line.append(',');
			String pStr = ArrayUtils.toString(pop);
			line.append(pStr.substring(1, pStr.length()-1));
			line.append(", ,");
			String chStr = ArrayUtils.toString(cumHealth);
			line.append(chStr.substring(1, chStr.length()-1));
			line.append(",*\n");
			csv.append(line);
		}
    
    private String prefix = "";
    	public void addPrefix(String s) { prefix = s.concat("_"); }
    	public String getPrefix() { return prefix; }
    	
    private String testSignature;
	    public String getTestSignature() {
	    	return "("+testSignature+","+getColors()+","+getGrowthEq()+")";
	    }

	/** Returns if the elapsed time is longer than the duration*/
	public boolean hasTimeElapsed() {
		return (System.currentTimeMillis() - getStartTime()) >= duration;
	}
	
	/** Returns the time since the test started to two decimals.*/
	public double getTimeElapsed() {
		return (Math.round( (System.currentTimeMillis() - getStartTime())/1000./60.*100.)/100.);
	}
	
	/** Returns the remaining time for the test to two decimals.*/
	public double getTimeRemaining() {
		return (Math.round( (duration  - (System.currentTimeMillis() - getStartTime()))/1000./60.*100.))/100.;
	}
	
	/* CONSTRUCTOR */
	/** Length is in minutes. Notes will be added to a separate text file. **/
	public TestConfig(String key, int length, int eq, String notes) {
		testSignature = key;
		equationNum = (eq > 0 ? eq : 0);
		duration = length < 5 ? 1 * 60 * 1000 : length * 60 * 1000;
		details = notes;
		uniqueId = getNewUniqueId();
	}
	public TestConfig(String key, int length, int eq, String notes, String pfx) {
		testSignature = key;
		equationNum = (eq > 0 ? eq : 0);
		duration = length < 5 ? 1 * 60 * 1000 : length * 60 * 1000;
		details = notes;
		prefix = pfx;
		uniqueId = getNewUniqueId();
	}
	
	/** Adds a coral 'seed' to the config. A new coral of that 
	 *  type will be created at the first available y value at 
	 *  the specified x,z coordinates. **/
	public void addSeed(int x, int z, CORAL_TYPE t) {
		SeedConfig q = new SeedConfig(x,z,t);
		
		//? Validation?
		
		seeds.add(q);
	}
	
	
	/**Seeds world inside the test boundaries using the seed configs given to it. */
	public boolean beginTest(World world, int x, int y, int z) {
		boolean success = true;
		int size = seeds.size();
		SeedConfig coralSeed;
		int relX, relZ, seedY, numCoralPlaced=0;
		StringBuilder goodKeys = new StringBuilder();
		Point3D dimensions = BlockControlBlock.getDims();
		Point3D direction = BlockControlBlock.getDirection(world, x,y,z);
		
		setStartTime();
		csv = new StringBuilder(new SimpleDateFormat("hh:mm").format(new Date(getStartTime()))+
				",Population,,,,,,,,,,Cumulative Health\nTime,"+CORAL_TYPE.toCsv()+", ,"+CORAL_TYPE.toCsv()+"\n");
		for(int i = 0; i < size; ++i) {
			coralSeed = seeds.get(i);
			if(coralSeed.x <= dimensions.x && coralSeed.z <= dimensions.z
				&& coralSeed.x > 0 && coralSeed.z > 0){ 
				relX = coralSeed.x + x;
				relZ = coralSeed.z + z;
				seedY= world.getTopSolidOrLiquidBlock(relX, relZ);
				if(Coral.coralBlock.addCoral(world, new Point3D(relX,seedY,relZ), coralSeed.blockId)){						
					++numCoralPlaced;
					goodKeys.append(i+" ");
				}
			} else {
				String error = "^ Seed #"+i+" ("+coralSeed.x+","+coralSeed.z+") is out of range ("+dimensions.x+","+dimensions.z+")";
				System.out.println(error);
				addError(error);
			}
		}
		if(numCoralPlaced == 0) {
			String error = "^ No coral placed. Skipping test. Test "+BlockControlBlock.getCurrentTestNumber();
			System.out.println(error);
			addError(error);
			success = false;
		} else if(numCoralPlaced < size) {
			String error = "^ Fewer coral placed than planned "+numCoralPlaced+" of "+size+". "+goodKeys;
			System.out.println(error);
			addError(error);
			success = true;	//with errors, but still true
		}
		return success;
	}

	public void endTest() {
		if(getStartTime() != 0) {
			String path = BlockControlBlock.getCurrentPath(true);
			BlockControlBlock.writeToFile(path, "_Description_"+uniqueId, "", this.toString());
			BlockControlBlock.writeToFile(path, "_Stats_"+uniqueId, "", this.csv.toString(), "csv");
			if(errorCount > 0) {
				BlockControlBlock.writeToFile(path, "_Errors_"+uniqueId, "Errors:\n", errors.toString());
			}
			if(getTimeRemaining() > 1) { //if time remaining > 1 min
				String data = getTimeElapsed()+"min of "+BlockControlBlock.timeToMin(duration)+"min; "
						+getTimeRemaining()+"min remaining";
				BlockControlBlock.writeToFile(path, "_Aborted", "", data);
			}
		}
	}
	
	public void abort() {
		String path = BlockControlBlock.getCurrentPath(true);
		BlockControlBlock.writeToFile(path, "_Aborted", "", "");
		if(errorCount > 0) {
			BlockControlBlock.writeToFile(path, "_Errors", "Errors:\n", errors.toString());
		}
	}

	public String toString() {
		return  getTestSignature()
			    +"\nFile names are yyyy-mm-dd_hh,mm_(survey number)_(elapsed time)"
			    +"\nDuration:\t\t"+getTimeElapsed()+"min"
			    +"\nFacility:\t\t"+testFacility+"\n"
			    +details
	    		+"\nSeeds ("+seeds.size()+"):\n"
			    +seeds.toString();
	}

	// CLASS SEEDCONFIG
	private class SeedConfig {
		public int x;
		public int z;
		public CORAL_TYPE type;
		public int blockId;
		
		public SeedConfig(int xCoor, int zCoor, CORAL_TYPE t) {
			x = xCoor;
			z = zCoor;
			type = t;
			blockId = CORAL_TYPE.getBlockId(type);
		}
		
		public String toString() {
			return type.name()+" ("+blockId+") "+String.format("(%3d, y, %3d)", x, z);
		}
	}

}
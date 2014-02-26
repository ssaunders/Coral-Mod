package coral;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.Sys;

import com.sun.xml.internal.ws.util.StringUtils;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import coral.BlockCoral.CORAL_TYPE;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CoralCommandBlock extends BlockContainer {
	private static boolean printMsgs = true;
	public static boolean showMessages() { return printMsgs; }
	/* GENERAL TEST INFORMATION */
	private final Point3D TEST_DIMS = new Point3D(19,0,9);
	/**Dimensions is the actual w/h/l of the area it to survey.
	 * It is NOT a point in 3D.
	 */
	private Point3D dimensions = null;
		public Point3D getDims() {return dimensions;}
		private void setDims(Point3D newDims, World world, int x, int y, int z) {
			if(newDims.x > 2 && newDims.z > 2) {				
				if(newDims.y < 2) {
					int lowestPoint = 10000, top = 0;
					for(int xIncr = 1; xIncr < newDims.x+1; ++xIncr) {
						for(int zIncr = 1; zIncr < newDims.z+1; ++zIncr) {
							top = world.getTopSolidOrLiquidBlock(x+xIncr, z+zIncr);
							if(lowestPoint > top) {
								lowestPoint = top;
							}
						}
					}
					dimensions = new Point3D(newDims.x, y-lowestPoint-1, newDims.z);
					if(lowestPoint < 2) { System.out.println("!!!! Lowest point is <2. Test will be ineffective."); }
				} else {
					dimensions = newDims;
				}
				relativeDimensions = new Point3D(dimensions.x+x,dimensions.y+y, dimensions.z+z); 
			} else {
				setDims(new Point3D(20,0,20), world, x, y, z);
			}
		}
	/** The dimensions added to the location of the command block. 
	 * This is the point on the opposite corner of the cube-shaped testing area.*/
		public Point3D getRelativeDimensions() {return relativeDimensions;}
	private Point3D relativeDimensions = new Point3D(0,0,0);
	
	private boolean active = false;	//this needs to save the state of the block
		public boolean isStopped() { return active; }
		
	/** The time of the very first run in MS. */
	private long firstRun = 0;
	/** Gets the time elapsed since the beginning of the tests */
		private double getTotalTimeElapsed(){
			return timeToMin(System.currentTimeMillis() - firstRun); 
		}
	/** The last time a survey was made. 
	 * Used to prevent multiple surveys in a short time **/
    private long lastSurvey = 0;
    
    private static String mainFolderPath = System.getProperty("user.home")+"\\Desktop\\Coral_Tests";
    private static String currTestFolderName = "";
    /** appendable == true to add "\\" on the end, unless currTestFolderName is "" */
    public  static String getCurrentPath(boolean appendable) {
    	return mainFolderPath +"\\"+ currTestFolderName + ("".equals(currTestFolderName) && appendable ? "" : "\\");
    }
    private static SimpleDateFormat folderFormat = new SimpleDateFormat ("yyyy-MM-dd_hh,mm'_rn'");
    private static SimpleDateFormat fileFormat = new SimpleDateFormat ("yyyy-MM-dd_hh,mm'_'");
    
    private static StringBuilder prevSurvey = new StringBuilder();
    private int[] population = new int[CORAL_TYPE.getNumberOfCoral()];
    private int[] cumHealth = new int[CORAL_TYPE.getNumberOfCoral()];
	
	/* CONSTRUCTOR */
	public CoralCommandBlock(int id) {
		super(id, Material.ground);
		setHardness(0.5F);
		
	    setUnlocalizedName("cmdCoralBlock");
	    setCreativeTab(CreativeTabs.tabBlock);
	    func_111022_d(ModInfo.NAME+":cmdCoralBlock");
	    
		setTickRandomly(false);
		
		//TODO
		//survey creates file, writes to file, bzips
		//Write script to interrogate file structure for file sizes 
		
		/*  TEST SETUP  */
		//TEST 1
		int x = 1, z = 1;
		TestConfig one = new TestConfig(0, "Just a test");
		one.addSeed(x++, z++, CORAL_TYPE.BLUE);
		one.addSeed(x++, z++, CORAL_TYPE.BLUE);
		one.addSeed(x++, z++, CORAL_TYPE.BLUE);
		one.addSeed(x++, z++, CORAL_TYPE.BLUE);
		one.addSeed(x++, z++, CORAL_TYPE.BLUE);
		one.addSeed(x++, z++, CORAL_TYPE.BLUE);
		one.addSeed(x++, z++, CORAL_TYPE.BLUE);
		one.addSeed(x++, z++, CORAL_TYPE.BLUE);
		one.addSeed(x++, z++, CORAL_TYPE.BLUE);
		tests.add(one);
		
		//TEST 2
		//....
	}

	/* MINECRAFT FUNCTIONS */
	/* All have to do with block behavior */
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, 
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (dimensions == null) {
			setDims(TEST_DIMS, world, x, y, z);
		}
        if (!world.isRemote)
        {
			if(!active) { //start condition
				active = true;
				player.addChatMessage("Starting execution of tests");
				if(startNewTest(0, world, x, y, z)){
					firstRun = getCurrentTest().startTime;
				} else {
					player.addChatMessage("Unable to execute tests. Stopping.");
					active = false;
				}
			} else if(world.getBlockId(x, y+1, z) == torchWood.blockID) { //check for torch
				active = false;
				player.addChatMessage("Stopping execution of tests. Ran "+(getRunNumber()-1)+" tests in "+getTotalTimeElapsed()+"min.");
				if(printMsgs) System.out.println("===X Stopping execution of tests. Ran "+(getRunNumber()-1)+" tests in "+getTotalTimeElapsed()+"min.");
				TestConfig tcfg = getCurrentTest();
				if(tcfg != null) {
					tcfg.endTest();
				}
				resetEnvironment(world, x, y, z);
			} else {
				player.addChatMessage(
					"Running test "+getTestNumber()+". Time remaining: "+getCurrentTest().getTimeRemaining()
					+".\n Executed "+(getRunNumber()-1)+" tests. Elapsed time: "+getTotalTimeElapsed()
					+"\nTo stop all tests, place a torch on top of the block.");
			}
        }
//        double doubleX = dimensions.x + x;
//        double doubleY = dimensions.y + y;
//        double doubleZ = dimensions.z + z;
//
//        GL11.glPushMatrix();
//        GL11.glTranslated(-doubleX, -doubleY, -doubleZ);
//        GL11.glColor3ub((byte)255,(byte)0,(byte)0);
//        float mx = 9;
//        float my = 9;
//        float mz = 9;
//        GL11.glBegin(GL11.GL_LINES);
//        GL11.glVertex3f(mx+0.4f,my,mz+0.4f);
//        GL11.glVertex3f(mx-0.4f,my,mz-0.4f);
//        GL11.glVertex3f(mx+0.4f,my,mz-0.4f);
//        GL11.glVertex3f(mx-0.4f,my,mz+0.4f);
//        GL11.glEnd();
//        GL11.glPopMatrix();
        
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(active) {	
	        double d0 = 0.0625D;
	        for (int l = 0; l < 6; ++l)
	        {
	            double d1 = (double)((float)x + rand.nextFloat());
	            double d2 = (double)((float)y + rand.nextFloat());
	            double d3 = (double)((float)z + rand.nextFloat());
	            
	            if (l == 0 && !world.isBlockOpaqueCube(x, y + 1, z)) {
	                d2 = (double)(y + 1) + d0;
	            } else if (l == 1 && !world.isBlockOpaqueCube(x, y - 1, z)) {
	                d2 = (double)(y + 0) - d0;
	            } else if (l == 2 && !world.isBlockOpaqueCube(x, y, z + 1)) {
	                d3 = (double)(z + 1) + d0;
	            } else if (l == 3 && !world.isBlockOpaqueCube(x, y, z - 1)) {
	                d3 = (double)(z + 0) - d0;
	            } else if (l == 4 && !world.isBlockOpaqueCube(x + 1, y, z)) {
	                d1 = (double)(x + 1) + d0;
	            } else if (l == 5 && !world.isBlockOpaqueCube(x - 1, y, z)) {
	                d1 = (double)(x + 0) - d0;
	            }
	            
	            if (d1 < (double)x || d1 > (double)(x + 1) || d2 < 0.0D || d2 > (double)(y + 1) || d3 < (double)z || d3 > (double)(z + 1))
	            {
	                world.spawnParticle("reddust", d1, d2, d3, 0.0D, 0.0D, 0.0D);
	            }
	        }
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
        if (!world.isRemote)
        {
            if (world.isBlockIndirectlyGettingPowered(x, y, z))
            {
            	if(active) {
            		long currTime = System.currentTimeMillis(); 
            		if(currTime - lastSurvey > 12000) {
            			lastSurvey = currTime;
            			survey(world, x, y, z);
            			
            			if(getCurrentTest().hasTimeElapsed()) {
            				endTest(world, x, y, z);
            				if(!startNewTest(-1, world, x, y, z)) {
            					//if no tests start...
            					active = false;
            				}
            			}
        			}
            		
        		} //active
            } //powered
        } //remote
	}
	
//	/**If any experiments are running, you must put a torch on to break the block*/
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if(active){
			if(world.getBlockId(x, y+1, z) == torchWood.blockID) {
				resetEnvironment(world, x, y, z);
				getCurrentTest().endTest();
				return super.removeBlockByPlayer(world, player, x, y, z);
			} else {				
				player.addChatMessage(
					"Executed "+getRunNumber()+" tests. Run time was "+getTotalTimeElapsed()+" min."
					+"\nTo stop all tests, place a torch on top of the block.");
				
				return false;
			}
		} else {
			resetEnvironment(world, x, y, z);
			TestConfig tcfg = getCurrentTest();
			if(tcfg != null) tcfg.endTest();
			return super.removeBlockByPlayer(world, player, x, y, z);
		}
	}
	
	private void resetEnvironment(World world, int x, int y, int z) {
		if (dimensions == null) {
			setDims(TEST_DIMS, world, x, y, z);
		}
		killAll(world, x, y, z);
		active = false;
		firstRun = 0;
		lastSurvey = 0;
		prevSurvey = new StringBuilder();
		testNumber = 0;
		surveyNum = 0;
		runNumber = 0;
	}
	
	//////// TESTING AREA ////////
	
	// VARIABLES, Getters, and setters
	/** The actual tests that to be run **/
	private ArrayList<TestConfig> tests = new ArrayList<TestConfig>(); 
    private TestConfig getCurrentTest() {
    	if(testNumber < tests.size()) {
    		return tests.get(testNumber);
    	} else {
    		return null;
    	}
    }
    
	/** The test at position # **/
	private int testNumber=0;
	/** Gets the number of test that is being run. **Different than than the run number** **/
	public int getTestNumber() {
		return testNumber;
	}
	
	/** How many runs were previous to this one **/
	private int runNumber=0;
	/** Gets how many runs were previous to this one. **Different than than the test number** **/
	public int getRunNumber() {
		return runNumber;
	}
	
	private int surveyNum = 0;
	public int getSurveyNum() {
		return surveyNum;
	}
	
	/** Returns true if a new test was started successfully. False if 
	 *  there are no more tests to run, or if the given number is out 
	 *  of range. User can specify which test to run, or -1 for the 
	 *  next one. Kills all coral in testing area.    **/
	public boolean startNewTest(int testNum, World world, int x, int y, int z) {
		boolean success = false;
		TestConfig tcfg;
		if(testNum < 0) {	//get the current test
			++testNumber;
			tcfg = getCurrentTest();
		} else {
			testNumber = testNum;
			tcfg = getCurrentTest();
		}
		
		killAll(world, x, y, z);
		runNumber++;
		surveyNum = 0;
		do {	//start the next test. This may involve skipping a test.
			if(tcfg == null) 
			{
				System.out.println("!!!! Test number "+testNumber+" is out of range "+tests.size());
				success = false;
			}
			else
			{
				if(printMsgs) System.out.println("===> Beggining test "+testNumber+" at "+(new Date()) );
				setupTestFolder();
				success = tcfg.beginTest(world, x, y, z);
				
				if(!success) 
				{
					if(printMsgs) System.out.println("===X Failed to start test. Aborting.");
					tcfg.abort();
					testNumber++;
					tcfg = getCurrentTest();
				}
			}
		} while(!success && tcfg != null);
		
		return success;
	}
	
	/** Writes description file. **/
	public void endTest(World world, int x, int y, int z) {
		if( testNumber >= 0) {
			getCurrentTest().endTest();
		}
		if(printMsgs) System.out.println("==== Ending test "+testNumber);
	}
	
	private void setupTestFolder() {
		if(printMsgs) System.out.println("~~~~ Made folders");
		String folderName = mainFolderPath+"\\"+getNewTestFolderName();
		
		new File(folderName).mkdirs();
		new File(folderName+"\\Concatenated Tests").mkdirs();
	}
	
	private String getNewTestFolderName() {
		currTestFolderName = folderFormat.format(new Date())+"_"+getRandFNumber();
		return currTestFolderName;
	}
	
	private int getRandFNumber() {
		return ((int)(Math.random()*99999) % 9000) + 1000;
	}
	
	/** Run through the current test and record &blah& **/
	private void survey(World world, int x, int y, int z) {
		if(printMsgs) System.out.println("~~~~ surveyed!");
		StringBuilder currTest = new StringBuilder();
		int tempHealth, idx;
		
		for(int xPos = x+1; xPos < x + dimensions.x; ++xPos) {			
			for(int zPos = z+1; zPos < z + dimensions.z; ++zPos) {
				//3-scan
				for(int yPos = y-1; yPos > y - dimensions.y; --yPos) {
					int bId = world.getBlockId(xPos, yPos, zPos);
					if(Coral.isCoral(bId)){
						tempHealth = Coral.coralBlock.getHealth(xPos, yPos, zPos);
						if(tempHealth > 0) {
							idx = CORAL_TYPE.toIndex(bId);
							population[idx]++;
							cumHealth[idx] += tempHealth;
						}
					}
					currTest.append(String.format("%03d", bId));
				}
				
				//top scan
//				yPos= world.getTopSolidOrLiquidBlock(xIncr+x, zIncr+z);
//				currTest.append(String.format("%03d", world.getBlockId(xIncr+x, yPos, zIncr+z)));
//				currTest.append(String.format("%03d", world.getBlockId(xIncr+x, yPos-1, zIncr+z)));
				
//					q = q+world.getLightBrightness(dimensions.x, dimensions.y, dimensions.z);
//					world.getBlockMetadata(par1, par2, par3); //if I decide to do health through metadata
			}
		}
		
		getCurrentTest().appendToCsv(population, cumHealth);
		for(int i = 0; i < population.length; ++i) {
			population[i]= 0;
			cumHealth[i] = 0;
		}
		
		++surveyNum; 
		
		writeToFile(getCurrentPath(true), getSurveyFileName(getSurveyNum()), "", currTest.toString());
		//TODO figure out if I can bzip from java
//		runBzip("");
		
		if(!"".equals(prevSurvey)) {			
			writeToFile(getCurrentPath(true)+"Concatenated Tests\\", getSurveyFileName(getSurveyNum()), "", prevSurvey.append(currTest).toString());
			//TODO figure out if I can bzip from java
			
			prevSurvey = currTest;
		}
	}
	//// END TEST AREA ////
	

	
	/** Kills all the coral inside the test area's bounds **/
	private void killAll(World world, int x, int y, int z) {
		if(printMsgs) System.out.println("XXXX killAll");
		int yPos;
		
		if(dimensions == null) {
			System.out.println("!!!! Dimensions is null. Could not kill anything. ");
			return;
		}
		for(int xIncr = 1; xIncr < dimensions.x+1; ++xIncr) {			
			for(int zIncr = 1; zIncr < dimensions.z+1; ++zIncr) {
				yPos= world.getTopSolidOrLiquidBlock(xIncr+x, zIncr+z);
				if(Coral.isCoral(world.getBlockId(xIncr+x, yPos, zIncr+z))){
					System.out.println("killed one coral "+new Point3D(x+xIncr, yPos, z+zIncr));	//!D
					Coral.coralBlock.removeCoral(world, x+xIncr, yPos, z+zIncr);
				}
			}
		}
	}
	

	/** Returns the formatted filename:
	 *  date_time_survey#_.txt OR 
	 *  ... **/
	public String getSurveyFileName(int uniqueId) {
		StringBuilder name = new StringBuilder();
		
		name.append( fileFormat.format( new Date(System.currentTimeMillis()) ) );
		name.append(getRunNumber()+"_");
		name.append(uniqueId);
//		System.out.println("survey name "+name);
		return name.toString()+".txt";
	}
	
	//? time between surveys, testing facility, spread (is it even interesting?)
	
	/* What questions are you trying to answer?
	 ** Notes ON THE FACILITY **
	 *  Experimental/Indep variables
	 *    Complexity of environment (slope, hills, soils, height, etc)
	 *    Initial composition of species
	 *    Parameters controlling species
	 *   
	 ** Characterization of corals. ** 
	 *  Within a simple environment, how do the parameters affect complexity?
	 *  Multiple tests with a single coral, changing parameters in small way, 
	 *  to measure their effect on growth. (measuring growth rate in different environments)
	 *  
	 *  ^ initial experiments to a) decide corals (make graphs) b) test it works
	 *  
	 *  next time have experiment results (50^3) x 3 facilities
	 */
	
	/** Writes the given String to a txt file using the given name. **/
	private void writeToFile(String filePath, String fileName, String header, String data) {
		writeToFile(filePath, fileName, header, data, "txt");
	}
	/** Writes the given String to a file using the given name and extension. **/
	private void writeToFile(String filePath, String fileName, String header, String data, String ext) {
		File newFile = new File(filePath+fileName+"."+ext);
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath+fileName+"."+ext), "utf-8"));
			newFile.createNewFile();
			writer.write(header+"\n"+data);
			writer.close();
		} catch (IOException e) {
			System.out.println("Could not create file "+fileName+"."+ext);
			e.printStackTrace();
		}
		
		if(printMsgs) System.out.println("<<<< written to file! "+filePath+" \\ "+fileName+"."+ext+" "+header);
	}
	
	private void runBzip(String fileName) {
//		Runtime.getRuntime().exec("c:\\program files\\test\\test.exe", null, new File("c:\\program files\\test\\"));
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}
	
	public static double timeToMin(long time){
		return (Math.round(time/1000./60.*100.)/100.);
	}
	
	/*** CLASS: TESTCONFIG ***/
	/***/
	private class TestConfig {
		ArrayList<SeedConfig> seeds =  new ArrayList<SeedConfig>();
		private int getCoralTypes() {
			if(numTypes < 0) {
				numTypes = 0;
				boolean[] typeCounter =  new boolean[CORAL_TYPE.getNumberOfCoral()];
				int siz = seeds.size();
				for(int i = 0; i < siz; ++i) {
					typeCounter[seeds.get(i).type.ordinal()] = true;
				}
				for(int i = 0; i < typeCounter.length; ++i) {
					if(typeCounter[i] == true)
						numTypes++;
				}
			}
			return numTypes;
		}

		int numTypes = -1;
		int duration; //in milliseconds
		String details;
		long startTime;
		public int errorCount=0;
		StringBuilder errors = null;
	    private StringBuilder csv;

		/** Returns if the elapsed time is longer than the duration*/
		public boolean hasTimeElapsed() {
			return (System.currentTimeMillis() - startTime) >= duration;
		}
		
		/** Returns the time since the test started to two decimals.*/
		public double getTimeElapsed() {
			return (Math.round( (System.currentTimeMillis() - startTime)/1000./60.*100.)/100.);
		}
		
		/** Returns the remaining time for the test to two decimals.*/
		public double getTimeRemaining() {
			return (Math.round( (duration  - (System.currentTimeMillis() - startTime))/1000./60.*100.))/100.;
		}
		
		/* CONSTRUCTOR */
		/** Length is in minutes. Notes will be added to a separate text file. **/
		public TestConfig(int length, String notes) {
			duration = length < 5 ? 5 * 60 * 1000 : length * 60 * 1000;
			details = notes;
		}
		
		/** Adds a coral 'seed' to the config. A new coral of that 
		 *  type will be created at the first available y value at 
		 *  the specified x,z coordinates. **/
		public void addSeed(int x, int z, CORAL_TYPE t) {
			SeedConfig q = new SeedConfig(x,z,t);
			
			//TODO add validation
			
			seeds.add(q);
		}
		
		/**Seeds world inside the test boundaries using the seed configs given to it. */
		private boolean beginTest(World world, int x, int y, int z) {
			boolean success = true;
			int size = seeds.size();
			SeedConfig coralSeed;
			int relX, relZ, seedY, numCoralPlaced=0;
			StringBuilder goodKeys = new StringBuilder();
			
			startTime = System.currentTimeMillis();
			csv = new StringBuilder(new SimpleDateFormat("hh:mm").format(new Date(startTime))+
					",First is population; second is cumHealth\nTime,"+CORAL_TYPE.toCsv()+", ,"+CORAL_TYPE.toCsv()+"\n");
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
					getCurrentTest().addError(error);
				}
			}
			if(numCoralPlaced == 0) {
				getCurrentTest().addError("^ No coral placed. Skipping test. Test "+testNumber);
				success = false;
			}
			if(numCoralPlaced < size) {
				getCurrentTest().addError("^ Fewer coral placed than planned "+numCoralPlaced+" of "+size+". "+goodKeys);
				success = false;
			}
			return success;
		}
		
		public void endTest() {
			if(startTime != 0) {				
				writeToFile(getCurrentPath(true), "_Description", "", this.toString());
				writeToFile(getCurrentPath(true), "_Stats", "", this.csv.toString(), "csv");
				if(errorCount > 0) {
					writeToFile(getCurrentPath(true), "_Errors", "Errors:\n", errors.toString());
				}
				if(getTimeRemaining() > 1) { //if time remaining > 1 min
					String data = getTimeElapsed()+"min of "+timeToMin(duration)+"min; "
							+getTimeRemaining()+"min remaining";
					writeToFile(getCurrentPath(true), "_UNFINISHED", "", data);
				}
			}
		}
		
		public void abort() {
			writeToFile(getCurrentPath(true), "_Aborted", "", "");
		}

		private void addError(String string) {
			if(errors != null) {
				errors.append("\n"+string);
			}
			else {
				errors = new StringBuilder(string);
			}
			errorCount++;
		}
		
		private void appendToCsv(int[] pop, int[] cumHealth) {
			StringBuffer line = new StringBuffer(
					new SimpleDateFormat("hh:mm").format( new Date(System.currentTimeMillis()) ) );
			line.append(',');
			line.append(ArrayUtils.toString(pop).substring(1, pop.length*2));	 //TODO fix this so that it doesn't chop off the end
			line.append(", ,");
			line.append(ArrayUtils.toString(cumHealth).substring(1, cumHealth.length*2));
			line.append(",*\n");
			csv.append(line);
		}

		public String toString() {
			return details
					+"\nFile names are yyyy-mm-dd_hh,mm_(run number)_(survey number)"
				    +"\nTypes of Coral:\t"+getCoralTypes()
				    +"\nDuration:\t\t"+getTimeElapsed()+" min \n"
		    		+"\nSeeds ("+seeds.size()+"):\n"
				    +seeds.toString();
		}

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

}

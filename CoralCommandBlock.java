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

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.commons.lang3.ArrayUtils;

import coral.BlockCoral.CORAL_TYPE;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CoralCommandBlock extends BlockContainer {
	private static boolean printMsgs = true;
	public static boolean showMessages() { return printMsgs; }
	
	/* GENERAL TEST INFORMATION */
//	private final Point3D TEST_DIMS = new Point3D(50,0,50);
	private final Point3D TEST_DIMS = new Point3D(82,0,82);
	
	/**Dimensions is the actual w/h/l of the area it to survey.
	 * It is NOT a point in 3D.
	 */
	private static Point3D dimensions = null;
		public static Point3D getDims() {return dimensions;}
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
					dimensions = new Point3D(newDims.x, y-lowestPoint, newDims.z);
					if(y-lowestPoint < 2) { System.out.println("!!!! Test height is <2. Tests will be ineffective."); }
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
	
	private static boolean active = false;	//this needs to save the state of the block
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
    
    private String testPrefix = "";
    
    
	
	/* CONSTRUCTOR */
	public CoralCommandBlock(int id) {
		super(id, Material.ground);
		setHardness(0.5F);
		
	    setUnlocalizedName("cmdCoralBlock");
	    setCreativeTab(CreativeTabs.tabBlock);
	    func_111022_d(ModInfo.NAME+":cmdCoralBlock");
//	    setTextureName(ModInfo.NAME+":cmdCoralBlock");
	    
		setTickRandomly(false);
		
		//TODO
		//Write script to interrogate file structure for file sizes 

		setupTests();
		//*/
		
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
					firstRun = getCurrentTest().getStartTime();
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
					"Running test "+getCurrentTestNumber()+". Time remaining: "+getCurrentTest().getTimeRemaining()
					+".\n Executed "+(getRunNumber()-1)+" tests. Elapsed time: "+getTotalTimeElapsed()
					+"\nTo stop all tests, place a torch on top of the block.");
			}
        }
        
        //DRAW A BOX SHOWING THE DIMS. MAKE IT HIDEABLE USING TORCHES
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
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) { 
	/*
	 	if(active) {
	 		chat to player "Cannot place block. Tests in progress".
	 		return false;
		} else if ( blockCoor != null ) {
			if ( there is a torch at xyz) {
				world.
				resetEnvironment(world, x, y, z);
				place block; //? do I need to do this?
				return super.canPlaceBlockAt(world, x, y, z);
			} else {
				chat to player "A coral command block already exists. To move the block, place on top of a torch."
			}
		}
	
	*/
		return super.canPlaceBlockAt(world, x, y, z);
	}
	
//	/**If any experiments are running, you must put a torch on to break the block*/
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if(active){
			if(world.getBlockId(x, y+1, z) == torchWood.blockID) {
				resetEnvironment(world, x, y, z);
				getCurrentTest().endTest();
				//clear current block coor
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
			//clear current block coor
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
	private static ArrayList<TestConfig> tests = new ArrayList<TestConfig>(); 
    public static TestConfig getCurrentTest() {
    	if(testNumber < getTotalNumTests() && active) {
    		return tests.get(testNumber);
    	} else {
    		return null;
    	}
    }
    public static int getTotalNumTests() {
    	return tests.size();
    }
    
	/** The test at position # **/
	private static int testNumber=0;
	/** Gets the number of test that is being run. **Different than than the run number** **/
	public static int getCurrentTestNumber() {
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
				System.out.println("!!!! Test number "+(testNumber+1)+" is out of range "+getTotalNumTests());
				success = false;
			}
			else
			{
				if(printMsgs) System.out.println("===> Beggining test "+testNumber+" at "+new Date()+". "+(testNumber+1)+" of "+getTotalNumTests() );
				System.out.println("Estimated finish time for all tests: "+getFinishTime());
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
		
//		killAll(world, x, y, z);
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
		String folderName = mainFolderPath+"\\"+getNewTestFolderName();
		
		new File(folderName).mkdirs();
		new File(folderName+"\\Concatenated Tests").mkdirs();
		if(printMsgs) System.out.println("~~~~ Made folders");
	}
	
	private String getNewTestFolderName() {
		TestConfig t = getCurrentTest();
		currTestFolderName = testPrefix+folderFormat.format(new Date())
							   +"_cl"+t.getColors()+"_eq"+t.getGrowthEq()+"_"+getRandFNumber();
		return currTestFolderName;
	}
	
	public static int getRandFNumber() {
		return ((int)(Math.random()*99999) % 9000) + 1000;
	}
	
	/** Run through the current test and record the block ids. Also records health and population in arrays. **/
	private void survey(World world, int x, int y, int z) {
		if(printMsgs) System.out.println("~~~~ surveyed! "+lastSurvey);
		StringBuilder currTest = new StringBuilder();
		int tempHealth, idx;
//		int high=-1, low=101, medn=-1, mode=-1;
		
		for(int xPos = x+1; xPos < x + dimensions.x; ++xPos) {			
			for(int zPos = z+1; zPos < z + dimensions.z; ++zPos) {
				//3-scan
				for(int yPos = y-1; yPos >= y - dimensions.y; --yPos) {	//TODO test if >= works ok
					int bId = world.getBlockId(xPos, yPos, zPos);
					if(Coral.isCoral(bId)){
						tempHealth = Coral.coralBlock.getHealth(xPos, yPos, zPos);
						if(tempHealth > 0) {
							idx = CORAL_TYPE.toIndex(bId);
							population[idx]++;
							cumHealth[idx] += tempHealth;
						}
					}
					currTest.append(String.format("%03d", bId));		//TODO add high/low/mean/median/mode health
//					TODO SHOULD I JUST OUTPUT ALL THE HEALTHS?
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
		
		if(surveyNum > 0) {
			writeToFile(getCurrentPath(true)+"Concatenated Tests\\", getConcatFileName(getSurveyNum()), "", prevSurvey.append(currTest).toString());
			//figure out if I can bzip from java
			
			prevSurvey = currTest;
		}
	}
	//// END TEST AREA ////
	
	/** Kills all the coral inside the test area's bounds. **/
	private void killAll(World world, int x, int y, int z) {
		if(printMsgs) System.out.println("XXXX killAll");
		int yPos, q;
		
		if(dimensions == null) {
			System.out.println("!!!! Dimensions is null. Could not kill anything. ");
			return;
		}
		for(int xIncr = 1; xIncr < dimensions.x+1; ++xIncr) {			
			for(int zIncr = 1; zIncr < dimensions.z+1; ++zIncr) {
				yPos= world.getTopSolidOrLiquidBlock(xIncr+x, zIncr+z);
				if(Coral.isCoral(world.getBlockId(xIncr+x, yPos, zIncr+z))){
//					if(printMsgs) System.out.println("killed one coral "+new Point3D(x+xIncr, yPos, z+zIncr));	//!D
					Coral.coralBlock.removeCoral(world, x+xIncr, yPos, z+zIncr);
					q=world.getBlockId(x+xIncr, yPos, z+zIncr);	//!D
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
		return name.toString();
	}
	public String getConcatFileName(int uniqueId) {
		return getSurveyFileName(uniqueId-1).concat("~"+(uniqueId)+"_c");
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
	public static void writeToFile(String filePath, String fileName, String header, String data) {
		writeToFile(filePath, fileName, header, data, "txt");
	}
	/** Writes the given String to a file using the given name and extension. **/
	public static void writeToFile(String filePath, String fileName, String header, String data, String ext) {
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
	
	/** Returns time in minutes */
	public int getAllTestsApxRunTime() {
		return getTestsApxRunTime(0);
	}	
	/** Returns time in minutes */
	public int getTestsApxRunTime(int start) {
		if(start < 0) start = 0;
		int totalTimeInMin = 0;
		for(int i = tests.size()-1; i >= start; --i) {
			totalTimeInMin += tests.get(i).getDuration();
		}
		return (int)timeToMin((long)totalTimeInMin);
	}
	/** Returns the am/pm time that the tests will finish */
	public String getFinishTime() {
		int totalTime = getTestsApxRunTime(testNumber);
		Date now = new Date(System.currentTimeMillis());
		SimpleDateFormat f = new SimpleDateFormat("h:mm a");
		
		now.setTime(now.getTime()+(totalTime*60*1000));
		return f.format(now);
	}
	
	private void setupTests() {		/// AREA IS 1 INDEXED
		
		
		
		//
		int totalTime = getAllTestsApxRunTime();
		
		System.out.println("@@@@");
		System.out.println("@@@@ There are "+getTotalNumTests()+" tests, which will take "+(totalTime/60)+"hrs "+(totalTime%60)+"min. Check back at "+getFinishTime() );
		System.out.println("@@@@");
	}
}

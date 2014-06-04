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

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import coral.BlockCoral.CORAL_TYPE;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockControlBlock extends BlockContainer {
	private static boolean printMsgs = true;
	public static boolean showMessages() { return printMsgs; }
	
	@SuppressWarnings("unused")
	private void setupTests() {		/// AREA IS 1 INDEXED
		CORAL_TYPE[] types = {CORAL_TYPE.RED, CORAL_TYPE.BLUE, CORAL_TYPE.GREEN};
		int std_length = 45;
		int std_rpt=10;
		
		TestFactory.setDims(getDims());
///*		
		tests.add(TestFactory.get4GroupTest(10, 1, CORAL_TYPE.RED));
		tests.get(0).addPrefix("trash");
/*	//do so for eq 1,2,3
		for(int eq = 3; eq > 0; --eq) {
			for(int i = types.length-1; i >= 0; --i) {
				for(int rpt = 2; rpt > 0; --rpt) {
					tests.add(TestFactory.get4GroupTest(std_length, eq, types[i]));
				}
			}
			
//				for(int i = types.length-2; i >= 0; --i) {
//					for(int rpt = 2; rpt > 0; --rpt) {					
//						tests.add(TestFactory.getOneDirTest(std_length, eq, types[i]));
//					}
//				}
				
				for(int i = types.length-2; i >= 0; --i) {
					for(int rpt = 1; rpt > 0; --rpt) {					
						tests.add(TestFactory.getScatteredTest(std_length+20, eq, types[i])); 
					}
				}
				
				for(int i = types.length-1; i >= 0; --i) {
					for(int rpt = 2; rpt > 0; --rpt) {					
						tests.add(TestFactory.getFullTest(std_length, eq, types[i]));
					}
				}
		}	//*/
		
//		tests.add(TestFactory.get4GroupTest(6*60, 3, CORAL_TYPE.RED));
//		tests.add(TestFactory.get4GroupTest(6*60, 2, CORAL_TYPE.RED));
//		tests.add(TestFactory.get4GroupTest(6*60, 1, CORAL_TYPE.RED));
//		tests.add(TestFactory.get4GroupTest(6*60, 3, CORAL_TYPE.GREEN));
//		tests.add(TestFactory.get4GroupTest(6*60, 2, CORAL_TYPE.GREEN));
//		tests.add(TestFactory.get4GroupTest(6*60, 1, CORAL_TYPE.GREEN));
//		tests.add(TestFactory.get4GroupTest(6*60, 3, CORAL_TYPE.BLUE));
//		tests.add(TestFactory.get4GroupTest(6*60, 2, CORAL_TYPE.BLUE));
//		tests.add(TestFactory.get4GroupTest(6*60, 1, CORAL_TYPE.BLUE));
//		tests.add(TestFactory.getFullTest(6*60, 3, CORAL_TYPE.RED));
//		tests.add(TestFactory.getFullTest(6*60, 2, CORAL_TYPE.RED));
//		tests.add(TestFactory.getFullTest(6*60, 1, CORAL_TYPE.RED));
//		tests.add(TestFactory.getFullTest(6*60, 3, CORAL_TYPE.GREEN));
//		tests.add(TestFactory.getFullTest(6*60, 2, CORAL_TYPE.GREEN));
//		tests.add(TestFactory.getFullTest(6*60, 1, CORAL_TYPE.GREEN));
//		tests.add(TestFactory.getFullTest(6*60, 3, CORAL_TYPE.BLUE));
//		tests.add(TestFactory.getFullTest(6*60, 2, CORAL_TYPE.BLUE));
//		tests.add(TestFactory.getFullTest(6*60, 1, CORAL_TYPE.BLUE));

		int totalTime = getAllTestsApxRunTime();
		
		System.out.println("@@@@");
		System.out.println("@@@@ There are "+getTotalNumTests()+" tests, which will take "+(totalTime/60)+"hrs "+(totalTime%60)+"min. Check back at "+getFinishTime() );
		System.out.println("@@@@ Dims: "+getDims());
	}
	public String getFacilityName(World world, int x, int y, int z) { 
		int belowBlock = world.getBlockId(x, y-1, z);
		String name=null;
		if(belowBlock == blockDiamond.blockID) {
			name = "";
		} else if(belowBlock == coalBlock.blockID) {
			name = "Partitioned Shaded";
		} else if(belowBlock == blockGold.blockID) {
			name = "Ideal";
		} else if(belowBlock == blockIron.blockID) {
			name = "Scatter Shaded";
		} else {
			name="not defined";
		}
//		} else if(belowBlock == blockEmerald.blockID) {
//			name = "not defined";
//		} else if(belowBlock == blockLapis.blockID) {
//			name = "not defined";
//		} else if(belowBlock == blockRedstone.blockID) {
//			name = "not defined";
//		} else if(belowBlock == blockSnow.blockID) {
//			name = "not defined";
//		}
		return "Ideal";
	}
	
	/* GENERAL TEST INFORMATION */
//	private static final Point3D TEST_DIMS = new Point3D(50,0,50);	/*	//50x50		
	private static final Point3D TEST_DIMS = new Point3D(80,20,80);		//82x82		*/
	private static Point3D blockCoor = null;
	private static ArrayList<Point3D> otherBlockCoor=new ArrayList<Point3D>();
		private static Point3D getBlockCoor() {
			return blockCoor;
		}
		private static void setBlockCoor(int x, int y, int z) {
			Point3D k = new Point3D(x, y, z);
			
			if(blockCoor != null && !blockCoor.equals(k)){				
				System.out.println("!!!! More than one command block running: "+blockCoor);
				otherBlockCoor.add(k);
			} else {
				blockCoor = k;
			}
		}
		private static void clearBlockCoor(int x, int y, int z) {
			Point3D k = new Point3D(x,y,z);
			if(blockCoor != null && blockCoor.equals(k)){
				blockCoor = null;
			} else {
				System.out.println("!!!! Tried to remove "+k+" but no value is there.");
			}
			if(otherBlockCoor.size() > 0) {
				blockCoor = otherBlockCoor.get(0);
				otherBlockCoor.remove(0);
				System.out.println("!!!! An additional command block(s) still exists: "+otherBlockCoor+". Using "+blockCoor);				
			}
		}
	
	/**Dimensions is the actual w/h/l of the area it to survey.
	 * It is NOT a point in 3D.
	 */
	private static Point3D dimensions = null;
		public static Point3D getDims() { 
			if(dimensions == null) 
				return TEST_DIMS;
			else
				return dimensions;
		}
//		private void setDims(Point3D newDims, World world, int x, int y, int z) {
//			if(newDims.x > 2 && newDims.z > 2) {				
//				if(newDims.y < 2) {
//					int lowestPoint = 10000, top = 0;
//					for(int xIncr = 1; xIncr < newDims.x+1; ++xIncr) {
//						for(int zIncr = 1; zIncr < newDims.z+1; ++zIncr) {
//							top = world.getTopSolidOrLiquidBlock(x+xIncr, z+zIncr);
//							if(lowestPoint > top) {
//								lowestPoint = top;
//							}
//						}
//					}
//					dimensions = new Point3D(newDims.x, y-lowestPoint, newDims.z);
//					if(y-lowestPoint < 2) { System.out.println("!!!! Test height is <2. Tests will be ineffective."); }
//				} else {
//					dimensions = newDims;
//				}
//				relativeDimensions = new Point3D(dimensions.x+x,dimensions.y+y, dimensions.z+z); 
//			} else {
//				setDims(new Point3D(20,0,20), world, x, y, z);
//			}
//		}
	
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
    
    private static SimpleDateFormat folderFormat = new SimpleDateFormat ("yyyy-MM-dd_HH,mm'_'");
    private static SimpleDateFormat fileFormat = new SimpleDateFormat ("yyyy-MM-dd_HH,mm'_'");
    
    private static StringBuilder prevSurvey = new StringBuilder();
    private int[] population = new int[CORAL_TYPE.getNumberOfCoral()];
    private int[] cumHealth = new int[CORAL_TYPE.getNumberOfCoral()];
    
    
	
	/* CONSTRUCTOR */
	public BlockControlBlock(int id) {
		super(id, Material.ground);
		setHardness(0.5F);
		
	    setUnlocalizedName("cmdCoralBlock");
	    setCreativeTab(CreativeTabs.tabBlock);
//	    func_111022_d(ModInfo.NAME+":cmdCoralBlock");
	    setTextureName(ModInfo.NAME+":cmdCoralBlock");
	    
		setTickRandomly(false);

		setupTests();
		
	}

	/* MINECRAFT FUNCTIONS */
	/* All have to do with block behavior */
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, 
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		setBlockCoor(x, y, z);
//		if (dimensions == null) {
//			setDims(TEST_DIMS, world, x, y, z);
//		}
        if (!world.isRemote)
        {
			if(!active) { //start condition
				active = true;
				TestFactory.setTestFacility(getFacilityName(world, x, y, z));
				player.addChatMessage("Starting execution of tests");
				if(startNewTest(0, world, x, y, z)){
					firstRun = getCurrentTest().getStartTime();
				} else {
					player.addChatMessage("Unable to execute tests. Stopping.");
					active = false;
				}
			} else if(world.getBlockId(x, y+1, z) == torchWood.blockID) { //Manual override for end execution
				active = false;
				player.addChatMessage("Stopping execution of tests. Ran "+(getRunNumber()-1)+" tests in "+getTotalTimeElapsed()+"min.");
				if(printMsgs) System.out.println("===X Stopping execution of tests. Ran "+(getRunNumber()-1)+" tests in "+getTotalTimeElapsed()+"min.");
				TestConfig tcfg = getCurrentTest();
				if(tcfg != null) {
					tcfg.endTest();
				}
				resetEnvironment(world, x, y, z);
				active = false;
			} else {	//End execution warning
				player.addChatMessage(
					"Running test "+getCurrentTestNumber()+". Time remaining: "+getCurrentTest().getTimeRemaining()
					+".\n Executed "+(getRunNumber()-1)+" tests. Elapsed time: "+getTotalTimeElapsed()
					+"\nTo stop all tests, place a torch on top of the block.");
			}
        }
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
            		setBlockCoor(x, y, z);
            		
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
            
            //DRAW A BOX SHOWING THE DIMS. MAKE IT HIDEABLE USING TORCHES
//            if(world.getBlockId(x, y+1, z) == glass.blockID) {
	//	        double doubleX = dimensions.x + x;
	//	        double doubleY = dimensions.y + y;
	//	        double doubleZ = dimensions.z + z;
	//	
	//	        GL11.glPushMatrix();
	//	        GL11.glTranslated(-doubleX, -doubleY, -doubleZ);
	//	        GL11.glColor3ub((byte)255,(byte)0,(byte)0);
	//	        float mx = 9;
	//	        float my = 9;
	//	        float mz = 9;
	//	        GL11.glBegin(GL11.GL_LINES);
	//	        GL11.glVertex3f(mx+0.4f,my,mz+0.4f);
	//	        GL11.glVertex3f(mx-0.4f,my,mz-0.4f);
	//	        GL11.glVertex3f(mx+0.4f,my,mz-0.4f);
	//	        GL11.glVertex3f(mx-0.4f,my,mz+0.4f);
	//	        GL11.glEnd();
	//	        GL11.glPopMatrix();      	
//            }
        } //remote
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		System.out.println("Command Block added at "+new Point3D(x,y,z).toPoint() );
		setBlockCoor(x, y, z);
		super.onBlockAdded(world, x, y, z);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) { 
		if(active) {
	 		messagePlayers(world, "Cannot place block. Tests in progress.");
	 		System.out.println("Cannot place block. Tests in progress.");
	 		return false;
		}
		Point3D blockCoord = getBlockCoor();
	 	if ( blockCoord != null ) {
			if (world.getBlockId(x, y, z) == torchRedstoneIdle.blockID) {
				if(world.getBlockId(blockCoord.x, blockCoord.y, blockCoord.z) == this.blockID) {
					world.destroyBlock(blockCoord.x, blockCoord.y, blockCoord.z, false);
					world.destroyBlock(x,y,z, false);
					resetEnvironment(world, x, y, z);
					return super.canPlaceBlockAt(world, x, y, z);
				} else { System.out.println("!!!! Block Coordinates were not attached to a command block"); }
			} else {
				messagePlayers(world, "A coral command block already exists. To move the block, place on top of a torch.");
				System.out.println("A coral command block already exists. To move the block, place on top of a torch.");
				return false;
			}
		}
	 	return super.canPlaceBlockAt(world, x, y, z);
	}
	
//	/**If any experiments are running, you must put a torch on to break the block*/
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if(active){
			if(world.getBlockId(x, y+1, z) == torchWood.blockID) {
				resetEnvironment(world, x, y, z);
				active = false;
				getCurrentTest().endTest();
				clearBlockCoor(x, y, z);
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
			clearBlockCoor(x, y, z);
			return super.removeBlockByPlayer(world, player, x, y, z);
		}
	}
	
	/** Kills everything, sets dimensions, and zeros out control variables */
	private void resetEnvironment(World world, int x, int y, int z) {
		clearBlockCoor(x, y, z);
//		setBlockCoor(x, y, z);
//		if (dimensions == null) {
//			setDims(TEST_DIMS, world, x, y, z);
//		}
		killAll(world, x, y, z);
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
	/** Returns the test config of the current running test. Returns null if no tests are running */
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
	/** Gets the number of test that is being run. **Different than the run number** **/
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
				prevSurvey= new StringBuilder();
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
		
		//TODO take screenshot!!!!!!!!!!!!!!
		
		return success;
	}
	
	/** Writes description file. **/
	public void endTest(World world, int x, int y, int z) {
		if( testNumber >= 0) {
			getCurrentTest().endTest();
		}
		if(printMsgs) System.out.println("==== Ending test "+testNumber);
//		resetEnvironment(world, x, y, z);
	}
	
	private void setupTestFolder() {
		String folderName = mainFolderPath+"\\"+getNewTestFolderName();
		
		new File(folderName).mkdirs();
		new File(folderName+"\\Concatenated_Tests").mkdirs();
		if(printMsgs) System.out.println("~~~~ Made folders for "+getCurrentTest().getTestSignature()+"_"+getCurrentTest().getUniqueId());
	}
	
	private String getNewTestFolderName() {
		TestConfig t = getCurrentTest();
		currTestFolderName = t.getPrefix()+folderFormat.format(new Date())+t.getTestSignature()+"_"+t.getUniqueId();
		return currTestFolderName;
	}
	
	/** Run through the current test and record the block ids. Also records health and population in arrays. **/
	private void survey(World world, int x, int y, int z) {
		if(printMsgs) System.out.println("~~~~ surveyed! "+lastSurvey);
		StringBuilder currTest = new StringBuilder();
		int tempHealth, idx;
//		int high=-1, low=1000, medn=-1, mode=-1;
		Point3D dims = getDims();
		
		for(int xPos = x+1; xPos < x + dims.x; ++xPos) {
			 for(int zPos = z+1; zPos < z + dims.z; ++zPos) {
				//3-scan
				for(int yPos = y-1; yPos >= y - dims.y; --yPos) {
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
					//TODO SHOULD I JUST OUTPUT ALL THE HEALTHS?		//TODO add high/low/mean/median/mode health
					
					//Debug: 
					if(world.getBlockMaterial(xPos, yPos, zPos)!= Material.water && bId != Block.dirt.blockID) {
						System.out.println(Block.blocksList[bId].getLocalizedName()+" shouldn't be at ("+xPos+","+yPos+","+zPos+")");
					}
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
		
		String num = ""+getCurrentTest().getTimeElapsed();
		writeToFile(getCurrentPath(true), getSurveyFileName(getSurveyNum()), num, currTest.toString());
		
		if(prevSurvey.length() > 0) System.out.println(getCurrentTest().getUniqueId()+" prev: "+prevSurvey.substring(0,15));
		System.out.println(getCurrentTest().getUniqueId());
		
		if(surveyNum > 1) {
			writeToFile(getCurrentPath(true)+"Concatenated_Tests\\", getConcatFileName(getSurveyNum()), num, prevSurvey.append(currTest).toString());
			
			prevSurvey = currTest;
		}
	}
	//// END TEST AREA ////
	
	/** Kills all the coral inside the test area's bounds. **/
	private void killAll(World world, int x, int y, int z) {
		if(printMsgs) System.out.println("XXXX killAll");
		int yPos;
		Point3D dims = getDims();
		
		if(dims == null) {
			System.out.println("!!!! Dimensions is null. Could not kill anything. ");
			return;
		}
						
		for(int xIncr = 1; xIncr < dims.x+1; ++xIncr) {
			 for(int zIncr = 1; zIncr < dims.z+1; ++zIncr) {
			 	yPos= world.getTopSolidOrLiquidBlock(xIncr+x, zIncr+z);
				if(Coral.isCoral(world.getBlockId(xIncr+x, yPos, zIncr+z))){
//					if(printMsgs) System.out.println("killed one coral "+new Point3D(x+xIncr, yPos, z+zIncr));	//!D
					Coral.coralBlock.removeCoral(world, x+xIncr, yPos, z+zIncr);
				}
			 }
		}
	}
	

	/** Returns the formatted filename:
	 *  date_time_survey#_.txt OR 
	 *  ... **/
	public String getSurveyFileName(int uniqueId) {
		return getSurveyFileName(uniqueId, true);
	}
	public String getSurveyFileName(int uniqueId, boolean withTime) {
		StringBuilder name = new StringBuilder();
		
		if(withTime) name.append( fileFormat.format( new Date(System.currentTimeMillis()) ) );
		name.append(String.format("%02d_%07.2f", uniqueId, getCurrentTest().getTimeElapsed()));
		return name.toString();
	}
	public String getConcatFileName(int uniqueId) {
		StringBuilder name = new StringBuilder();
		
		name.append( fileFormat.format( new Date(System.currentTimeMillis()) ) );
		name.append( String.format("%02d~%02d_%07.2f_c",uniqueId, uniqueId-1, getCurrentTest().getTimeElapsed()) );
		return name.toString();
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
		
		if(printMsgs) System.out.println("<<<< written to file! "+fileName+"."+ext+" "+header);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		TileEntityControlBlock tecb = new TileEntityControlBlock();
		return tecb;
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	public void messagePlayers(World world, String msg) {
		if(active) {
	 		for (Object p : world.playerEntities)
		 	{
	 			((EntityPlayer)p).addChatMessage(msg);
		 	}
		}
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
		SimpleDateFormat f = new SimpleDateFormat("E MMM d h:mm a");
		
		now.setTime(now.getTime()+(totalTime*60*1000));
		return f.format(now);
	}

}

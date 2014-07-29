package coral;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class BlockCoral extends Block {
	private boolean printMsgs = BlockControlBlock.showMessages();
	// // General Functions/Variables // // 
	public static enum CORAL_TYPE {
		RED, ORANGE, YELLOW, GREEN, BLUE, PINK, DEEP, WEED, AIR, RANDOM;
		
		private static final int OFFSET = 501; //500 + BlockCoral
		public static int getBlockId(CORAL_TYPE t) {
			return t.ordinal()+OFFSET;
		}
		/** Subtracts the offset to allow for indexing into an array */
		public static int toIndex(int blockId){
			return blockId - OFFSET;
		}
		/** Returns the coral name associated with the block id */
		public static String getCoralName(int blockId) {
			if(blockId-OFFSET > 0 && blockId-OFFSET < values().length)
				return values()[blockId-OFFSET].name();
			else if (blockId >= 0 && blockId < values().length) //based on index
				return values()[blockId].name();
			else
				return null;
		}
		
		private static String csvFormat;
		public static String toCsv() {
			if(csvFormat == null) {				
				StringBuilder j = new StringBuilder();
				CORAL_TYPE[] vals = values();
				j.append(vals[0].name());
				for(int i = 1; i < getNumberOfCoral(); ++i) {
					j.append(","+vals[i].name());
				}
				
				csvFormat = j.toString();
			}
			return csvFormat;
		}
		
		public static CORAL_TYPE getRandomType(int limit) {
			CORAL_TYPE[] c = CORAL_TYPE.values();
			if(limit < 1 || limit > c.length-2) {
				limit = c.length-2;
			}
			return c[rGen.nextInt(limit)];
		}
		public static int getNumberOfCoral() {
			return values().length-1;
		}
	}
	private static ArrayList<Material> suitableGround = new ArrayList<Material>();
	private static Random rGen = new Random();

	/*** OVERRIDE THESE VARIABLES FOR NEW CORAL (! = mandatory, ? = optional) ***/
	/** Variables are added together for a final total	//!D it would be nice to make these final
	/*****/
	protected int   maxHealth;		 // (22-100)
	    private static HashMap<Point3D, Integer> healthMeter; //the actual health of the coral
    protected int   startingHealth;	 // (22-100) Beginning health of new coral 
    protected int   splitPoint; 	 // (22-100) Value at which coral divides (eg. creates new block).
    protected int   expansionCost;	 // (5-15) Cost of reproducing
    protected int   livingCost;	 	 // (1-5) How many resources a coral uses to stay alive. 
    protected int   growthFactor; 	 // (1-5) How quickly a coral grows 
    protected int   photoFactor;	 	 //? (1-4) How well the coral grows at full light level. 
//	protected int[] soilPreference; 	 //? (1-6) Some soils are more nutritious than others. This contains how much benefit a type of soil gives the coral. 
//	protected static int[] preferenceList; //x (1-3) How much a coral likes/dislikes other types. It may dislike (-) one kind more than another, but it always likes (+) its own type
	
	//Total possible states for all variables: 
	//Health variables * growth * expansion * living * soil pref * photosynthesis
	//    (803 / 4)    *   5    *    10     *   5    * (6 * # o/soils) * 4 = 3.072x10^9
	//					   5				*	5					   * 4 = 100 

	/*** OVERRIDE END ***/
	/*** RELATED FUNCTIONS ************************/
	//health
	public int getHealth(int x, int y, int z) { 
		Integer q = healthMeter.get(new Point3D(x, y, z));
		if(q == null) {
			System.out.println("!!!! No health entry for ("+x+","+y+","+z+")");
			return -1;
		} else {
			return q.intValue();
		}
	}

	public int getStartHealth() { return startingHealth; }
	public int getSplitPt() { return splitPoint; }
	protected void setHealthVars(int max, int start, int split) {
		max = (max < 20 ? 20 : max);
		maxHealth = (max > 100 ? 100 : max);

		start = (start < 20 ? 20 : start);
		startingHealth = (start > 100 ? 100 : start);

		split = (split < startingHealth ? startingHealth : split);
		splitPoint = (split > maxHealth ? maxHealth : split);
	}
	
	/** Adds amount to the health of the coral at (x,y,z). Health can be negative */
	private void grow(int amount, int x, int y, int z) {
		Point3D pt = new Point3D(x, y, z);
		Integer val = healthMeter.get(pt);
		if(val != null) {			
			int tempHealth = val + amount;
			healthMeter.put( pt, (tempHealth > maxHealth ? maxHealth : tempHealth));
		} else {
			System.out.println("No health record for ("+x+","+y+","+z+")");
		}
	}
	//cost
	public int getExpansionCost() { return expansionCost; }
	protected void setExpansionCost(int cost) {
		cost = (cost < 5 ? 5 : cost);
		expansionCost = (cost > 15 ? 15 : cost);
	}
	public int getLivingCost() { return livingCost; }
	protected void setLivingCost(int cost) {
		cost = (cost < 1 ? 1 : cost);
		livingCost = (cost > 5 ? 5 : cost);
	}
	//growth
	public int getGrowthFactor() { return growthFactor; }
	protected void setGrowthFactor(int factor) {
		if(factor < 1)
			factor = 1;
		else if(factor > 5)
			factor = 5;
		growthFactor = factor;
	}
	//photo
	public int getPhotoFactor() { return photoFactor; }
	protected void setPhotoFactor(int factor) {
		if(factor < 1)
			factor = 1;
		else if(factor > 4)
			factor = 4;
		photoFactor = factor;
	}
	//pref
	public int getPreference(CORAL_TYPE t) {
		return 5;
	}
	
	/*** RELATED END ***/
	/***/
	//TYPE 
	private final CORAL_TYPE type;
	public static CORAL_TYPE getType(World world, int x, int y, int z) {	//? do these fns belong here?
		int id = world.getBlockId(x, y, z);
		if(Coral.isCoral(id))
			return CORAL_TYPE.values()[id];
		else 
			return null; 
	}
	public static String getTypeName(World world, int x, int y, int z) {
		return CORAL_TYPE.getCoralName(world.getBlockId(x, y, z));
	}
	public static int getTypeId(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if(Coral.isCoral(id))
			return id;
		else 
			return -1; 
	}
	
	private int numCoral = 0;

	public boolean addCoral(World world, Point3D spot, int blockID) {
		if(canPlaceBlockAt(world, spot.x, spot.y, spot.z)) {			
			world.setBlock(spot.x, spot.y, spot.z, blockID);
			++numCoral;
			return true;
		} else {
			System.out.println("!!!! Cannot add coral at "+spot.toPoint()
					+" "+world.getBlockId(spot.x, spot.y+1, spot.z)
					+" "+world.getBlockId(spot.x, spot.y, spot.z)
					+" "+world.getBlockId(spot.x, spot.y-1, spot.z));
			return false;
		}
	}
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		healthMeter.put(new Point3D(x, y, z), startingHealth);
		++numCoral; //! This may not be fully accurate, as it may be overwriting another coral.
	}
	
	public void removeCoral(World world, int x, int y, int z) {
		if(Coral.isCoral(world.getBlockId(x, y, z)) ) {
//			world.destroyBlock(x, y, z, false);
			world.setBlock(x, y, z, 9);
			healthMeter.put(new Point3D(x,y,z), -1);	//? Remove?
		} else {
			System.out.println("!!!! Tried to destroy coral and got "+world.getBlockId(x, y, z));
		}
	}
	@Override
	public void onBlockDestroyedByPlayer(World par1World, int x, int y, int z, int par5) {
		healthMeter.put(new Point3D(x, y, z), -1);
		super.onBlockDestroyedByPlayer(par1World, x, y, z, par5);
	}
	
	// // CONSTRUCTOR // //
	public BlockCoral(int id,  CORAL_TYPE type, int maxHealth, int startHealth, int splitPoint, 
			int expansionCost, int growthFactor, int livingCost, int photoFactor) {
		super(id, Material.water);
		setHardness(0.5F);
		setStepSound(Block.soundStoneFootstep);

	    if(suitableGround.size() == 0) {
			suitableGround.add(Material.ground);
			suitableGround.add(Material.rock);
			suitableGround.add(Material.sand);
			suitableGround.add(Material.clay);
			suitableGround.add(Material.wood);
	    }
	    
	    setTickRandomly(true);
	    
	    //CORAL VARIABLES
		if(type == CORAL_TYPE.RANDOM) {
			this.type = CORAL_TYPE.getRandomType(0);
		} else {
			this.type = type;
		}
	    
		setHealthVars(maxHealth, startHealth, splitPoint);
		setExpansionCost(expansionCost);
		setLivingCost(livingCost);
		setGrowthFactor(growthFactor);
		setPhotoFactor(photoFactor);
	}
	public BlockCoral(int id,  CORAL_TYPE type) {
		super(id, Material.water);
		setHardness(0.5F);
		setStepSound(Block.soundStoneFootstep);
		
		if(suitableGround.size() == 0) {
			suitableGround.add(Material.ground);
			suitableGround.add(Material.rock);
			suitableGround.add(Material.sand);
			suitableGround.add(Material.clay);
			suitableGround.add(Material.wood);
		}
		
		setTickRandomly(true);
		
		//CORAL VARIABLES
		if(type == CORAL_TYPE.RANDOM) {
			this.type = CORAL_TYPE.getRandomType(0);
		} else {
			this.type = type;
		}
	}
	public BlockCoral(int id) {
		super(id,Material.water);
		type = CORAL_TYPE.RANDOM;
		if(healthMeter == null) {			
			healthMeter = new HashMap<Point3D, Integer>(50 * 50, .9f);
		}
	}

	private int supportsNumOfEq;
	// // MINECRAFT FUNCTIONS // //
	@Override	// One 'turn'. Grow, then split or die
	public void updateTick(World world, int x, int y, int z, Random random) {
		TestConfig t = BlockControlBlock.getCurrentTest();
		if(t == null) {	//if there is not a test going on, don't bother
			return;
		}
		CoralInfo[] neighbors = new CoralInfo[8];
		int numNeighbors = getNeighbors(world, x, y, z, neighbors);
		int brightness = world.getBlockLightValue(x, y, z);

		int healthModifier = -livingCost;
		
		if(printMsgs) {
			Integer val = healthMeter.get(new Point3D(x,y,z));
			if(val == null) {	
				System.out.println("No health record for ("+x+","+y+","+z+")");
				return;
//			} else {
//				System.out.println(CORAL_TYPE.getCoralName(this.blockID)+" Coral at ("+ x+", "+y+", "+z+") Num Nghbr: "+numNeighbors+" health: "+ val.intValue());
			}
		}
		
		switch (t.getGrowthEq()) {
			case 0:
				healthModifier += equation0(neighbors, numNeighbors, brightness);
				break;
			case 1:
				healthModifier += equation1(neighbors, numNeighbors, 4, brightness);
				break;
			case 2:
				healthModifier += equation1(neighbors, numNeighbors, 5, brightness);
				break;
			case 3:
				healthModifier += equation1(neighbors, numNeighbors, 6, brightness);
				break;
			case 4:
				healthModifier += equation4(neighbors, numNeighbors, 4, brightness);
				break;
			case 5:
				healthModifier += equation4(neighbors, numNeighbors, 5, brightness);
				break;
			case 6:
				healthModifier += equation4(neighbors, numNeighbors, 6, brightness);
				break;
			case 7:
				healthModifier += equation7(neighbors, numNeighbors, 4, brightness);
				break;
			case 8:
				healthModifier += equation7(neighbors, numNeighbors, 5, brightness);
				break;
			case 9:
				healthModifier += equation7(neighbors, numNeighbors, 6, brightness);
				break;
			default:
				System.out.println("!!!! Growth equation "+t.getGrowthEq()+" does not exist.");
				break;
		}
		
		supportsNumOfEq = 4;
		
		grow(healthModifier, x, y, z);
		
		//split
		if(numNeighbors < 8 && getHealth(x,y,z) >= splitPoint) {
			grow(-expansionCost, x, y, z);
			//!D we could pass the columns the neighbors are in to allow for faster finding
			Point3D newCoralSpot = findNewCoralSpot(world, x, y, z);
			if(newCoralSpot != null) {
				addCoral(world, newCoralSpot, this.blockID);
			}
		} else if(getHealth(x,y,z) <= 0) {
		//die
			removeCoral(world,x,y,z);
		}
//*/
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		int currentBlock = world.getBlockId(x, y, z);

		boolean isWaterBlock = Coral.isWater(world, x, y, z);

		if(currentBlock != 0 && !isWaterBlock) { //if there is block, but it is not a water block
			return false;
		}
		
		/*	Brightness values (getLightBrightness)
		 *  1	
		 * 	2	
		 * 	3	0.058823526
		 * 	4	0.08333333
		 * 	5	0.11111113
		 * 	6	0.14285712
		 * 	7	0.1794872
		 * 	8	0.22222225
		 * 	9	0.2727273
		 * 	10	0.33333334
		 * 	11	0.40740743
		 * 	12	0.50000006
		 * 	13	0.61904764
		 * 	14	0.77777773
		 * 	15	1
		 */
		
		//if the block is water and the block above it is water as well.
		if(isWaterBlock && Coral.isWater(world, x, y + 1, z) && isSuitableGround(world, x, y-1, z)) {
			return true;
		}
//		check if stackable, if yes, mark stacked.
//		if (world.getBlockId(x, y - 1, z) == Coral_Mod.Coral1.blockID && world.getBlockMetadata(x, y - 1, z) == 1) {
//			if(blockID == Coral_Mod.Coral1.blockID && type == 1) {
//				stacked = true;
//				return true;
//			}
//		}

		return false; //currently, canBlockStay doesn't do anything different.
//		return canBlockStay(world, x, y, z);
	}
	@Override //He used a boolean to say whether or not to allow in stationary water
	public boolean canBlockStay(World world, int x, int y, int z) {
//		int belowBlockId = world.getBlockId(x, y - 1, z);
//		int belowBlockMeta = world.getBlockMetadata(x, y - 1, z);
//		int currentBlockMeta = world.getBlockMetadata(x, y, z);

//		if (currblock is the type that can stand two tall
//			&& belowBlockId == same type
//			&& !stacked) {
//				return true;
//			}
//
//		}
		
		//should light be a factor?
		return (Coral.isWater(world, x, y + 1, z));
	}

	@Override  //only updates for blocks touching the sides
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID) {
		if(!canBlockStay(world, x, y, z)) {
			removeCoral(world, x, y, z);
		}
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	// // CORAL FUNCTIONS // //
	
	//Gets all the surrounding coral, returns the number of neighbors
	int numNeighbors=0;
	private int getNeighbors(World world, int x, int y, int z, CoralInfo[] neighbors){
		CORAL_TYPE[] types = CORAL_TYPE.values();
		int nbrIdx = 0;
		int nbrblockId = -1;
		numNeighbors = 0;
		boolean found;
		
		//scan each of the eight surrounding columns of three
		for(int northSouth = -1; northSouth <= 1; ++northSouth) { //z
			for(int leftRight = -1; leftRight <= 1; ++leftRight) { //x
				found = false;
				if(leftRight == 0 && northSouth == 0) { 
					//do nothing on middle squares
				} else {
					for(int upDown = -1; upDown <= 1 && !found; ++upDown) { //y
						nbrblockId = world.getBlockId(x+leftRight, y+upDown, z+northSouth);
						if( Coral.isCoral(nbrblockId) ){
							neighbors[nbrIdx] = new CoralInfo();
							neighbors[nbrIdx].location = new Point3D(x+leftRight, y+upDown, z+northSouth);
							neighbors[nbrIdx].type = types[nbrblockId-501];
							numNeighbors++; nbrIdx++;
							found = true;
						}
					}
				}
			}
		}
		return numNeighbors;
	}

	//Finds a suitable location for a coral to grow. Returns null if there is none.
	public Point3D findNewCoralSpot(World world, int x, int y, int z) {
		//In order for the spot to be suitable, it must meet the following conditions:
		//	1) block is water (not coral)
		//	2) above block must be water
		//  3) below block is solid
		//Suitability is converted into a score, higher is better, 0 means uninhabitable.
		//	3) Light strength
		//	4) Ground type is preferred.

		boolean placeable = false;
		Point3D returnVal = null;
		int eastWest, northSouth, groundBlock;
		
		//Randomizes the -1, 0, 1 array for random growing
		int nth = rGen.nextInt(5); //number of times to randomize
		int ew_ary[] = {-1,0,1};
		int ns_ary[] = {0, 1,-1};
		int tmp, pos1, pos2;
		for(int rotate = 0; rotate < nth; rotate++) {
			pos1 = rGen.nextInt(3);
			pos2 = rGen.nextInt(3);			
			
			tmp = ew_ary[pos1];
			ew_ary[pos1] = ew_ary[pos2];
			ew_ary[pos2] = tmp;
			
			tmp = ns_ary[pos1];
			ns_ary[pos1] = ns_ary[pos2];
			ns_ary[pos2] = tmp;
		}

		//Analyze the 5-high columns surrounding the coral in a 3-block window for suitability
		/***
		 [ ]	 [ ]	 >[ ]		> - check for water
		 [ ]	>[ ]	 >[ ]		? - is this suitable ground?
		>[ ] &	>[ ] &	 ?[ ] &
		>[ ][*]	?[ ][*]	  [x][*]
		?[ ]	 [x]	  [x]
		***/
		//Since the coral can only grow into adjacent blocks, only three blocks need to be checked 
		
		for(int xItr = 0; xItr < 3 && !placeable; ++xItr){ //&& !findOpt
			eastWest = ew_ary[xItr];
			for (int zItr = 0; zItr < 3 && !placeable; ++zItr) { // && !findOpt
				if(eastWest == 0 && ns_ary[zItr] == 0) {
					//do nothing on middle squares
				} else {
					northSouth = ns_ary[zItr];
					for(int position= -2; position < 1 && !placeable; ++position) { //y
						groundBlock = y+position;
						if(canPlaceBlockAt(world, x+eastWest, groundBlock+1, z+northSouth)) {  //aboveBlock
							placeable = true;
							returnVal = new Point3D(x+eastWest, y+position+1, z+northSouth); //add ranking?
						}
					} // y loop
				} //if
			} //z loop
		} // x loop

		return returnVal;
	}

	public boolean isSuitableGround(World world, int x, int y, int z) {
		return suitableGround.contains(world.getBlockMaterial(x, y, z));
	}
	
	// // GROWTH EQUATIONS // //
	public void showBestAndWorst(){
		int threshold = 4,
			worstNumNbrs= 8;
		CoralInfo w = new CoralInfo();
		w.type = CORAL_TYPE.RANDOM;
		CoralInfo[] worstNeighbors= {w,w,w,w,w,w,w,w};
			
		CoralInfo b;
		CoralInfo[] bestNeighbors = new CoralInfo[8];
		for(int i=7; i>=0; --i) {
			b = new CoralInfo();
			b.type = this.type;
			bestNeighbors[i] = b;
		}
		int worstEQ0 = equation0(worstNeighbors, worstNumNbrs, 0) - livingCost;
		int worstEQ1 = equation1(worstNeighbors, worstNumNbrs, threshold, 0) - livingCost;
		int worstEQ2 = equation1(worstNeighbors, worstNumNbrs, threshold+1, 0) - livingCost;
		int worstEQ3 = equation1(worstNeighbors, worstNumNbrs, threshold+2, 0) - livingCost;
		int worstEQ4 = equation4(worstNeighbors, worstNumNbrs, threshold, 0) - livingCost;
		int worstEQ5 = equation4(worstNeighbors, worstNumNbrs, threshold+1, 0) - livingCost;
		int worstEQ6 = equation4(worstNeighbors, worstNumNbrs, threshold+2, 0) - livingCost;
		
		int bestEQ0 = equation0(bestNeighbors, 8, 15) - livingCost;
		int bestEQ1 = equation1(bestNeighbors, threshold-1, threshold, 15) - livingCost;
		int bestEQ2 = equation1(bestNeighbors, threshold, threshold+1, 15) - livingCost;
		int bestEQ3 = equation1(bestNeighbors, threshold+1, threshold+2, 15) - livingCost;
		int bestEQ4 = equation4(bestNeighbors, threshold-1, threshold, 15) - livingCost;
		int bestEQ5 = equation4(bestNeighbors, threshold, threshold+1, 15) - livingCost;
		int bestEQ6 = equation4(bestNeighbors, threshold+1, threshold+2, 15) - livingCost;
		
		System.out.println(
			"\n\tWorst\tBest\tfor "+this.type+
		  "\nEq0   "+worstEQ0+"\t "+bestEQ0+"\n"+
			"Eq1   "+worstEQ1+"\t "+bestEQ1+"\n"+
			"Eq2   "+worstEQ2+"\t "+bestEQ2+"\n"+
			"Eq3   "+worstEQ3+"\t "+bestEQ3+"\n"+
			"Eq4   "+worstEQ4+"\t "+bestEQ4+"\n"+
			"Eq5   "+worstEQ5+"\t "+bestEQ5+"\n"+
			"Eq6   "+worstEQ6+"\t "+bestEQ6+"\n\n"
		);
	}
	public void genericShowBestAndWorst(){
		int bestGrowth = 5,
				worstGrowth= 1,
				bestLight  = 4,
				worstLight  = 1,
				threshold = 4,
				bestNumNbrs = threshold-1,
				worstNumNbrs= 8,
				bestLivCost = 1,
				worstLivCost= 5;
		CoralInfo w = new CoralInfo();
		w.type = CORAL_TYPE.RANDOM;
		CoralInfo[] worstNeighbors= {w,w,w,w,w,w,w,w};
		
		CoralInfo b;
		CoralInfo[] bestNeighbors = new CoralInfo[8];
		for(int i=7; i>=0; --i) {
			b = new CoralInfo();
			b.type = this.type;
			bestNeighbors[i] = b;
		}
		photoFactor = worstLight;
		int worstEQ0 = equation0(worstNeighbors, worstNumNbrs, 0) - worstLivCost;
		int worstEQ1 = equation1(worstNeighbors, worstNumNbrs, threshold, 0) - worstLivCost;
		int worstEQ2 = equation1(worstNeighbors, worstNumNbrs, threshold+1, 0) - worstLivCost;
		int worstEQ3 = equation1(worstNeighbors, worstNumNbrs, threshold+2, 0) - worstLivCost;
		
		photoFactor = bestLight;
		int bestEQ0 = equation0(bestNeighbors, 8, 15) - bestLivCost;
		int bestEQ1 = equation1(bestNeighbors, threshold-1, threshold, 15) - bestLivCost;
		int bestEQ2 = equation1(bestNeighbors, threshold, threshold+1, 15) - bestLivCost;
		int bestEQ3 = equation1(bestNeighbors, threshold+1, threshold+2, 15) - bestLivCost;
		
		System.out.println(
				"\n\tWorst\tBest\tfor "+this.type+
				"\nEq0   "+worstEQ0+"\t "+bestEQ0+"\n"+
				"Eq1   "+worstEQ1+"\t "+bestEQ1+"\n"+
				"Eq2   "+worstEQ2+"\t "+bestEQ2+"\n"+
				"Eq3   "+worstEQ3+"\t "+bestEQ3+"\n\n"
				);
	}
	
	public static int numEqs = 7;	//MAKE SURE TO UPDATE THIS NUMBER
	
	/** Equation 0: Every Coral +/-1, light value adds 1-4 (not more than it's photo factor) NO DEATH RULE*/
	private int equation0(CoralInfo[] neighbors, int numNeighbors, int lightLvl) {
		int ngbrVal = 0, growth=0;
		
		if(neighbors != null) {
			for(int i = 0; i < numNeighbors; ++i) {
				if(neighbors[i].type == this.type){
					++ngbrVal;
				} else {
					--ngbrVal;
				}
			}
		}
		
		lightLvl = (int)Math.ceil(lightLvl / 4.);
		if(lightLvl < photoFactor) {
			growth += lightLvl;
		} else {
			growth += photoFactor;
		}
		
		return ngbrVal+growth;
	}
	
	/** Equation 1-3: Every Coral +/-1, crowding factor only, light value adds 1-4 (not more than it's photo factor) */
	private int equation1(CoralInfo[] neighbors, int numNeighbors, int threshold, int lightLvl) {
		int ngbrVal = 0, growth=0;
		int friends = 0, enemies = 0;
		
		if(neighbors != null) {
			for(int i = 0; i < numNeighbors; ++i) {
				if(neighbors[i].type == this.type){
					++friends;
				} else {
					++enemies;
				}
			}
		}
		
		if(friends + enemies > threshold) {	//make this a var? crowdingFactor
			ngbrVal = -2;
		} else {
			ngbrVal = (-enemies + friends);
		}
		
		lightLvl = (int)Math.ceil(lightLvl / 4.);
		if(lightLvl < photoFactor) {
			growth += lightLvl;
		} else {
			growth += photoFactor;
		}
		
		return ngbrVal+growth;
	}
	
	/** Equation 4-6: Every Coral +/-1, crowding factor AND -enemies light value adds 1-4 (not more than it's photo factor) */
	private int equation4(CoralInfo[] neighbors, int numNeighbors, int threshold, int lightLvl) {
		int ngbrVal = 0, growth=0;
		int friends = 0, enemies = 0;
		
		if(neighbors != null) {
			for(int i = 0; i < numNeighbors; ++i) {
				if(neighbors[i].type == this.type){
					++friends;
				} else {
					++enemies;
				}
			}
		}
		
		if(friends + enemies > threshold) {
			ngbrVal = -2 - enemies;
		} else {
			ngbrVal = friends - enemies;
		}
		
		lightLvl = (int)Math.ceil(lightLvl / 4.);
		if(lightLvl < photoFactor) {
			growth += lightLvl;
		} else {
			growth += photoFactor;
		}
		
		return ngbrVal+growth;
	}
	
	/** Equation 7-9: Every Coral +/-1, crowding factor AND -enemies light value adds 1-4 (not more than it's photo factor) */
	private int equation7(CoralInfo[] neighbors, int numNeighbors, int threshold, int lightLvl) {
		int ngbrVal = 0, growth=growthFactor;
		int friends = 0, enemies = 0;
	
		if(neighbors != null) {
			for(int i = 0; i < numNeighbors; ++i) {
				if(neighbors[i].type == this.type){
					++friends;
				} else {
					++enemies;
				}
			}
		}
		
		if(friends + enemies > threshold) {
			ngbrVal = -2 - enemies;
		} else {
			ngbrVal = friends - enemies;
		}
		
		lightLvl = (int)Math.ceil(lightLvl / 4.);
		if(lightLvl < photoFactor) {
			growth += lightLvl;
		} else {
			growth += photoFactor;
		}
		
		return ngbrVal+growth;
	}
	
	// IF YOU ADD AN EQUATION, UPDATE numEqs

}

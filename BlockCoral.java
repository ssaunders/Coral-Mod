package coral;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockCoral extends Block {

	/*MEMBER VARS AND GETTERS/SETTERS*/
	private static int[] preferenceList;
	public static enum CORAL_TYPE {RED, ORANGE, YELLOW, GREEN, BLUE, PINK, DEEP, WEED, AIR, RANDOM}
	public static CORAL_TYPE getRandomType(int limit) {

		CORAL_TYPE[] c = CORAL_TYPE.values();
		if(limit < 1 || limit > c.length-2) {
			limit = c.length-2;
		}
		return c[(int)(Math.random()*(limit))];
	}
	public static String getCoralName(int id) {
		return CORAL_TYPE.values()[id-501].name();
	}
	private static ArrayList<Material> suitableGround = new ArrayList<Material>();

	public static int STD_GROWTH = 5;

	public static int FULL_HEALTH = 75;
//	public static BlockCoral generateRandomCoral() {
//		CORAL_TYPE t = getRandomType(0);
//	}

	/*** OVERRIDE THESE VARIABLES FOR NEW CORAL ***/
	protected int health;
	protected int splitPoint; //Point at which coral divides (eg. creates new block)
//	Returns result of some kind of equation relating brightness and STD_GROWTH involving photosynthesis factor;
	//!N Could have multiple equations here, based on switch
	private int individualGrowthVal(int brightness) {
		return STD_GROWTH;
	}

	/*** RELATED FUNCTIONS ************************/
	public void setHealth(int h) {
		h = (h <= 0 ? FULL_HEALTH : h);
		health = (h > 100 ? 100 : h);
	}
	public int getHealth() { return health; }
	public void setSplitPt(int h) {
		splitPoint = (h < 10 ? 100 : h);
	}
	public int getSplitPt() { return splitPoint; }
	/*** OVERRIDE END ***/
	
	//Grow amount
	private void grow(int amount) {
		setHealth(getHealth()+amount);
	}

	private CORAL_TYPE type;
	public CORAL_TYPE getType() {
		return type;
	}
	private void setType(CORAL_TYPE t){
		if(t == CORAL_TYPE.RANDOM) {
			type = getRandomType(0);
		} else {
			type = t;
		}
//		preferenceList = PREFERENCES[t.ordinal()];
	}
	// vv ?
	public int getPreference(CORAL_TYPE t) {
		return 5;
	}

	private void getAttackStrength(CORAL_TYPE t) {
		//return the value stored in preferences?
	}

	int numNeighbors=0;

	/*CONSTRUCTORS*/
	public BlockCoral(int id) {
		super(id, Material.water);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);

		suitableGround.add(Material.ground);
		suitableGround.add(Material.rock);
		suitableGround.add(Material.sand);
		suitableGround.add(Material.clay);
		suitableGround.add(Material.wood);

	    setTickRandomly(true);
	}

	/*FUNCTIONS*/
	@Override	// One 'turn'
	public void updateTick(World world, int x, int y, int z, Random random) {
		Point3D[] neighbors = getNeighbors(world, x, y, z);
		int brightness = world.getBlockLightValue(x, y, z);
		
		System.out.println(getCoralName(this.blockID)+" Coral at ("+ x+", "+y+", "+z+") Num Nghbr: "+numNeighbors+" health: "+health);
		
		grow(individualGrowthVal(brightness));
		
//		for(int itr = 0; itr < numNeighbors; ++itr) {
//			//help from same kind, hurt from others
//			health += neighbors[itr].getPreference(type);
//		}
		//bubbles?
		//split
		if(health >= splitPoint) {
			setHealth(FULL_HEALTH);
			Point3D newCoralSpot = findNewCoralSpot(world, x, y, z);
			if(newCoralSpot != null) {
				world.setBlock(newCoralSpot.x, newCoralSpot.y, newCoralSpot.z, this.blockID);
			}
		}
	}
	//Gets all the surrounding coral
	private Point3D[] getNeighbors(World world, int x, int y, int z){
		Point3D[] neighbors = new Point3D[8];
		int nbrIdx = 0;
		int nbrblockId = -1;
		numNeighbors = 0;
		//scan each of the eight surrounding columns
		for(int northSouth = -1; northSouth <= 1; ++northSouth) { //z
			for(int leftRight = -1; leftRight <= 1; ++leftRight) { //x
				if(leftRight != 0 && northSouth != 0) { //do nothing on middle squares
					for(int upDown = -1; upDown <= 1; ++upDown) { //y
						nbrblockId = world.getBlockId(x+leftRight, y+upDown, z+northSouth);
						if( Coral.isCoral(nbrblockId) ){
							neighbors[nbrIdx] = new Point3D(x+leftRight, y+upDown, z+northSouth);
							numNeighbors++;
						}
					}
				}
			}
		}
		return neighbors;
	}

	public Point3D findNewCoralSpot(World world, int x, int y, int z) {
		//In order for the spot to be suitable, it must meet the following conditions:
		//	1) block is water
		//	2) above block must be water
		//  3) below block is solid
		//Suitability is converted into a score, higher is better, 0 means uninhabitable.
		//	3) Light strength
		//	4) Ground type is preferred.

		Material groundBlock, placeSpot, aboveBlock;
		boolean placeable = false;
		Point3D returnVal = null;
		
		//Randomizes the -1, 0, 1 array for random growing
		int nth = (int)(Math.random()*4)+1; //keeps things a little more random
		int ew_ary[] = {-1,0,1};
		int ns_ary[] = {0, 1,-1};
		int tmp, pos1, pos2;
		for(int rotate = 0; rotate < nth; rotate++) {
			pos1 = (int)(Math.random()*2);
			pos2 = (int)(Math.random()*2);
			
			tmp = ew_ary[pos1];
			ew_ary[pos1] = ew_ary[pos2];
			tmp = ew_ary[pos2];
			
			tmp = ns_ary[pos1];
			ns_ary[pos1] = ns_ary[pos2];
			tmp = ns_ary[pos2];
		}

		//Analyze the 6-high columns surrounding the coral in a 3-block window for suitability
		/***
		 [ ]	 [ ]
		 [ ]	 [ ]
		 [ ]	>[ ]
		>[ ] &	>[ ] &
		>[ ][*]	>[ ][*]
		>[ ]	 [ ]
		***/
		
		for(int eastWest : ew_ary){
			for (int northSouth : ns_ary) {
		
//		for(int eastWest = -1; eastWest <= 1; ++eastWest) { //x
//			for(int northSouth = -1; northSouth <= 1; ++northSouth) { //z
				if(eastWest != 0 && northSouth != 0) { //do nothing on middle squares
					//grab three blocks, starting from two below
					groundBlock  = world.getBlockMaterial(x+eastWest, y-2, z+northSouth);
					placeSpot  = world.getBlockMaterial(x+eastWest, y-1, z+northSouth);
					aboveBlock = world.getBlockMaterial(x+eastWest, y, z+northSouth);
					for(int position= -2; position < 2 && !placeable; ++position) { //y
						if( isSuitableGround(groundBlock)
						   && placeSpot  == Material.water
						   && aboveBlock == Material.water) {
							placeable = true;
							returnVal = new Point3D(x+eastWest, y+position, z+northSouth); //add ranking?
						} else {
							//if not suitable, move the window
							groundBlock = placeSpot;
							placeSpot = aboveBlock;
							aboveBlock = world.getBlockMaterial(x+eastWest, y+position, z+northSouth);
						}
					} // y loop
				} //if
			} //z loop
		} // x loop

		return returnVal;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		int currentBlock = world.getBlockId(x, y, z);

		boolean isWaterBlock = Coral.checkWater(world, x, y, z);

		if(currentBlock != 0 && !isWaterBlock) { //if there is block, but it is not a water block
			return false;
		}
		//if the block is water and the block above it is water as well.
		if(isWaterBlock && Coral.checkWater(world, x, y + 1, z)) {
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
		int belowBlockId = world.getBlockId(x, y - 1, z);
		int belowBlockMeta = world.getBlockMetadata(x, y - 1, z);
		int currentBlockMeta = world.getBlockMetadata(x, y, z);

//		if (currblock is the type that can stand two tall
//			&& belowBlockId == same type
//			&& !stacked) {
//				return true;
//			}
//
//		}

		return (Coral.checkWater(world, x, y + 1, z));
	}
	// vv ?
	@Override  //only updates for blocks touching the sides
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID) {
//		if(!canBlockStay(world, x, y, z)) {
//			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
//			world.setBlockToAir(x, y, z);
//		}
		System.out.println("neighbor changed: ["+neighborBlockID+"] ("+x+","+y+","+z+")");
	}

	/** Utility functions: Simple, generic functions **/
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public int amountShaded(World world, int x, int y, int z) {
		return world.getBlockLightValue(x, y, z);
	}

	public boolean isSuitableGround(Material m) {
		return suitableGround.contains(m);
	}

}

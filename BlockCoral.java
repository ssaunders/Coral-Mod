package coral;

import java.util.ArrayList;
import java.util.Random;

import com.google.common.collect.ArrayListMultimap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.world.World;

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
	public static int STD_GROWTH = 5;
//	public static BlockCoral generateRandomCoral() {
//		CORAL_TYPE t = getRandomType(0);
//	}
	
	private int health;
	public static int FULL_HEALTH = 75;
	public void setHealth(int h) {
		h = (h <= 0 ? FULL_HEALTH : h);
		health = (h > 100 ? 100 : h);
	}
	public int getHealth() { return health; }
	
	private CORAL_TYPE type;
	public CORAL_TYPE getType() {
		return type;
	}
	public void setType(CORAL_TYPE t){
		if(t == CORAL_TYPE.RANDOM) {
			type = getRandomType(0);
		} else {
			type = t;
		}
//		preferenceList = PREFERENCES[t.ordinal()];
	}
	public int getPreference(CORAL_TYPE t) {
		return 5;
	}
	
	private void getAttackStrength(CORAL_TYPE t) {
		//return the value stored in preferences?
	}
	
	/*CONSTRUCTORS*/
	public BlockCoral(int id) {
		super(id, Material.water);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("coralBlock");
	    setCreativeTab(CreativeTabs.tabBlock);
	    func_111022_d(ModInfo.NAME+":genericCoral");
	    
	    setTickRandomly(true);
	}
	
	/*FUNCTIONS*/
	@Override	// One 'turn'
	public void updateTick(World world, int x, int y, int z, Random random) {
		Point3D[] neighbors = getNeighbors(world, x, y, z);
		int numNeighbors = neighbors.length;
//		int brightness = world.getBlockLightValue(x, y, z);
//		int brightness = 12;
		
		System.out.println(world+" "+ x+" "+y+" "+z+" "+random);
//		health += individualGrowthVal(brightness);
		
//		for(int itr = 0; itr < numNeighbors; ++itr) {
//			//help from same kind, hurt from others
//			health += neighbors[itr].getPreference(type);
//		}
		//bubbles?
		//split
		//amountShaded
	}
	private Point3D[] getNeighbors(World world, int x, int y, int z){
		Point3D[] neighbors = new Point3D[8];
		int nbrIdx = 0;
		int blockId = -1;
		//scan three rings of eight around
		for(int upDown = -1; upDown <= 1; ++upDown) { //y
			for(int leftRight = -1; leftRight <= 1; ++leftRight) { //x
				for(int northSouth = -1; northSouth <= 1; ++northSouth) { //z
					if(leftRight == 0 && northSouth == 0) {
						//do nothing on middle squares
					} else {						
						blockId = world.getBlockId(x+leftRight, y+upDown, z+northSouth);
						if( Coral.isCoral(blockId) ){
							neighbors[nbrIdx] = new Point3D(x+leftRight, y+upDown, z+northSouth);
						}
						System.out.println("("+(x+leftRight)+","+(y+upDown)+","+(z+northSouth)+") ID: "+blockId);
					}
				}
			}
		}
		return neighbors;
	}
	private int individualGrowthVal(int brightness) {
//		return some kind of equation that relates brightness and STD_GROWTH involving photosynthesis factor;
		return STD_GROWTH;
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		int currentBlock = world.getBlockId(x, y, z);
		
		System.out.println("Light value: "+world.getBlockLightValue(x, y, z));
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
	
	@Override  //only updates for blocks touching the sides 
	public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID) {
//		if(!canBlockStay(world, x, y, z)) {
//			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
//			world.setBlockToAir(x, y, z);
//		}
		System.out.println("neighbor changed: ["+neighborBlockID+"] ("+x+","+y+","+z+")");
	}
	
	/** Functionality functions **/
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

}

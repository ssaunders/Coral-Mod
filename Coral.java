package coral;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import coral.BlockCoral.CORAL_TYPE;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid=ModInfo.ID, name=ModInfo.NAME, version=ModInfo.VERSION)
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class Coral {

	/***BLOCK INSTANCES***/
	public static BlockCoral coralBlock;
	public static BlockCoralRed redCoral;
	public static Block orangeCoral;
	public static Block yellowCoral;
	public static BlockCoralBlue blueCoral;
	public static BlockCoralGreen greenCoral;  
	public static Block pinkCoral;
	public static Block deepCoral;
	public static Block weedCoral;
	public static Block airCoral;
	public static CoralCommandBlock commandBlock;
	
	public static int growthEquation=0;

	/***MOD STUFF***/
    // The instance of your mod that Forge uses.
    @Instance(ModInfo.NAME)
    public static Coral instance;

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="coral.client.ClientProxy", serverSide="coral.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler // used in 1.6.2
    public void preInit(FMLPreInitializationEvent event) {
    	int first = 500;
    	commandBlock = new CoralCommandBlock(first - 1);
    	coralBlock = new BlockCoral(first);
    	redCoral   = new BlockCoralRed(  CORAL_TYPE.getBlockId(CORAL_TYPE.RED));
    	blueCoral  = new BlockCoralBlue( CORAL_TYPE.getBlockId(CORAL_TYPE.BLUE));
    	greenCoral = new BlockCoralGreen(CORAL_TYPE.getBlockId(CORAL_TYPE.GREEN));
    }

    @EventHandler // used in 1.6.2
    public void load(FMLInitializationEvent event) {
    	GameRegistry.registerBlock(redCoral, "redCoral");
    	LanguageRegistry.addName(redCoral, "Red Coral");
    	MinecraftForge.setBlockHarvestLevel(redCoral, "shovel", 0);
    	MinecraftForge.setBlockHarvestLevel(redCoral, "pickaxe", 0);
    	MinecraftForge.setBlockHarvestLevel(redCoral, "axe", 0);

    	GameRegistry.registerBlock(blueCoral, "blueCoral");
    	LanguageRegistry.addName(blueCoral, "Blue Coral");
    	MinecraftForge.setBlockHarvestLevel(blueCoral, "shovel", 0);
    	MinecraftForge.setBlockHarvestLevel(blueCoral, "pickaxe", 0);
    	MinecraftForge.setBlockHarvestLevel(blueCoral, "axe", 0);

    	GameRegistry.registerBlock(greenCoral, "greenCoral");
    	LanguageRegistry.addName(greenCoral, "Green Coral");
    	MinecraftForge.setBlockHarvestLevel(greenCoral, "shovel", 0);
    	MinecraftForge.setBlockHarvestLevel(greenCoral, "pickaxe", 0);
    	MinecraftForge.setBlockHarvestLevel(greenCoral, "axe", 0);
    	
    	GameRegistry.registerBlock(commandBlock, "cmdCoralBlock");
    	LanguageRegistry.addName(commandBlock, "Coral Command Block");

        proxy.registerRenderers();
    }

    @EventHandler // used in 1.6.2
    public void postInit(FMLPostInitializationEvent event) {
        // Stub Method
    }


    /** UTILITY FUNCTIONS **/
	public static boolean isWater(World world, int x, int y, int z) {
		if(world.getBlockMaterial(x, y, z) == Material.water ) {

			int blockID = world.getBlockId(x, y, z);

			if(blockID > 0 && blockID < Block.blocksList.length && !Coral.isCoral(blockID) ) {
				return true;
//				Block waterBlock = Block.blocksList[blockID];
//				if(waterBlock != null) {
//					boolean waterStationary = waterBlock.func_82506_l();
//					return waterStationary == stationary;
//				}
			}
//			System.out.println("Unknown block ID: " + blockID);
		}

		return false;
	}

	public static boolean isCoral(int blockID) {
		// TODO: make this an array check pulled from config
		return blockID > 500 && blockID < 500+CORAL_TYPE.RANDOM.ordinal();
	}

}
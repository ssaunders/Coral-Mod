package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

//THE PICKY EATER
public class BlockCoralRed extends BlockCoral {
	public BlockCoralRed(int id) {
		super(id, CORAL_TYPE.RED, 70, 30, 60, 10, 3, 3, 3);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("redCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
//	    func_111022_d(ModInfo.NAME+":redCoral");
	    setTextureName(ModInfo.NAME+":redCoral");
	}

}

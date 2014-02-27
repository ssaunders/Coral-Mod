package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

//THE COLONIST
public class BlockCoralGreen extends BlockCoral {
	public BlockCoralGreen(int id) {
		super(id, CORAL_TYPE.GREEN, 30, 20, 25, 5, 3, 3, 3);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("greenCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
//	    func_111022_d(ModInfo.NAME+":greenCoral");
	    setTextureName(ModInfo.NAME+":greenCoral");
    }

}

package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import coral.BlockCoral.CORAL_TYPE;

//THE XXX
public class BlockCoralDeep extends BlockCoral {
	public BlockCoralDeep(int id) {
		super(id, CORAL_TYPE.PINK, 70, 30, 60, 10, 3, 3, 3);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("deepCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
//	    func_111022_d(ModInfo.NAME+":deepCoral");
	    setTextureName(ModInfo.NAME+":deepCoral");
	}

}

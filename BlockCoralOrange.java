package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import coral.BlockCoral.CORAL_TYPE;

//THE XXX
public class BlockCoralOrange extends BlockCoral {
	public BlockCoralOrange(int id) {
		super(id, CORAL_TYPE.ORANGE, 70, 30, 60, 10, 3, 3, 3);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("orangeCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
//	    func_111022_d(ModInfo.NAME+":orangeCoral");
	    setTextureName(ModInfo.NAME+":orangeCoral");
	}

}

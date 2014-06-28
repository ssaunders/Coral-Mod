package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import coral.BlockCoral.CORAL_TYPE;

//THE XXX
public class BlockCoralYellow extends BlockCoral {
	public BlockCoralYellow(int id) {
		super(id, CORAL_TYPE.YELLOW, 70, 30, 60, 10, 3, 3, 3);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("yellowCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
//	    func_111022_d(ModInfo.NAME+":yellowCoral");
	    setTextureName(ModInfo.NAME+":yellowCoral");
	}

}

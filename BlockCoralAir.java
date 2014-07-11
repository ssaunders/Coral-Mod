package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import coral.BlockCoral.CORAL_TYPE;

//THE XXX
public class BlockCoralAir extends BlockCoral {
	public BlockCoralAir(int id) {
		super(id, CORAL_TYPE.AIR, 70, 30, 60, 10, 3, 3, 3);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("airCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
//	    func_111022_d(ModInfo.NAME+":airCoral");
	    setTextureName(ModInfo.NAME+":airCoral");
	}

}

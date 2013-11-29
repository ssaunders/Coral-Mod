package coral;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockCoralRed extends BlockCoral {

	public BlockCoralRed(int id) {
		super(id);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("redCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
	    func_111022_d(ModInfo.NAME+":redCoral");
	}

}

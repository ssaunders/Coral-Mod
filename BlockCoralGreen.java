package coral;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockCoralGreen extends BlockCoral {
	private int individualGrowthVal(int brightness) {
		return STD_GROWTH;
	}

	public BlockCoralGreen(int id) {
		super(id);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("greenCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
	    func_111022_d(ModInfo.NAME+":greenCoral");

	    setHealth(FULL_HEALTH);
	    setSplitPt(80);
    }

}

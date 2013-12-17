package coral;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockCoralBlue extends BlockCoral {
	private int individualGrowthVal(int brightness) {
		return STD_GROWTH;
	}

	public BlockCoralBlue(int id) {
		super(id);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("blueCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
	    func_111022_d(ModInfo.NAME+":blueCoral");

	    setHealth(FULL_HEALTH);
	    setSplitPt(80);
	}

}

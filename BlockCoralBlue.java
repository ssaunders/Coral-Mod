package coral;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

//THE PHILANTHROPIST
public class BlockCoralBlue extends BlockCoral {
	public BlockCoralBlue(int id) {
		super(id, CORAL_TYPE.BLUE, 100, 70, 85, 6, 3, 2, 4);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("blueCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
	    func_111022_d(ModInfo.NAME+":blueCoral");
	}

}

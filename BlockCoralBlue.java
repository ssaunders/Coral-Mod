package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

//THE PHILANTHROPIST
/* Characteristics
 * Lower living cost
 * Higher photosynthesis
 */
public class BlockCoralBlue extends BlockCoral {
	public BlockCoralBlue(int id) {
		super(id, CORAL_TYPE.BLUE, 100, 70, 85, 6, 3, 2, 4);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("blueCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
//	    func_111022_d(ModInfo.NAME+":blueCoral");
	    setTextureName(ModInfo.NAME+":blueCoral");
	}

	public boolean hasGoodValues() {
		return maxHealth == 100 &&
				startingHealth == 70 &&
				splitPoint == 85 &&
				expansionCost == 6 &&
				growthFactor == 3 &&
				livingCost == 2 &&
				photoFactor == 4;
	}
}

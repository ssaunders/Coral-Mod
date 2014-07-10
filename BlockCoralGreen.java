package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

//THE COLONIST
/* Characteristics:
 * Starting health > split point
 * Low max health
 * 
 */
public class BlockCoralGreen extends BlockCoral {
	public BlockCoralGreen(int id) {
		//(id, type, maxHealth, startHealth, splitPoint, expansionCost, growthFactor, livingCost, photoFactor)
		super(id, CORAL_TYPE.GREEN, 30, 20, 25, 5, 3, 3, 3);
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("greenCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
//	    func_111022_d(ModInfo.NAME+":greenCoral");
	    setTextureName(ModInfo.NAME+":greenCoral");
    }
	
	public boolean hasGoodValues() {
		return maxHealth == 30 &&
				startingHealth == 20 &&
				splitPoint == 25 &&
				expansionCost == 5 &&
				growthFactor == 3 &&
				livingCost == 3 &&
				photoFactor == 3;
	}

}

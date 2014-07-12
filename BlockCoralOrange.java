package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import coral.BlockCoral.CORAL_TYPE;

//THE XXX
public class BlockCoralOrange extends BlockCoral {
	public BlockCoralOrange(int id) {
		super(id, CORAL_TYPE.ORANGE);
		
//		setHealthVars(100, 70, 85);
//		setExpansionCost(6);
//		setLivingCost(2);
//		setGrowthFactor(3);
//		setPhotoFactor(4);
		
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("orangeCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
	    func_111022_d(ModInfo.NAME+":orangeCoral");
//	    setTextureName(ModInfo.NAME+":orangeCoral");
	}
	public void showBestAndWorst() {
		super.showBestAndWorst();
	}

//	public boolean hasGoodValues() {
//		return maxHealth ==  &&
//				startingHealth == &&
//				splitPoint == &&
//				expansionCost == &&
//				growthFactor == &&
//				livingCost == &&
//				photoFactor == ;			
//	}
}

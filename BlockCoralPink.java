package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import coral.BlockCoral.CORAL_TYPE;

//THE XXX
public class BlockCoralPink extends BlockCoral {
	public BlockCoralPink(int id) {
		super(id, CORAL_TYPE.PINK);
		
//		setHealthVars(100, 70, 85);
//		setExpansionCost(6);
//		setLivingCost(2);
//		setGrowthFactor(3);
//		setPhotoFactor(4);
		
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("pinkCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
//	    func_111022_d(ModInfo.NAME+":pinkCoral");
	    setTextureName(ModInfo.NAME+":pinkCoral");
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

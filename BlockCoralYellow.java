package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import coral.BlockCoral.CORAL_TYPE;

//THE XXX
public class BlockCoralYellow extends BlockCoral {
	public BlockCoralYellow(int id) {
		super(id, CORAL_TYPE.YELLOW);
		
//		setHealthVars(100, 70, 85);
//		setExpansionCost(6);
//		setLivingCost(2);
//		setGrowthFactor(3);
//		setPhotoFactor(4);
		
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("yellowCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
	    func_111022_d(ModInfo.NAME+":yellowCoral");
//	    setTextureName(ModInfo.NAME+":yellowCoral");
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

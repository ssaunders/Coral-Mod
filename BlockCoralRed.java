package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

//THE PICKY EATER
public class BlockCoralRed extends BlockCoral {
	public BlockCoralRed(int id) {
		super(id, CORAL_TYPE.RED);

		setHealthVars(70, 30, 60);
		setExpansionCost(10);
		setLivingCost(3);
		setGrowthFactor(3);
		setPhotoFactor(3);
		
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("redCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
	    func_111022_d(ModInfo.NAME+":redCoral");
//	    setTextureName(ModInfo.NAME+":redCoral");
	}

	public void showBestAndWorst() {
		super.showBestAndWorst();
	}
	
	public boolean hasGoodValues() {
		return maxHealth ==  70 &&
				startingHealth == 30 &&
				splitPoint == 60 &&
				expansionCost == 10 &&
				growthFactor == 3 &&
				livingCost == 3 &&
				photoFactor == 3;			
	}

}

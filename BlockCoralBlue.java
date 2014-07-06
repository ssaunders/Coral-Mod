package coral;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

//THE PHILANTHROPIST
public class BlockCoralBlue extends BlockCoral {
	public BlockCoralBlue(int id) {
		super(id, CORAL_TYPE.BLUE);
		
		setHealthVars(100, 70, 85);
		setExpansionCost(6);
		setLivingCost(2);
		setGrowthFactor(3);
		setPhotoFactor(4);
		
		setHardness(0.5F);
	    setStepSound(Block.soundStoneFootstep);
	    setUnlocalizedName("blueCoral");
	    setCreativeTab(CreativeTabs.tabBlock);
	    func_111022_d(ModInfo.NAME+":blueCoral");
//	    setTextureName(ModInfo.NAME+":blueCoral");
	}
	
	public void showBestAndWorst() {
		super.showBestAndWorst();
	}

}

package eddxample.toybox.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import eddxample.toybox.features.PistonHelper;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BlockPistonBase.class)
public class MixinBlockPistonBase extends BlockDirectional
{
	protected MixinBlockPistonBase(Material materialIn)
	{
		super(materialIn);
		isSticky = false;
	}
	
	@Shadow
	private final boolean isSticky;
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		boolean flag = PistonHelper.isNecessary() && playerIn.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.AIR;
		
		if (!worldIn.isRemote && flag)
		{
			boolean extending = !(Boolean)state.getValue(BlockPistonBase.EXTENDED);
			if((!pos.equals(PistonHelper.pistonPos) || !PistonHelper.activated) && (extending || isSticky))
			{
				EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
				
				IBlockState state1 = worldIn.getBlockState(pos.offset(enumfacing));
				
				BlockPistonStructureHelper ph = null;

				if (!extending)
				{
					worldIn.setBlockState(pos, Blocks.BARRIER.getDefaultState(), 2);
					worldIn.setBlockToAir(pos);
					worldIn.setBlockToAir(pos.offset(enumfacing));
				}
				
				ph = new BlockPistonStructureHelper(worldIn, pos, enumfacing, extending);
				ph.canMove();
				PistonHelper.set(pos, ph.getBlocksToMove().toArray(new BlockPos[12]), ph.getBlocksToDestroy().toArray(new BlockPos[12]), ph.canMove(), extending);
				PistonHelper.activated = true;
				
				if (!extending)
				{
					worldIn.setBlockState(pos, state, 2);
					worldIn.setBlockState(pos.offset(enumfacing), state1, 2);
				}
			}
			else PistonHelper.activated = false;
		}
		
		if (worldIn.isRemote)
		{
			return isSticky || !(Boolean)state.getValue(BlockPistonBase.EXTENDED);
		}
		
		return flag;
	}
}

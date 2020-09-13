package thebetweenlands.common.block.container;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.tile.TileEntityCrabPot;
import thebetweenlands.util.AdvancedStateMap;

public class BlockCrabPot extends BlockSwampWater implements ITileEntityProvider, IStateMappedBlock {

	public BlockCrabPot() {
		this(FluidRegistry.SWAMP_WATER, Material.WATER);
		this.setHardness(0.5F);
	}

	public BlockCrabPot(Fluid fluid, Material materialIn) {
		super(fluid, materialIn);
		setHardness(0.5F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setUnderwaterBlock(true);
		this.setDefaultState(blockState.getBaseState().withProperty(LEVEL, 0));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(LEVEL);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCrabPot();
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		if (!world.isRemote) {
			
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.isRemote)
			return true;

		if (!world.isRemote) {
			if (world.getTileEntity(pos) instanceof TileEntityCrabPot) {
				TileEntityCrabPot tile = (TileEntityCrabPot) world.getTileEntity(pos);
				if (!player.getHeldItem(hand).isEmpty() && tile.getItems().get(0).isEmpty()) {
					ItemStack stack = player.getHeldItem(hand).splitStack(1);
					if (!stack.isEmpty()) {
						tile.getItems().set(0, stack);
						tile.markForUpdate();
						return true;
					}
				} else if (!tile.getItems().get(0).isEmpty()) {
					ItemStack extracted = tile.getItems().get(0);
					if (!extracted.isEmpty()) {
						EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
						item.motionX = item.motionY = item.motionZ = 0D;
						world.spawnEntity(item);
						tile.getItems().set(0, ItemStack.EMPTY);
						tile.markForUpdate();
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityCrabPot tile = (TileEntityCrabPot) world.getTileEntity(pos);
		if (tile != null) 
			InventoryHelper.dropInventoryItems(world, pos, tile);
		super.breakBlock(world, pos, state);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public ItemBlock getItemBlock() {
		return ICustomItemBlock.getDefaultItemBlock(this);
	}

}

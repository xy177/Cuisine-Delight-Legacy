package xy177.cuisinedelightlegacy.content.block;

import com.wdcftgg.farmersdelightlegacy.common.util.HeatSourceHelper;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xy177.cuisinedelightlegacy.content.item.CuisineSkilletItem;
import xy177.cuisinedelightlegacy.content.item.PlateItem;
import xy177.cuisinedelightlegacy.content.item.SpatulaItem;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;

import javax.annotation.Nullable;

public class CuisineSkilletBlock extends BlockHorizontal implements ITileEntityProvider {
    public static final PropertyBool SUPPORT = PropertyBool.create("support");
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(1 / 16D, 0, 1 / 16D, 15 / 16D, 3 / 16D, 15 / 16D);

    public CuisineSkilletBlock() {
        super(Material.IRON);
        setHardness(2);
        setSoundType(SoundType.METAL);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(SUPPORT, false));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new CuisineSkilletBlockEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing face, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof CuisineSkilletBlockEntity)) return false;
        CuisineSkilletBlockEntity skillet = (CuisineSkilletBlockEntity) tile;
        ItemStack stack = player.getHeldItem(hand);
        Item item = stack.getItem();
        if (item == CDItems.SPATULA || item == CDItems.PLATE) {
            if (!skillet.isCooking()) return false;
            if (item == CDItems.SPATULA && player.getCooldownTracker().hasCooldown(item)) return true;
            if (!world.isRemote) {
                if (item == CDItems.SPATULA) {
                    SpatulaItem.stir(skillet, player, stack);
                } else {
                    player.setHeldItem(hand, PlateItem.plate(skillet.cookingData, world, player, stack));
                    skillet.cookingData.contents.clear();
                    skillet.cookingData.setSpeed(1);
                    skillet.sync();
                }
            }
            return true;
        }
        if (IngredientConfig.getEntry(stack) == null) return false;
        if (!world.isRemote && CuisineSkilletItem.addItem(skillet.cookingData, player, stack, skillet.canCook(),
                skillet.slowCook(), skillet.getAmount(), pos)) {
            skillet.sync();
        }
        return true;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return state.withProperty(SUPPORT, tile != null && HeatSourceHelper.hasVisualSupportForCookware(tile.getWorld(), pos));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, SUPPORT);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return withRotation(state, mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return SHAPE;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        drops.add(tile instanceof CuisineSkilletBlockEntity ? ((CuisineSkilletBlockEntity) tile).toItemStack()
                : new ItemStack(CDItems.SKILLET));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof CuisineSkilletBlockEntity ? ((CuisineSkilletBlockEntity) tile).toItemStack()
                : new ItemStack(CDItems.SKILLET);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity tile,
                             ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);
        world.setBlockToAir(pos);
    }

}

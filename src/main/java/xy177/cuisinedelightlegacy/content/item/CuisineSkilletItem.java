package xy177.cuisinedelightlegacy.content.item;

import com.wdcftgg.farmersdelightlegacy.common.registry.ModSounds;
import com.wdcftgg.farmersdelightlegacy.common.util.HeatSourceHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xy177.cuisinedelightlegacy.content.block.CuisineSkilletBlockEntity;
import xy177.cuisinedelightlegacy.content.logic.CookingData;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig;
import xy177.cuisinedelightlegacy.init.data.CDConfig;
import xy177.cuisinedelightlegacy.init.data.LangData;

import javax.annotation.Nullable;
import java.util.List;

public class CuisineSkilletItem extends ItemBlock {
    public CuisineSkilletItem(Block block) {
        super(block);
        setMaxStackSize(1);
    }

    @Nullable
    public static CookingData getData(ItemStack stack) {
        NBTTagCompound tag = stack.getSubCompound("Cooking");
        return tag == null ? null : CookingData.read(tag);
    }

    public static void setData(ItemStack stack, @Nullable CookingData data) {
        if (data == null) stack.removeSubCompound("Cooking");
        else stack.setTagInfo("Cooking", data.write());
    }

    public static boolean canUse(ItemStack stack, EntityPlayer player) {
        return stack.getSubCompound("Cooking") != null || player.isBurning()
                || EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) > 0
                || HeatSourceHelper.hasHeatSourceNearby(player.world, player.getPosition(), 1);
    }

    public static ItemStack otherHand(EntityPlayer player, EnumHand hand) {
        return player.getHeldItem(hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
    }

    public static boolean addItem(CookingData data, EntityPlayer player, ItemStack held, boolean heated,
                                  boolean slow, int amount, BlockPos pos) {
        if (!heated) {
            player.sendStatusMessage(LangData.MSG_NO_HEAT.get(), true);
            return false;
        }
        if (data.contents.size() >= CDConfig.maxIngredient) {
            player.sendStatusMessage(LangData.MSG_FULL.get(), true);
            return false;
        }
        ItemStack item = held.splitStack(amount);
        if (slow) data.setSpeed(0.5F);
        data.addItem(item, player.world.getTotalWorldTime());
        if (item.getItem().hasContainerItem(item)) {
            ItemStack remainder = item.getItem().getContainerItem(item);
            if (!remainder.isEmpty()) {
                remainder.setCount(item.getCount());
                giveItem(player, remainder);
            }
        }
        playSound(player.world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, false);
        return true;
    }

    static void giveItem(EntityPlayer player, ItemStack stack) {
        if (!player.addItemStackToInventory(stack) && !stack.isEmpty()) player.dropItem(stack, false);
    }

    public static void playSound(World world, double x, double y, double z, boolean sizzle) {
        SoundEvent sound = sizzle ? ModSounds.SKILLET_SIZZLE : ModSounds.SKILLET_ADD_FOOD;
        if (sound != null) {
            float pitch = 0.9F + world.rand.nextFloat() * 0.2F;
            if (world.isRemote) world.playSound(x, y, z, sound, SoundCategory.BLOCKS, 0.4F, pitch, false);
            else world.playSound(null, x, y, z, sound, SoundCategory.BLOCKS, 0.4F, pitch);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ItemStack other = otherHand(player, hand);
        if (other.getItem() instanceof PlateItem || other.getItem() instanceof SpatulaItem || player.isSneaking()) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (IngredientConfig.getEntry(other) == null) {
            if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
                player.sendStatusMessage((other.isEmpty() ? LangData.MSG_PLACE_HELP : LangData.MSG_NOT_INGREDIENT).get(), true);
            }
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (!world.isRemote) {
            CookingData data = getData(stack);
            if (data == null) data = new CookingData();
            int fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
            int amount = 1 + EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
            if (addItem(data, player, other, canUse(stack, player), fireAspect == 1, amount, player.getPosition())) {
                setData(stack, data);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        return player.isSneaking() ? super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ)
                : EnumActionResult.PASS;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                float hitX, float hitY, float hitZ, IBlockState state) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state)) return false;
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote && tile instanceof CuisineSkilletBlockEntity) {
            ((CuisineSkilletBlockEntity) tile).setSkilletItem(stack);
        }
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.FIRE_ASPECT || enchantment == Enchantments.EFFICIENCY;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 14;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isRemote && entity instanceof EntityPlayer && stack.getSubCompound("Cooking") != null
                && (selected || ((EntityPlayer) entity).getHeldItemOffhand() == stack) && world.rand.nextInt(10) == 0) {
            playSound(world, entity.posX, entity.posY, entity.posZ, true);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(LangData.ENCH_FIRE.get().getFormattedText());
            tooltip.add(LangData.ENCH_EFFICIENCY.get().getFormattedText());
        } else {
            tooltip.add(LangData.ENCH_SHIFT.get().getFormattedText());
        }
    }
}

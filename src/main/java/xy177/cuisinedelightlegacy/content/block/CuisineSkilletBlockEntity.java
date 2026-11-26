package xy177.cuisinedelightlegacy.content.block;

import com.wdcftgg.farmersdelightlegacy.common.util.HeatSourceHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import xy177.cuisinedelightlegacy.content.item.CuisineSkilletItem;
import xy177.cuisinedelightlegacy.content.item.SpatulaItem;
import xy177.cuisinedelightlegacy.content.logic.CookingData;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;

public class CuisineSkilletBlockEntity extends TileEntity implements ITickable {
    public CookingData cookingData = new CookingData();
    private ItemStack baseItem = ItemStack.EMPTY;
    private long stirTime = -SpatulaItem.ANIM_TIME;

    @Override
    public void update() {
        if (world.isRemote && isCooking() && world.rand.nextInt(10) == 0) {
            CuisineSkilletItem.playSound(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, true);
        }
    }

    public boolean isCooking() {
        return !cookingData.contents.isEmpty();
    }

    public void setSkilletItem(ItemStack stack) {
        baseItem = stack.copy();
        baseItem.setCount(1);
        CookingData data = CuisineSkilletItem.getData(stack);
        if (data != null) cookingData = data;
        CuisineSkilletItem.setData(baseItem, null);
        sync();
    }

    public ItemStack toItemStack() {
        ItemStack stack = baseItem.isEmpty() ? new ItemStack(CDItems.SKILLET) : baseItem.copy();
        if (isCooking()) CuisineSkilletItem.setData(stack, cookingData);
        return stack;
    }

    public int getAmount() {
        return 1 + EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, baseItem);
    }

    public boolean canCook() {
        return HeatSourceHelper.isCookwareHeated(world, pos)
                || EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, baseItem) > 0;
    }

    public boolean slowCook() {
        BlockPos heatPos = HeatSourceHelper.resolveCookwareHeatSourcePos(world, pos);
        if (HeatSourceHelper.isDirectHeatSource(world, heatPos)) {
            ResourceLocation heat = world.getBlockState(heatPos).getBlock().getRegistryName();
            if (heat != null && (heat.equals(Blocks.FIRE.getRegistryName()) || heat.getResourcePath().endsWith("campfire")
                    || "soul_fire".equals(heat.getResourcePath()))) return true;
        }
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, baseItem) == 1;
    }

    public float getStirPercent(float partialTick) {
        return Math.max(0, SpatulaItem.ANIM_TIME - (world.getTotalWorldTime() - stirTime) - partialTick)
                / SpatulaItem.ANIM_TIME;
    }

    public void stir(long time, int reduce) {
        cookingData.stir(time, reduce);
        stirTime = time;
        sync();
    }

    public void sync() {
        markDirty();
        if (world != null && !world.isRemote) {
            net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        baseItem = new ItemStack(tag.getCompoundTag("Skillet"));
        cookingData = CookingData.read(tag.getCompoundTag("Cooking"));
        if (tag.hasKey("StirTime")) stirTime = tag.getLong("StirTime");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (!baseItem.isEmpty()) tag.setTag("Skillet", baseItem.writeToNBT(new NBTTagCompound()));
        tag.setTag("Cooking", cookingData.write());
        tag.setLong("StirTime", stirTime);
        return tag;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }
}

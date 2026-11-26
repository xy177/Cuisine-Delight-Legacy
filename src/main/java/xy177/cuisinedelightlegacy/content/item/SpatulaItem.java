package xy177.cuisinedelightlegacy.content.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xy177.cuisinedelightlegacy.content.block.CuisineSkilletBlockEntity;
import xy177.cuisinedelightlegacy.content.logic.CookingData;
import xy177.cuisinedelightlegacy.init.data.LangData;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;

import javax.annotation.Nullable;
import java.util.List;

public class SpatulaItem extends Item {
    public static final int ANIM_TIME = 20;

    public SpatulaItem() {
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack spatula = player.getHeldItem(hand);
        ItemStack skillet = CuisineSkilletItem.otherHand(player, hand);
        CookingData data = skillet.getItem() == CDItems.SKILLET ? CuisineSkilletItem.getData(skillet) : null;
        if (data == null) return new ActionResult<>(EnumActionResult.PASS, spatula);
        if (!world.isRemote) {
            data.stir(world.getTotalWorldTime(), getReduction(spatula));
            CuisineSkilletItem.setData(skillet, data);
            player.getCooldownTracker().setCooldown(CDItems.SKILLET, ANIM_TIME);
            player.getCooldownTracker().setCooldown(this, ANIM_TIME);
            CuisineSkilletItem.playSound(world, player.posX, player.posY, player.posZ, false);
        }
        if (world.isRemote) player.swingArm(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, spatula);
    }

    public static void stir(CuisineSkilletBlockEntity skillet, EntityPlayer player, ItemStack spatula) {
        World world = player.world;
        skillet.stir(world.getTotalWorldTime(), getReduction(spatula));
        player.getCooldownTracker().setCooldown(spatula.getItem(), ANIM_TIME);
        CuisineSkilletItem.playSound(world, player.posX, player.posY, player.posZ, false);
    }

    private static int getReduction(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0 ? 20 : 0;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.SILK_TOUCH;
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
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add((GuiScreen.isShiftKeyDown() ? LangData.ENCH_SILK : LangData.ENCH_SHIFT).get().getFormattedText());
    }
}

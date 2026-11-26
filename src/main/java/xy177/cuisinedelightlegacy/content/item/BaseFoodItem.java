package xy177.cuisinedelightlegacy.content.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xy177.cuisinedelightlegacy.content.logic.CookedFoodData;
import xy177.cuisinedelightlegacy.content.recipe.CuisineRecipeContainer;
import xy177.cuisinedelightlegacy.events.FoodEatenEvent;
import xy177.cuisinedelightlegacy.init.data.LangData;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;

import javax.annotation.Nullable;
import java.util.List;

public class BaseFoodItem extends ItemFood {
    public BaseFoodItem() {
        super(0, 0.0F, false);
        setMaxStackSize(1);
    }

    @Nullable
    public static CookedFoodData getData(ItemStack stack) {
        NBTTagCompound tag = stack.getSubCompound("Cooked");
        return tag == null ? null : CookedFoodData.read(tag);
    }

    public static void setData(ItemStack stack, CookedFoodData data) {
        stack.setTagInfo("Cooked", data.write());
    }

    @Override
    public int getHealAmount(ItemStack stack) {
        CookedFoodData data = getData(stack);
        return data != null && data.canEat() ? data.getHunger() : 0;
    }

    @Override
    public float getSaturationModifier(ItemStack stack) {
        CookedFoodData data = getData(stack);
        return data != null && data.canEat() ? data.getSaturation() : 0.0F;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        CookedFoodData data = getData(stack);
        if (data != null && data.canEat() && player.canEat(data.score == 100)) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.EAT;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        CookedFoodData data = getData(stack);
        return data != null && data.score == 100 ? 16 : 32;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        CookedFoodData data = getData(stack);
        if (data == null || !data.canEat() || !(entity instanceof EntityPlayer)) return stack;
        EntityPlayer player = (EntityPlayer) entity;
        if (!world.isRemote) {
            player.getFoodStats().addStats(data.getHunger(), data.getSaturation());
            data.getEffects().forEach(player::addPotionEffect);
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP,
                    SoundCategory.PLAYERS, 0.5F, 0.9F + world.rand.nextFloat() * 0.1F);
            player.addStat(StatList.getObjectUseStats(this));
            if (player instanceof EntityPlayerMP) CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
            MinecraftForge.EVENT_BUS.post(new FoodEatenEvent(player, data));
        }
        if (--data.size == 0) return new ItemStack(CDItems.PLATE);
        setData(stack, data);
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        NBTTagCompound display = stack.getSubCompound("Display");
        if (display != null) {
            tooltip.add(LangData.INFO_DISPLAY.get(display.getDouble("Min"), display.getDouble("Max")).getFormattedText());
            return;
        }
        CookedFoodData data = getData(stack);
        if (data == null) {
            tooltip.add(LangData.CREATIVE.get().getFormattedText());
            return;
        }
        if (data.canEat()) {
            tooltip.add(LangData.SERVE_SIZE.get(data.size).getFormattedText());
            tooltip.add(LangData.SCORE.get(data.score).getFormattedText());
        } else {
            tooltip.add(LangData.BAD_FOOD.get().getFormattedText());
        }
        if (GuiScreen.isShiftKeyDown()) {
            for (CookedFoodData.Entry entry : data.entries) {
                String name = entry.stack.getDisplayName();
                if (entry.burnt) tooltip.add(LangData.BAD_BURNT.get(name).getFormattedText());
                if (entry.raw) tooltip.add(LangData.BAD_RAW.get(name).getFormattedText());
                if (entry.overcooked) tooltip.add(LangData.BAD_OVERCOOKED.get(name).getFormattedText());
                if (!entry.burnt && !entry.raw && !entry.overcooked) tooltip.add(LangData.GOOD.get(name).getFormattedText());
            }
        } else if (data.total > 0) {
            tooltip.add(LangData.SHIFT.get().getFormattedText());
            for (ItemStack entry : new CuisineRecipeContainer(data).list) {
                double percent = Math.round(1000D * entry.getCount() / data.total) / 10D;
                tooltip.add(entry.getDisplayName() + ": " + percent + "%");
            }
        }
    }
}

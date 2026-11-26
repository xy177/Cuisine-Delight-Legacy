package xy177.cuisinedelightlegacy.content.item;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import xy177.cuisinedelightlegacy.content.logic.CookedFoodData;
import xy177.cuisinedelightlegacy.content.logic.CookingData;
import xy177.cuisinedelightlegacy.content.recipe.BaseCuisineRecipe;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;

public class PlateItem extends Item {
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack plate = player.getHeldItem(hand);
        ItemStack skillet = CuisineSkilletItem.otherHand(player, hand);
        CookingData data = skillet.getItem() == CDItems.SKILLET ? CuisineSkilletItem.getData(skillet) : null;
        if (data == null || data.contents.isEmpty()) return new ActionResult<>(EnumActionResult.PASS, plate);
        if (!world.isRemote) {
            plate = plate(data, world, player, plate);
            CuisineSkilletItem.setData(skillet, null);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, plate);
    }

    public static ItemStack plate(CookingData data, World world, EntityPlayer player, ItemStack plate) {
        data.stir(world.getTotalWorldTime(), 0);
        CookedFoodData food = CookedFoodData.of(data);
        ItemStack result = BaseCuisineRecipe.findBestMatch(food);
        int experience = food.score * food.size / 100;
        while (experience > 0) {
            int value = EntityXPOrb.getXPSplit(experience);
            world.spawnEntity(new EntityXPOrb(world, player.posX, player.posY, player.posZ, value));
            experience -= value;
        }
        if (plate.getCount() == 1) return result;
        plate.shrink(1);
        CuisineSkilletItem.giveItem(player, result);
        return plate;
    }
}

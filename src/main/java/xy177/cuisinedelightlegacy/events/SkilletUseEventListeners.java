package xy177.cuisinedelightlegacy.events;

import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xy177.cuisinedelightlegacy.content.item.CuisineSkilletItem;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;

import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class SkilletUseEventListeners {
    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() == EnumHand.MAIN_HAND && preferOffHand(event.getEntityPlayer())) {
            event.setCancellationResult(EnumActionResult.PASS);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.isSneaking() || event.getWorld().getBlockState(event.getPos()).getBlock() == CDItems.SKILLET_BLOCK) return;
        if (player.getHeldItemMainhand().getItem() == CDItems.SKILLET || preferOffHand(player)) {
            event.setUseBlock(Event.Result.DENY);
        }
        if (event.getHand() == EnumHand.MAIN_HAND && preferOffHand(player)) {
            event.setCancellationResult(EnumActionResult.PASS);
            event.setCanceled(true);
        }
    }

    private static boolean preferOffHand(EntityPlayer player) {
        if (player.isSneaking()) return false;
        ItemStack main = player.getHeldItemMainhand();
        ItemStack off = player.getHeldItemOffhand();
        Item offItem = off.getItem();
        if (main.getItem() == CDItems.SKILLET) return offItem == CDItems.SPATULA || offItem == CDItems.PLATE;
        return offItem == CDItems.SKILLET && IngredientConfig.getEntry(main) != null && CuisineSkilletItem.canUse(off, player);
    }
}

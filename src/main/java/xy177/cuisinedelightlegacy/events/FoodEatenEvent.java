package xy177.cuisinedelightlegacy.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import xy177.cuisinedelightlegacy.content.logic.CookedFoodData;

public class FoodEatenEvent extends PlayerEvent {
    public final CookedFoodData food;

    public FoodEatenEvent(EntityPlayer player, CookedFoodData food) {
        super(player);
        this.food = food;
    }
}

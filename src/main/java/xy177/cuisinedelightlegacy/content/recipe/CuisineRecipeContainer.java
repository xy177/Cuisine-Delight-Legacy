package xy177.cuisinedelightlegacy.content.recipe;

import net.minecraft.item.ItemStack;
import xy177.cuisinedelightlegacy.content.logic.CookedFoodData;

import java.util.ArrayList;
import java.util.List;

public class CuisineRecipeContainer {
    public final List<ItemStack> list = new ArrayList<>();

    public CuisineRecipeContainer(CookedFoodData food) {
        for (CookedFoodData.Entry entry : food.entries) {
            if (entry.itemSize <= 0) continue;
            ItemStack match = ItemStack.EMPTY;
            for (ItemStack stack : list) {
                if (ItemStack.areItemsEqual(stack, entry.stack) && ItemStack.areItemStackTagsEqual(stack, entry.stack)) {
                    match = stack;
                    break;
                }
            }
            if (match.isEmpty()) {
                match = entry.stack.copy();
                match.setCount(entry.itemSize);
                list.add(match);
            } else {
                match.grow(entry.itemSize);
            }
        }
    }
}

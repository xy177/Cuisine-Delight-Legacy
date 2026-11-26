package xy177.cuisinedelightlegacy.content.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.List;

public final class CuisineRecipeMatch {
    public final Ingredient ingredient;
    public final double min, max, score, bonus;

    public CuisineRecipeMatch(Ingredient ingredient, double min, double max, double score, double bonus) {
        this.ingredient = ingredient;
        this.min = min;
        this.max = max;
        this.score = score;
        this.bonus = bonus;
    }

    public double reduce(List<ItemStack> inputs) {
        int amount = 0;
        int total = 0;
        for (ItemStack stack : inputs) {
            if (ingredient.apply(stack)) amount += stack.getCount();
            total += stack.getCount();
        }
        if (total == 0) return 0;
        double percentage = (double) amount / total;
        if (percentage < min || percentage > max) return 0;
        double range = (max - min) / 2;
        return range > 0 && bonus > 0 ? score + bonus * Math.abs(percentage - (min + max) / 2) / range : score;
    }
}

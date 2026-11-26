package xy177.cuisinedelightlegacy.content.logic;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class IngredientConfig {
    private static final List<IngredientEntry> ENTRIES = new ArrayList<>();

    private IngredientConfig() {
    }

    public static void add(Ingredient ingredient, FoodType type, int minTime, int maxTime, int stirTime,
                           float rawPenalty, float overcookPenalty, int size, int nutrition, PotionEffect... effects) {
        if (ingredient == Ingredient.EMPTY) return;
        add(ingredient, type, minTime, maxTime, stirTime, rawPenalty, overcookPenalty, size, nutrition, false, effects);
    }

    public static IngredientEntry add(Ingredient ingredient, FoodType type, int minTime, int maxTime, int stirTime,
                                      float rawPenalty, float overcookPenalty, int size, int nutrition,
                                      boolean override, PotionEffect... effects) {
        IngredientEntry entry = new IngredientEntry(ingredient, type, minTime, maxTime, stirTime,
                rawPenalty, overcookPenalty, size, nutrition, effects);
        if (override) {
            ENTRIES.add(0, entry);
        } else {
            ENTRIES.add(entry);
        }
        return entry;
    }

    @Nullable
    public static IngredientEntry getEntry(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        for (IngredientEntry entry : ENTRIES) {
            if (entry.ingredient.apply(stack)) {
                return entry;
            }
        }
        return null;
    }

    public static ItemStack[] getAll(FoodType type) {
        return ENTRIES.stream().filter(entry -> entry.type == type)
                .flatMap(entry -> Arrays.stream(entry.ingredient.getMatchingStacks()).filter(stack -> getEntry(stack) == entry))
                .toArray(ItemStack[]::new);
    }

    public static final class IngredientEntry {
        public final Ingredient ingredient;
        public final FoodType type;
        public final int minTime;
        public final int maxTime;
        public final int stirTime;
        public final float rawPenalty;
        public final float overcookPenalty;
        public final int size;
        public final int nutrition;
        public final PotionEffect[] effects;

        private IngredientEntry(Ingredient ingredient, FoodType type, int minTime, int maxTime, int stirTime,
                                float rawPenalty, float overcookPenalty, int size, int nutrition, PotionEffect[] effects) {
            this.ingredient = ingredient;
            this.type = type;
            this.minTime = minTime;
            this.maxTime = maxTime;
            this.stirTime = stirTime;
            this.rawPenalty = rawPenalty;
            this.overcookPenalty = overcookPenalty;
            this.size = size;
            this.nutrition = nutrition;
            this.effects = effects;
        }
    }
}

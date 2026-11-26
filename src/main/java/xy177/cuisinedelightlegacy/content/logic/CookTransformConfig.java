package xy177.cuisinedelightlegacy.content.logic;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig.IngredientEntry;
import xy177.cuisinedelightlegacy.content.logic.transform.Stage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CookTransformConfig {
    private static final List<CookTransform> TRANSFORMS = new ArrayList<>();
    private static final Map<IngredientEntry, CookTransform> CUSTOM_TRANSFORMS = new HashMap<>();

    private CookTransformConfig() {
    }

    public static void item(ItemStack input, ItemStack result, Stage stage) {
        if (!input.isEmpty()) item(Ingredient.fromStacks(input), result, stage);
    }

    public static void item(Ingredient input, ItemStack result, Stage stage) {
        if (!result.isEmpty()) TRANSFORMS.add(new CookTransform(input, result, stage, 0));
    }

    public static void item(IngredientEntry input, ItemStack result) {
        CUSTOM_TRANSFORMS.put(input, new CookTransform(input.ingredient, result, Stage.COOKED, 0));
    }

    public static void fluid(ItemStack input, int color) {
        if (!input.isEmpty()) {
            TRANSFORMS.add(new CookTransform(Ingredient.fromStacks(input), ItemStack.EMPTY, Stage.RAW, color));
        }
    }

    @Nullable
    public static CookTransform get(ItemStack stack) {
        if (!CUSTOM_TRANSFORMS.isEmpty()) {
            CookTransform custom = CUSTOM_TRANSFORMS.get(IngredientConfig.getEntry(stack));
            if (custom != null) return custom;
        }
        for (CookTransform transform : TRANSFORMS) {
            if (transform.input.apply(stack)) {
                return transform;
            }
        }
        return null;
    }

    public static ItemStack renderStack(Stage stage, ItemStack stack) {
        CookTransform transform = get(stack);
        return transform == null ? stack : transform.renderStack(stage, stack);
    }

    public static final class CookTransform {
        private final Ingredient input;
        private final ItemStack result;
        public final Stage stage;
        public final int color;

        private CookTransform(Ingredient input, ItemStack result, Stage stage, int color) {
            this.input = input;
            this.result = result;
            this.stage = stage;
            this.color = color;
        }

        public ItemStack renderStack(Stage current, ItemStack stack) {
            if (result.isEmpty() || current.ordinal() < stage.ordinal()) {
                return stack;
            }
            ItemStack copy = result.copy();
            copy.setCount(stack.getCount());
            if (stack.hasTagCompound()) {
                copy.setTagCompound(stack.getTagCompound().copy());
            }
            return copy;
        }
    }
}

package xy177.cuisinedelightlegacy.content.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import xy177.cuisinedelightlegacy.content.logic.FoodType;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig;

import javax.annotation.Nullable;
import java.util.Locale;

public class FoodTypeIngredient extends Ingredient {
    public final FoodType type;

    private FoodTypeIngredient(FoodType type) {
        super(0);
        this.type = type;
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        IngredientConfig.IngredientEntry entry = stack == null ? null : IngredientConfig.getEntry(stack);
        return entry != null && entry.type == type;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return IngredientConfig.getAll(type);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    public static final class Factory implements IIngredientFactory {
        @Override
        public Ingredient parse(JsonContext context, JsonObject json) {
            return new FoodTypeIngredient(FoodType.valueOf(JsonUtils.getString(json, "food_type").toUpperCase(Locale.ROOT)));
        }
    }
}

package xy177.cuisinedelightlegacy.content.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.ArrayList;
import java.util.List;

public class PlateCuisineRecipe extends BaseCuisineRecipe {
    private PlateCuisineRecipe(ItemStack result, List<CuisineRecipeMatch> list, double bonus, double modifier) {
        super(result, list, bonus, modifier);
    }

    public static final class Factory implements IRecipeFactory {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            List<CuisineRecipeMatch> list = new ArrayList<>();
            for (JsonElement element : JsonUtils.getJsonArray(json, "list")) {
                JsonObject match = element.getAsJsonObject();
                double min = number(match, "min");
                double max = number(match, "max");
                if (min < 0 || max < min) throw new JsonSyntaxException("Expected 0 <= min <= max");
                list.add(new CuisineRecipeMatch(CraftingHelper.getIngredient(match.get("ingredient"), context),
                        min, max, number(match, "score"), number(match, "bonus")));
            }
            JsonObject item = new JsonObject();
            item.addProperty("item", JsonUtils.getString(json, "holderItem"));
            ItemStack result = CraftingHelper.getItemStack(item, context);
            if (result.isEmpty()) throw new JsonSyntaxException("holderItem must be a non-empty item");
            return new PlateCuisineRecipe(result, list, number(json, "saturationBonus"), number(json, "saturationBonusModifier"));
        }

        private static double number(JsonObject json, String key) {
            JsonElement field = json.get(key);
            if (field == null || !field.isJsonPrimitive() || !field.getAsJsonPrimitive().isNumber()) {
                throw new JsonSyntaxException("Expected a number for '" + key + "'");
            }
            double value = field.getAsDouble();
            if (!Double.isFinite(value)) throw new JsonSyntaxException("Expected a finite number for '" + key + "'");
            return value;
        }
    }
}

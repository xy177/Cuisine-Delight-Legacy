package xy177.cuisinedelightlegacy.init.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Logger;
import xy177.cuisinedelightlegacy.content.logic.CookTransformConfig;
import xy177.cuisinedelightlegacy.content.logic.FoodType;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig;
import xy177.cuisinedelightlegacy.content.recipe.BaseCuisineRecipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;

import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.MOD_ID;
import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.loc;

public final class CustomConfig {
    private CustomConfig() {
    }

    public static void load(Path directory, Logger logger) {
        Path recipes = directory.resolve("recipes");
        try {
            Files.createDirectories(recipes);
            for (String name : new String[]{"ingredients.json", "ingredients.example.json", "recipe.example.json", "guide.txt"}) {
                Path target = directory.resolve(name);
                if (Files.exists(target)) continue;
                try (InputStream source = CustomConfig.class.getResourceAsStream("/assets/" + MOD_ID + "/config/" + name)) {
                    Files.copy(source, target);
                }
            }
        } catch (IOException e) {
            logger.warn("Could not prepare custom cooking files in {}: {}", directory, e.getMessage());
        }

        JsonContext context = new JsonContext(MOD_ID);
        loadIngredients(directory.resolve("ingredients.json"), context, logger);
        if (Files.isDirectory(recipes)) loadRecipes(recipes, context, logger);
    }

    private static void loadIngredients(Path file, JsonContext context, Logger logger) {
        if (!Files.isRegularFile(file)) return;
        try {
            JsonArray entries = JsonUtils.getJsonArray(read(file), "entries");
            for (int i = 0; i < entries.size(); i++) {
                try {
                    loadIngredient(entries.get(i).getAsJsonObject(), context);
                } catch (RuntimeException e) {
                    logger.warn("Skipping ingredient #{} in {}: {}", i + 1, file, e.getMessage());
                }
            }
        } catch (IOException | RuntimeException e) {
            logger.warn("Could not read custom ingredients in {}: {}", file, e.getMessage());
        }
    }

    private static void loadIngredient(JsonObject json, JsonContext context) {
        JsonElement input = json.get("ingredient");
        validateIngredient(input);
        Ingredient ingredient = CraftingHelper.getIngredient(input, context);
        FoodType type = FoodType.valueOf(JsonUtils.getString(json, "type").toUpperCase(Locale.ROOT));
        int minTime = JsonUtils.getInt(json, "min_time");
        int maxTime = JsonUtils.getInt(json, "max_time");
        int stirTime = JsonUtils.getInt(json, "stir_time");
        if (minTime < 0 || maxTime < minTime || stirTime <= 0) {
            throw new JsonSyntaxException("Expected 0 <= min_time <= max_time and stir_time > 0");
        }
        float rawPenalty = JsonUtils.getFloat(json, "raw_penalty");
        float overcookPenalty = JsonUtils.getFloat(json, "overcook_penalty");
        if (!(rawPenalty >= 0 && rawPenalty <= 1 && overcookPenalty >= 0 && overcookPenalty <= 1)) {
            throw new JsonSyntaxException("raw_penalty and overcook_penalty must be between 0 and 1");
        }
        int size = JsonUtils.getInt(json, "size");
        int nutrition = JsonUtils.getInt(json, "nutrition");
        if (size < 0 || nutrition < 0) throw new JsonSyntaxException("size and nutrition must be non-negative");

        JsonArray effects = JsonUtils.getJsonArray(json, "effects", new JsonArray());
        PotionEffect[] potions = new PotionEffect[effects.size()];
        for (int i = 0; i < potions.length; i++) {
            JsonObject effect = effects.get(i).getAsJsonObject();
            String id = JsonUtils.getString(effect, "effect");
            Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(id));
            if (potion == null) throw new JsonSyntaxException("Unknown effect '" + id + "'");
            int level = JsonUtils.getInt(effect, "level");
            int time = JsonUtils.getInt(effect, "time");
            if (level < 0 || time <= 0) throw new JsonSyntaxException("Expected effect level >= 0 and time > 0");
            potions[i] = new PotionEffect(potion, time, level);
        }

        ItemStack cooked = ItemStack.EMPTY;
        if (json.has("cooked")) {
            cooked = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "cooked"), context);
            if (cooked.isEmpty()) throw new JsonSyntaxException("cooked must be a non-empty item");
        }
        boolean override = JsonUtils.getBoolean(json, "override", false);
        IngredientConfig.IngredientEntry entry = IngredientConfig.add(ingredient, type, minTime, maxTime, stirTime,
                rawPenalty, overcookPenalty, size, nutrition, override, potions);
        if (!cooked.isEmpty()) CookTransformConfig.item(entry, cooked);
    }

    private static void validateIngredient(JsonElement json) {
        if (json != null && json.isJsonArray()) {
            for (JsonElement option : json.getAsJsonArray()) validateIngredient(option);
            return;
        }
        if (json == null || !json.isJsonObject()) throw new JsonSyntaxException("Expected ingredient object or array");
        String type = JsonUtils.getString(json.getAsJsonObject(), "type", "minecraft:item");
        if (!"minecraft:item".equals(type) && !"forge:ore_dict".equals(type)) {
            throw new JsonSyntaxException("Ingredient entries support minecraft:item and forge:ore_dict");
        }
    }

    private static void loadRecipes(Path directory, JsonContext context, Logger logger) {
        try (Stream<Path> files = Files.list(directory)) {
            files.filter(file -> Files.isRegularFile(file) && file.getFileName().toString().endsWith(".json"))
                    .sorted().forEach(file -> loadRecipe(file, context, logger));
        } catch (IOException e) {
            logger.warn("Could not list custom plate recipes in {}: {}", directory, e.getMessage());
        }
    }

    private static void loadRecipe(Path file, JsonContext context, Logger logger) {
        try {
            String fileName = file.getFileName().toString();
            String name = fileName.substring(0, fileName.length() - ".json".length());
            if (!name.matches("[a-z0-9_.-]+")) throw new JsonSyntaxException("Expected a lowercase recipe file name");
            JsonObject json = read(file);
            String type = context.appendModId(JsonUtils.getString(json, "type"));
            if (!loc("plate_cuisine").toString().equals(type)) throw new JsonSyntaxException("Expected type " + loc("plate_cuisine"));
            if (!CraftingHelper.processConditions(json, "conditions", context)) return;
            BaseCuisineRecipe recipe = (BaseCuisineRecipe) CraftingHelper.getRecipe(json, context);
            BaseCuisineRecipe.addRecipe(loc("plate_cuisine/" + name), recipe, JsonUtils.getBoolean(json, "override", false));
        } catch (IOException | RuntimeException e) {
            logger.warn("Skipping custom plate recipe {}: {}", file, e.getMessage());
        }
    }

    private static JsonObject read(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return new JsonParser().parse(reader).getAsJsonObject();
        }
    }
}

package xy177.cuisinedelightlegacy.compat;

import com.google.gson.JsonObject;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import static xy177.cuisinedelightlegacy.content.logic.FoodType.*;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig;
import static xy177.cuisinedelightlegacy.init.data.CDConfigGen.*;

public final class FoodDelight {
    private FoodDelight() {
    }

    public static void add() {
        nether();
        end();
        miners();
        twilight();
    }

    private static void nether() {
        String mod = "nethers_delight_legacy";
        IngredientConfig.add(ingredient(mod, "hoglin_loin"), MEAT, 180, 300, 80, 1, .5F, 3, 12);
        IngredientConfig.add(ingredient(mod, "hoglin_ear"), MEAT, 120, 300, 80, 1, .5F, 1, 10);
        IngredientConfig.add(ingredient(mod, "strider_slice"), MEAT, 180, 300, 80, 1, .5F, 3, 10,
                new PotionEffect(MobEffects.FIRE_RESISTANCE, 400));
        IngredientConfig.add(ingredient(mod, "ground_strider"), MEAT, 120, 300, 80, 1, .5F, 2, 10,
                new PotionEffect(MobEffects.FIRE_RESISTANCE, 300));
        IngredientConfig.add(ingredient(mod, "propelpearl"), VEG, 0, 300, 80, 0, 0, 1, 6);
        cook(mod, "hoglin_loin", "hoglin_sirloin");
    }

    private static void end() {
        String mod = "ends_delight";
        IngredientConfig.add(ingredient(mod, "dragon_leg", "raw_dragon_meat"), MEAT, 240, 360, 80, 1, .5F, 3, 12);
        IngredientConfig.add(ingredient(mod, "raw_dragon_meat_cuts"), MEAT, 180, 300, 80, 1, .5F, 2, 12);
        IngredientConfig.add(ingredient(mod, "liquid_dragon_egg"), MEAT, 180, 240, 80, 1, .5F, 2, 15);
        IngredientConfig.add(ingredient(mod, "shulker_meat"), MEAT, 180, 240, 80, 1, .5F, 2, 8,
                new PotionEffect(MobEffects.LEVITATION, 80));
        IngredientConfig.add(ingredient(mod, "shulker_meat_slice"), MEAT, 120, 180, 80, 1, .5F, 1, 8,
                new PotionEffect(MobEffects.LEVITATION, 40));
        IngredientConfig.add(ingredient(mod, "raw_ender_mite_meat"), MEAT, 180, 300, 80, 1, .5F, 1, 8);
        IngredientConfig.add(ingredient(mod, "chorus_succulent"), VEG, 0, 120, 60, .3F, .3F, 1, 3);
        IngredientConfig.add(ingredient(mod, "chorus_fruit_grain"), VEG, 0, 120, 60, .3F, .3F, 1, 2);
        cook(mod, "raw_dragon_meat_cuts", "roasted_dragon_meat_cuts");
        cook(mod, "raw_dragon_meat", "roasted_dragon_meat");
        cook(mod, "dragon_leg", "smoked_dragon_leg");
        cook(mod, "raw_ender_mite_meat", "dried_endermite_meat");
        cook(mod, "shulker_meat", "roasted_shulker_meat");
        cook(mod, "shulker_meat_slice", "roasted_shulker_meat_slice");
        cook(mod, "liquid_dragon_egg", "fried_dragon_egg");
    }

    private static void miners() {
        String mod = "miners_delight";
        IngredientConfig.add(ingredient(mod, "silverfish_eggs"), MEAT, 60, 180, 60, .8F, .3F, 1, 5);
        IngredientConfig.add(ingredient(mod, "bat_wing"), MEAT, 180, 300, 60, .8F, .3F, 1, 5);
        IngredientConfig.add(ingredient(mod, "arthropod"), MEAT, 60, 120, 60, .8F, .3F, 2, 5);
        IngredientConfig.add(ingredient(mod, "squid", "glow_squid"), SEAFOOD, 120, 180, 40, .3F, .5F, 3, 12);
        IngredientConfig.add(ingredient(mod, "tentacles"), SEAFOOD, 60, 120, 40, .3F, .5F, 2, 12);
        IngredientConfig.add(ingredient(mod, "cave_carrot"), VEG, 240, 360, 60, .2F, .2F, 1, 8);
        IngredientConfig.add(ingredient(mod, "moss"), VEG, 60, 120, 60, .2F, .2F, 1, 3);
        cook(mod, "bat_wing", "smoked_bat_wing");
        cook(mod, "arthropod", "cooked_arthropod");
        cook(mod, "squid", "baked_squid");
        cook(mod, "glow_squid", "baked_squid");
        cook(mod, "tentacles", "baked_tentacles");
        cook(mod, "cave_carrot", "baked_cave_carrot");
    }

    private static void twilight() {
        String forest = "twilightforest";
        String delight = "twilightdelight";
        String spark = "twilight_spark_delight";
        IngredientConfig.add(ingredient(forest, "raw_meef", "raw_venison"), MEAT, 180, 240, 80, .5F, .5F, 3, 10);
        IngredientConfig.add(ingredient(forest, "torchberries"), VEG, 0, 80, 60, 0, .3F, 1, 4);
        cook(forest, "raw_meef", "cooked_meef");
        cook(forest, "raw_venison", "cooked_venison");

        IngredientConfig.add(ingredient(delight, "raw_yeti_meat", "raw_naga_meat", "raw_tomahawk_smeak"), MEAT, 180, 240, 80, .5F, .5F, 3, 10);
        IngredientConfig.add(ingredient(delight, "raw_meef_slice", "raw_venison_rib", "raw_yeti_meat_slice", "raw_naga_piece", "raw_insect"),
                MEAT, 120, 240, 80, .5F, .5F, 2, 10);
        cook(delight, "raw_meef_slice", "cooked_meef_slice");
        cook(delight, "raw_venison_rib", "cooked_venison_rib");
        cook(delight, "raw_yeti_meat", "cooked_yeti_meat");
        cook(delight, "raw_yeti_meat_slice", "cooked_yeti_meat_slice");
        cook(delight, "raw_naga_meat", "cooked_naga_meat");
        cook(delight, "raw_naga_piece", "cooked_naga_piece");
        cook(delight, "raw_insect", "cooked_insect");
        cook(delight, "raw_tomahawk_smeak", "cooked_tomahawk_smeak");

        IngredientConfig.add(ingredient(spark, "raw_bighorn_mutton", "raw_wild_boar_meat"), MEAT, 180, 240, 80, .5F, .5F, 3, 10);
        IngredientConfig.add(ingredient(spark, "raw_bighorn_mutton_chop", "raw_wild_boar_meat_cubes", "fire_beetle_leg"),
                MEAT, 120, 240, 80, .5F, .5F, 2, 10);
        cook(spark, "raw_bighorn_mutton", "cooked_bighorn_mutton");
        cook(spark, "raw_bighorn_mutton_chop", "cooked_bighorn_mutton_chop");
        cook(spark, "raw_wild_boar_meat", "cooked_wild_boar_meat");
        cook(spark, "raw_wild_boar_meat_cubes", "cooked_wild_boar_meat_cubes");
        cook(spark, "fire_beetle_leg", "cooked_fire_beetle_leg");
    }

    public static Ingredient mushrooms() {
        return ore("listAllmushroom", new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(Blocks.RED_MUSHROOM),
                stack("twilightforest:twilight_plant", 4), stack("twilight_spark_delight:labyrinth_mushroom"));
    }

    public static final class MushroomIngredient implements IIngredientFactory {
        @Override
        public Ingredient parse(JsonContext context, JsonObject json) {
            return mushrooms();
        }
    }
}

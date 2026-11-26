package xy177.cuisinedelightlegacy.compat;

import static xy177.cuisinedelightlegacy.content.logic.FoodType.*;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig;
import static xy177.cuisinedelightlegacy.init.data.CDConfigGen.cook;
import static xy177.cuisinedelightlegacy.init.data.CDConfigGen.ingredient;

public final class FoodAlex {
    private FoodAlex() {
    }

    public static void add() {
        String mobs = "alexsmobs";
        String delight = "alexsmobsdelightlegacy";
        IngredientConfig.add(ingredient(mobs, "moose_ribs", "kangaroo_meat"), MEAT, 240, 300, 60, .3F, .5F, 3, 10);
        IngredientConfig.add(ingredient(mobs, "blobfish", "lobster_tail", "raw_catfish", "cosmic_cod"), SEAFOOD, 180, 240, 40, .3F, .5F, 3, 12);
        IngredientConfig.add(ingredient(mobs, "gongylidia"), VEG, 60, 240, 60, .3F, .3F, 1, 5);
        IngredientConfig.add(ingredient(mobs, "rainbow_jelly"), NONE, 60, 240, 60, .3F, .3F, 1, 5);
        cook(mobs, "raw_catfish", "cooked_catfish");
        cook(mobs, "lobster_tail", "cooked_lobster_tail");
        cook(mobs, "kangaroo_meat", "cooked_kangaroo_meat");
        cook(mobs, "moose_ribs", "cooked_moose_ribs");

        IngredientConfig.add(ingredient(delight, "raw_bison_meat"), MEAT, 240, 300, 60, .3F, .5F, 3, 10);
        IngredientConfig.add(ingredient(delight, "raw_moose_rib_piece", "raw_kangaroo_meat_slice", "raw_bison_meat_cubes"),
                MEAT, 180, 240, 60, .3F, .5F, 2, 10);
        IngredientConfig.add(ingredient(mobs, "centipede_leg"), MEAT, 180, 240, 60, .3F, .5F, 2, 10);
        IngredientConfig.add(ingredient(delight, "raw_flying_fish"), SEAFOOD, 180, 240, 40, .3F, .5F, 3, 12);
        IngredientConfig.add(ingredient(delight, "raw_catfish_slice", "raw_flying_fish_slice"), SEAFOOD, 60, 180, 40, .5F, .5F, 2, 12);
        cook(delight, "raw_bison_meat", "cooked_bison_meat");
        cook(delight, "raw_bison_meat_cubes", "cooked_bison_meat_cubes");
        cook(delight, "raw_moose_rib_piece", "cooked_moose_rib_piece");
        cook(delight, "raw_kangaroo_meat_slice", "cooked_kangaroo_meat_slice");
        cook(delight, "raw_catfish_slice", "cooked_catfish_slice");
        cook(delight, "raw_flying_fish", "cooked_flying_fish");
        cook(delight, "raw_flying_fish_slice", "cooked_flying_fish_slice");
    }
}

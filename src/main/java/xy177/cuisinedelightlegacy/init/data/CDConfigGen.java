package xy177.cuisinedelightlegacy.init.data;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreIngredient;
import xy177.cuisinedelightlegacy.compat.FoodAlex;
import xy177.cuisinedelightlegacy.compat.FoodDelight;
import xy177.cuisinedelightlegacy.content.logic.CookTransformConfig;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig;
import xy177.cuisinedelightlegacy.content.logic.transform.Stage;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;

import java.util.Arrays;

import static xy177.cuisinedelightlegacy.content.logic.FoodType.*;
import static xy177.cuisinedelightlegacy.content.logic.IngredientConfig.add;
import static xy177.cuisinedelightlegacy.content.logic.CookTransformConfig.item;

public final class CDConfigGen {
    private CDConfigGen() {
    }

    public static void init() {
        ItemStack cod = new ItemStack(Items.FISH, 1, 0);
        ItemStack salmon = new ItemStack(Items.FISH, 1, 1);
        ItemStack goldApple = new ItemStack(Items.GOLDEN_APPLE, 1, 0);
        ItemStack enchantedApple = new ItemStack(Items.GOLDEN_APPLE, 1, 1);
        ItemStack kelp = first("farmers_future_delight:kelp", "oe:kelp");
        ItemStack driedKelp = first("farmers_future_delight:dried_kelp", "oe:dried_kelp");
        ItemStack sweetBerries = first("farmers_future_delight:sweet_berries", "futuremc:sweet_berries");
        ItemStack glowBerries = first("farmers_future_delight:glow_berries", "da:glow_berry", "depthsupdate:glow_berries");
        ItemStack honey = first("farmers_future_delight:honey_bottle", "futuremc:honey_bottle");
        Ingredient eggs = ore("listAllegg", new ItemStack(Items.EGG));

        add(Ingredient.fromItems(Items.MUTTON, Items.PORKCHOP, Items.BEEF), MEAT, 180, 240, 80, .5F, .5F, 3, 10);
        add(Ingredient.fromItems(Items.CHICKEN, Items.RABBIT), MEAT, 180, 240, 80, 1F, .3F, 2, 8);
        add(eggs, MEAT, 60, 240, 80, .2F, .2F, 1, 8);
        add(stacks(cod, salmon, new ItemStack(Items.FISH, 1, 2)), SEAFOOD, 60, 120, 40, .5F, .5F, 2, 12);
        add(stacks(new ItemStack(Items.FISH, 1, 3)), SEAFOOD, 60, 120, 40, .5F, .5F, 1, 5,
                new PotionEffect(MobEffects.NAUSEA, 100), new PotionEffect(MobEffects.HUNGER, 100), new PotionEffect(MobEffects.POISON, 100));
        add(Ingredient.fromItems(Items.POTATO), CARB, 180, 360, 60, .5F, .3F, 1, 8);
        add(Ingredient.fromItems(Items.BEETROOT, Items.CARROT), VEG, 240, 360, 60, .2F, .2F, 1, 5);
        add(FoodDelight.mushrooms(), VEG, 60, 360, 60, .2F, .2F, 1, 4);
        add(stacks(kelp), VEG, 120, 180, 60, .3F, .3F, 1, 3);
        add(stacks(new ItemStack(Items.APPLE), new ItemStack(Items.MELON), sweetBerries, glowBerries,
                new ItemStack(Items.CHORUS_FRUIT)), VEG, 0, 80, 60, 0, .3F, 1, 4);
        add(Ingredient.fromItems(Items.GOLDEN_CARROT), VEG, 240, 360, 40, .5F, .2F, 1, 20);
        add(stacks(goldApple), VEG, 0, 80, 60, 0, .3F, 1, 10,
                new PotionEffect(MobEffects.REGENERATION, 100, 1), new PotionEffect(MobEffects.SATURATION, 8));
        add(stacks(enchantedApple), VEG, 0, 80, 60, 0, .3F, 1, 10, new PotionEffect(MobEffects.REGENERATION, 400, 1),
                new PotionEffect(MobEffects.RESISTANCE, 6000), new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000),
                new PotionEffect(MobEffects.SATURATION, 32));
        add(stacks(new ItemStack(Blocks.YELLOW_FLOWER)), NONE, 60, 80, 40, 0, 0, 0, 0,
                new PotionEffect(MobEffects.SATURATION, 7));
        add(stacks(new ItemStack(Items.SUGAR), honey), NONE, 0, 300, 80, 0, 0, 1, 1);
        item(new ItemStack(Items.MUTTON), new ItemStack(Items.COOKED_MUTTON), Stage.COOKED);
        item(new ItemStack(Items.BEEF), new ItemStack(Items.COOKED_BEEF), Stage.COOKED);
        item(new ItemStack(Items.PORKCHOP), new ItemStack(Items.COOKED_PORKCHOP), Stage.COOKED);
        item(new ItemStack(Items.CHICKEN), new ItemStack(Items.COOKED_CHICKEN), Stage.COOKED);
        item(new ItemStack(Items.RABBIT), new ItemStack(Items.COOKED_RABBIT), Stage.COOKED);
        item(cod, new ItemStack(Items.COOKED_FISH, 1, 0), Stage.COOKED);
        item(salmon, new ItemStack(Items.COOKED_FISH, 1, 1), Stage.COOKED);
        item(new ItemStack(Items.POTATO), new ItemStack(Items.BAKED_POTATO), Stage.COOKED);
        item(kelp, driedKelp, Stage.COOKED);
        item(eggs, new ItemStack(CDItems.FRIED_EGG), Stage.RAW);
        CookTransformConfig.fluid(honey, 0xFFFED32E);

        add(fd("rice", "raw_pasta", "wheat_dough"), CARB, 300, 360, 80, .7F, .5F, 2, 8);
        add(fd("tomato", "cabbage", "onion", "pumpkin_slice"), VEG, 0, 360, 60, 0, .2F, 2, 5);
        add(fd("cabbage_leaf"), VEG, 0, 240, 60, 0, .2F, 1, 5);
        add(fd("milk_bottle"), NONE, 0, 360, 60, 0, 0, 1, 1);
        add(fd("ham"), MEAT, 240, 360, 80, .5F, .5F, 3, 10);
        add(fd("minced_beef", "mutton_chops"), MEAT, 120, 240, 80, .5F, .5F, 2, 10);
        add(fd("bacon"), MEAT, 120, 240, 80, 1, .5F, 2, 10);
        add(fd("chicken_cuts"), MEAT, 120, 240, 80, 1, .5F, 1, 8);
        add(fd("cod_slice", "salmon_slice"), SEAFOOD, 60, 120, 40, .5F, .5F, 1, 12);
        cook("farmersdelight", "bacon", "cooked_bacon");
        cook("farmersdelight", "chicken_cuts", "cooked_chicken_cuts");
        cook("farmersdelight", "cod_slice", "cooked_cod_slice");
        cook("farmersdelight", "salmon_slice", "cooked_salmon_slice");
        cook("farmersdelight", "minced_beef", "beef_patty");
        cook("farmersdelight", "mutton_chops", "cooked_mutton_chops");
        cook("farmersdelight", "ham", "smoked_ham");
        CookTransformConfig.fluid(stack("farmersdelight:milk_bottle"), 0xFFFFFFFF);

        FoodDelight.add();
        FoodAlex.add();
    }

    public static void cook(String mod, String raw, String cooked) {
        item(stack(mod + ":" + raw), stack(mod + ":" + cooked), Stage.COOKED);
    }

    public static Ingredient ingredient(String mod, String... paths) {
        return stacks(Arrays.stream(paths).map(path -> stack(mod + ":" + path)).toArray(ItemStack[]::new));
    }

    private static Ingredient fd(String... paths) {
        return ingredient("farmersdelight", paths);
    }

    public static Ingredient ore(String name, ItemStack... knownStacks) {
        return new CompoundIngredient(Arrays.asList(stacks(knownStacks), new OreIngredient(name))) {};
    }

    private static Ingredient stacks(ItemStack... stacks) {
        return Ingredient.fromStacks(Arrays.stream(stacks).filter(stack -> !stack.isEmpty()).toArray(ItemStack[]::new));
    }

    public static ItemStack stack(String id) {
        return stack(id, 0);
    }

    public static ItemStack stack(String id, int metadata) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        return item == null ? ItemStack.EMPTY : new ItemStack(item, 1, metadata);
    }

    private static ItemStack first(String... ids) {
        for (String id : ids) {
            ItemStack result = stack(id);
            if (!result.isEmpty()) return result;
        }
        return ItemStack.EMPTY;
    }
}

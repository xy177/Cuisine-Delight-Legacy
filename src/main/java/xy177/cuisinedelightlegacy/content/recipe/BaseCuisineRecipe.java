package xy177.cuisinedelightlegacy.content.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import xy177.cuisinedelightlegacy.content.item.BaseFoodItem;
import xy177.cuisinedelightlegacy.content.logic.CookedFoodData;
import xy177.cuisinedelightlegacy.init.registrate.PlateFood;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaseCuisineRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private static final Map<ResourceLocation, BaseCuisineRecipe> CUSTOM_RECIPES = new LinkedHashMap<>();

    public final List<CuisineRecipeMatch> list;
    public final double saturationBonus;
    public final double saturationBonusModifier;
    private final ItemStack result;

    protected BaseCuisineRecipe(ItemStack result, List<CuisineRecipeMatch> list, double bonus, double modifier) {
        this.result = result;
        this.list = list;
        saturationBonus = bonus;
        saturationBonusModifier = modifier;
    }

    public static List<BaseCuisineRecipe> getRecipes() {
        Map<ResourceLocation, BaseCuisineRecipe> recipes = new LinkedHashMap<>();
        ForgeRegistries.RECIPES.getValuesCollection().stream().filter(BaseCuisineRecipe.class::isInstance)
                .map(BaseCuisineRecipe.class::cast).sorted(Comparator.comparing(recipe -> recipe.getRegistryName().toString()))
                .forEach(recipe -> recipes.put(recipe.getRegistryName(), recipe));
        recipes.putAll(CUSTOM_RECIPES);
        return new ArrayList<>(recipes.values());
    }

    public static void addRecipe(ResourceLocation id, BaseCuisineRecipe recipe, boolean override) {
        IRecipe existing = ForgeRegistries.RECIPES.getValue(id);
        if (existing != null && !(existing instanceof BaseCuisineRecipe)) {
            throw new IllegalArgumentException("Cannot replace non-cuisine recipe " + id);
        }
        if (!override && (existing != null || CUSTOM_RECIPES.containsKey(id))) {
            throw new IllegalArgumentException("Recipe " + id + " already exists. Set override to true to replace it.");
        }
        recipe.setRegistryName(id);
        CUSTOM_RECIPES.put(id, recipe);
    }

    public static ItemStack findBestMatch(CookedFoodData food) {
        CuisineRecipeContainer container = new CuisineRecipeContainer(food);
        BaseCuisineRecipe best = null;
        double bestScore = -1;
        for (BaseCuisineRecipe recipe : getRecipes()) {
            double score = recipe.getScore(container);
            if (score > bestScore) {
                bestScore = score;
                best = recipe;
            }
        }
        ItemStack stack = best == null ? new ItemStack(PlateFood.SUSPICIOUS_MIX.item) : best.result.copy();
        if (best != null) {
            double offset = best.list.stream().mapToDouble(match -> match.score).sum();
            food.nutrition *= 1 + best.saturationBonusModifier * (bestScore - offset);
        }
        BaseFoodItem.setData(stack, food);
        return stack;
    }

    public double getScore(CuisineRecipeContainer container) {
        double score = 0;
        for (CuisineRecipeMatch match : list) {
            double part = match.reduce(container.list);
            if (part == 0) return -1;
            score += part;
        }
        return score;
    }

    public ItemStack displayStack() {
        ItemStack stack = result.copy();
        NBTTagCompound display = new NBTTagCompound();
        display.setDouble("Min", saturationBonus);
        display.setDouble("Max", saturationBonus + saturationBonusModifier * list.stream().mapToDouble(match -> match.bonus).sum());
        stack.setTagInfo("Display", display);
        return stack;
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return result.copy();
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}

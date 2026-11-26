package xy177.cuisinedelightlegacy.compat;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xy177.cuisinedelightlegacy.content.recipe.BaseCuisineRecipe;
import xy177.cuisinedelightlegacy.content.recipe.CuisineRecipeMatch;
import xy177.cuisinedelightlegacy.content.recipe.FoodTypeIngredient;
import xy177.cuisinedelightlegacy.init.data.LangData;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.NAME;
import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.loc;

public final class CuisineRecipeCategory implements IRecipeCategory<CuisineRecipeCategory.Recipe> {
    public static final String UID = loc("cuisine").toString();
    private final IDrawable background, icon, slot, arrow;

    public CuisineRecipeCategory(IGuiHelper gui) {
        background = gui.createBlankDrawable(116, 54);
        icon = gui.createDrawableIngredient(new ItemStack(CDItems.SKILLET));
        slot = gui.getSlotDrawable();
        arrow = gui.createDrawable(new ResourceLocation("jei", "textures/gui/gui_vanilla.png"), 82, 128, 24, 17);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return LangData.JEI_TITLE.get().getUnformattedText();
    }

    @Override
    public String getModName() {
        return NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void drawExtras(Minecraft mc) {
        for (int index = 0; index < 9; index++) slot.draw(mc, index % 3 * 18, index / 3 * 18);
        slot.draw(mc, 94, 18);
        arrow.draw(mc, 61, 18);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, Recipe wrapper, IIngredients ingredients) {
        IGuiItemStackGroup items = layout.getItemStacks();
        List<CuisineRecipeMatch> matches = wrapper.recipe.list;
        for (int index = 0; index < matches.size(); index++) {
            items.init(index, true, index % 3 * 18, index / 3 * 18);
        }
        items.init(matches.size(), false, 94, 18);
        items.set(ingredients);
        items.addTooltipCallback((index, input, stack, tooltip) -> {
            if (input) ingredientTooltip(matches.get(index), tooltip);
        });
    }

    private static void ingredientTooltip(CuisineRecipeMatch match, List<String> tooltip) {
        if (match.ingredient instanceof FoodTypeIngredient) {
            FoodTypeIngredient ingredient = (FoodTypeIngredient) match.ingredient;
            tooltip.add(LangData.JEI_FOOD_TYPE.get(ingredient.type.get()).getFormattedText());
        }
        int min = (int) Math.round(match.min * 100);
        int max = (int) Math.round(Math.min(match.max, 1) * 100);
        String amount = max >= 100 ? min + "%+" : min + "%-" + max + "%";
        tooltip.add(LangData.JEI_INGREDIENT_AMOUNT.get(amount).getFormattedText());
    }

    public static final class Recipe implements IRecipeWrapper {
        private final BaseCuisineRecipe recipe;

        public Recipe(BaseCuisineRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM, recipe.list.stream()
                    .map(match -> Arrays.asList(match.ingredient.getMatchingStacks())).collect(Collectors.toList()));
            ingredients.setOutput(VanillaTypes.ITEM, recipe.displayStack());
        }
    }
}

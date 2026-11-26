package xy177.cuisinedelightlegacy.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import xy177.cuisinedelightlegacy.content.recipe.BaseCuisineRecipe;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;

@JEIPlugin
public final class JEICompat implements IModPlugin {
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CuisineRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(BaseCuisineRecipe.class, CuisineRecipeCategory.Recipe::new, CuisineRecipeCategory.UID);
        registry.addRecipes(BaseCuisineRecipe.getRecipes(), CuisineRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(CDItems.SKILLET), CuisineRecipeCategory.UID);
    }
}

package xy177.cuisinedelightlegacy.init.data;

import com.wdcftgg.farmersdelightlegacy.api.recipe.CuttingBoardRecipeApi;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreIngredient;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;
import xy177.farmersfuturedelight.api.WorkstationRecipes;

import java.util.Collections;

import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.MOD_ID;
import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.loc;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class RecipeGen {
    private static final ResourceLocation STONECUTTER = new ResourceLocation("farmers_future_delight", "stonecutter");

    private RecipeGen() {
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        if (!ForgeRegistries.BLOCKS.containsKey(STONECUTTER)) {
            NonNullList<Ingredient> inputs = NonNullList.create();
            inputs.add(Ingredient.fromItems(Items.IRON_INGOT));
            inputs.add(Ingredient.fromItems(Items.STICK));
            event.getRegistry().register(new ShapelessRecipes("", new ItemStack(CDItems.SPATULA), inputs)
                    .setRegistryName(loc("spatula")));
        }
    }

    public static void init() {
        if (ForgeRegistries.BLOCKS.containsKey(STONECUTTER)) {
            Stonecutting.register();
        } else {
            CuttingBoardRecipeApi.registerRecipe(loc("plate").toString(), "ore:plankWood", "ore:toolAxe",
                    loc("plate").toString(), 16, 1.0F);
        }
    }

    private static final class Stonecutting {
        private static void register() {
            WorkstationRecipes.register(WorkstationRecipes.Type.STONECUTTING, loc("plate"),
                    Collections.singletonList(new OreIngredient("plankWood")), new ItemStack(CDItems.PLATE, 16), 1, 0);
            WorkstationRecipes.register(WorkstationRecipes.Type.STONECUTTING, loc("spatula"),
                    Collections.singletonList(Ingredient.fromItems(Items.IRON_INGOT)), new ItemStack(CDItems.SPATULA), 1, 0);
        }
    }
}

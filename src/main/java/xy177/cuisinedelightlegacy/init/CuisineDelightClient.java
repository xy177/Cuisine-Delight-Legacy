package xy177.cuisinedelightlegacy.init;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import xy177.cuisinedelightlegacy.content.block.CuisineSkilletBlockEntity;
import xy177.cuisinedelightlegacy.content.client.CuisineSkilletRenderer;
import xy177.cuisinedelightlegacy.content.client.SkilletBEWLR;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig;
import xy177.cuisinedelightlegacy.init.data.LangData;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;
import xy177.cuisinedelightlegacy.init.registrate.PlateFood;

import java.util.List;

import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.MOD_ID;
import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.loc;

@Mod.EventBusSubscriber(modid = MOD_ID, value = Side.CLIENT)
public final class CuisineDelightClient {
    private static final ModelResourceLocation SKILLET = new ModelResourceLocation(loc("cuisine_skillet"), "inventory");

    private CuisineDelightClient() {
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerModel(CDItems.SKILLET);
        registerModel(CDItems.SPATULA);
        registerModel(CDItems.PLATE);
        registerModel(CDItems.FRIED_EGG);
        for (PlateFood food : PlateFood.values()) registerModel(food.item);
        ClientRegistry.bindTileEntitySpecialRenderer(CuisineSkilletBlockEntity.class, new CuisineSkilletRenderer());
    }

    private static void registerModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public static void bakeModel(ModelBakeEvent event) {
        IBakedModel model = event.getModelRegistry().getObject(SKILLET);
        if (model == null) return;
        CDItems.SKILLET.setTileEntityItemStackRenderer(new SkilletBEWLR(model));
        event.getModelRegistry().putObject(SKILLET, new SkilletBEWLR.Model(model));
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        IngredientConfig.IngredientEntry entry = IngredientConfig.getEntry(event.getItemStack());
        if (entry == null) return;
        List<String> tooltip = event.getToolTip();
        if (!GuiScreen.isShiftKeyDown()) {
            tooltip.add(LangData.SHIFT.get().getFormattedText());
            return;
        }
        tooltip.add(TextFormatting.GOLD + entry.type.get().getUnformattedText());
        tooltip.add(LangData.INFO_SIZE.get(entry.size).getFormattedText());
        tooltip.add(LangData.INFO_NUTRITION.get(entry.nutrition).getFormattedText());
        tooltip.add(LangData.INFO_MIN_TIME.get(entry.minTime / 20).getFormattedText());
        tooltip.add(LangData.INFO_MAX_TIME.get(entry.maxTime / 20).getFormattedText());
        tooltip.add(LangData.INFO_STIR_TIME.get(entry.stirTime / 20).getFormattedText());
        tooltip.add(LangData.INFO_RAW_PENALTY.get(Math.round(entry.rawPenalty * 100)).getFormattedText());
        tooltip.add(LangData.INFO_OVERCOOK_PENALTY.get(Math.round(entry.overcookPenalty * 100)).getFormattedText());
        for (PotionEffect effect : entry.effects) {
            Potion potion = effect.getPotion();
            String name = I18n.format(potion.getName());
            if (effect.getDuration() > 20) name = I18n.format("potion.withDuration", name, Potion.getPotionDurationString(effect, 1));
            tooltip.add((potion.isBadEffect() ? TextFormatting.RED : TextFormatting.BLUE) + name);
        }
    }
}

package xy177.cuisinedelightlegacy.content.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import xy177.cuisinedelightlegacy.content.block.CuisineSkilletBlockEntity;
import xy177.cuisinedelightlegacy.content.item.CuisineSkilletItem;
import xy177.cuisinedelightlegacy.content.logic.CookTransformConfig;
import xy177.cuisinedelightlegacy.content.logic.CookingData;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig;
import xy177.cuisinedelightlegacy.init.data.CDConfig;
import xy177.cuisinedelightlegacy.init.registrate.CDItems;

import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.MOD_ID;
import static xy177.cuisinedelightlegacy.content.client.PieRenderer.Texture.*;

@Mod.EventBusSubscriber(modid = MOD_ID, value = Side.CLIENT)
public final class CookingOverlay {
    private static final float MAX_TIME = 400;
    private static final float STIR_TIME = 100;

    private CookingOverlay() {
    }

    @SubscribeEvent
    public static void render(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || CDConfig.uiScale <= 0) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null || mc.gameSettings.hideGUI) return;
        CookingData data = getData(mc);
        if (data == null || data.contents.isEmpty()) return;
        data.update(mc.world.getTotalWorldTime());

        ScaledResolution resolution = event.getResolution();
        float scale = CDConfig.uiScale * resolution.getScaledHeight() / 240;
        int width = (int) (resolution.getScaledWidth() / scale);
        int height = (int) (resolution.getScaledHeight() / scale);
        int x = CDConfig.uiXOffset + (CDConfig.uiXAnchor + 1) * (width - 68) / 2;
        int y = CDConfig.uiYOffset + (CDConfig.uiYAnchor + 1) * (height - data.contents.size() * 20) / 2;
        RenderItem renderer = mc.getRenderItem();
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        for (CookingData.CookingEntry entry : data.contents) {
            ItemStack item = entry.getItem();
            ItemStack display = CookTransformConfig.renderStack(entry.getStage(data), item);
            RenderHelper.enableGUIStandardItemLighting();
            renderer.renderItemAndEffectIntoGUI(display, x, y + 2);
            renderer.renderItemOverlays(mc.fontRenderer, display, x, y + 2);
            RenderHelper.disableStandardItemLighting();
            IngredientConfig.IngredientEntry ingredient = IngredientConfig.getEntry(item);
            if (ingredient != null) drawGauges(data, entry, ingredient, x, y, event.getPartialTicks());
            y += 20;
        }
        GlStateManager.popMatrix();
    }

    private static CookingData getData(Minecraft mc) {
        EntityPlayer player = mc.player;
        ItemStack held = player.getHeldItemMainhand();
        if (held.getItem() != CDItems.SKILLET) held = player.getHeldItemOffhand();
        if (held.getItem() == CDItems.SKILLET) {
            CookingData data = CuisineSkilletItem.getData(held);
            if (data != null && !data.contents.isEmpty()) return data;
        }
        RayTraceResult hit = mc.objectMouseOver;
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) return null;
        TileEntity tile = mc.world.getTileEntity(hit.getBlockPos());
        return tile instanceof CuisineSkilletBlockEntity ? ((CuisineSkilletBlockEntity) tile).cookingData : null;
    }

    private static void drawGauges(CookingData data, CookingData.CookingEntry entry,
                                   IngredientConfig.IngredientEntry ingredient, int x, int y, float partialTicks) {
        PieRenderer cook = new PieRenderer(x + 28, y + 12);
        float min = ingredient.minTime / MAX_TIME;
        float max = ingredient.maxTime / MAX_TIME;
        cook.fillPie(0, min, PIE_GREEN);
        cook.fillPie(min, max, PIE_YELLOW);
        cook.fillPie(max, 1, PIE_RED);
        cook.drawNeedle(NEEDLE_BLACK, MathHelper.clamp(entry.getDuration(data, partialTicks) / MAX_TIME, 0, 1));
        cook.drawIcon(COOK);

        PieRenderer stir = new PieRenderer(x + 48, y + 12);
        float limit = ingredient.stirTime / STIR_TIME;
        stir.fillPie(0, limit, PIE_GREEN);
        stir.fillPie(limit, 1, PIE_RED);
        float current = MathHelper.clamp(entry.timeSinceStir(data, partialTicks) / STIR_TIME, 0, 1);
        float longest = MathHelper.clamp(entry.getMaxStirTime(data) / STIR_TIME, current, 1);
        stir.drawNeedle(NEEDLE_BLACK, current);
        stir.drawNeedle(NEEDLE_RED, longest + 0.5F);
        stir.drawIcon(FLIP);
    }
}

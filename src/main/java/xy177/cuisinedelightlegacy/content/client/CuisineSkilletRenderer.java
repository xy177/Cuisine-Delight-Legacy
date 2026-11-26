package xy177.cuisinedelightlegacy.content.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import xy177.cuisinedelightlegacy.content.block.CuisineSkilletBlockEntity;
import xy177.cuisinedelightlegacy.content.logic.CookTransformConfig;
import xy177.cuisinedelightlegacy.content.logic.CookingData;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig;
import xy177.cuisinedelightlegacy.content.logic.transform.Stage;

public final class CuisineSkilletRenderer extends TileEntitySpecialRenderer<CuisineSkilletBlockEntity> {
    @Override
    public void render(CuisineSkilletBlockEntity skillet, double x, double y, double z, float partialTicks,
                       int destroyStage, float alpha) {
        if (!skillet.isCooking() || destroyStage >= 0) return;
        skillet.cookingData.update(skillet.getWorld().getTotalWorldTime());
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.095, z + 0.5);
        renderItem(skillet.cookingData, skillet.getStirPercent(partialTicks));
        GlStateManager.popMatrix();
    }

    static void renderItem(CookingData data, float stir) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderItem renderer = mc.getRenderItem();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableRescaleNormal();
        float lift = stir * (1 - stir) * 2;
        int index = 0;
        int red = 0, green = 0, blue = 0, liquids = 0;
        for (CookingData.CookingEntry entry : data.contents) {
            ItemStack stack = entry.getItem();
            CookTransformConfig.CookTransform transform = CookTransformConfig.get(stack);
            if (transform != null && transform.color != 0) {
                red += (transform.color >> 16) & 255;
                green += (transform.color >> 8) & 255;
                blue += transform.color & 255;
                liquids++;
                continue;
            }
            Stage stage = entry.getStage(data);
            ItemStack display = transform == null ? stack : transform.renderStack(stage, stack);
            float lightX = OpenGlHelper.lastBrightnessX;
            float lightY = OpenGlHelper.lastBrightnessY;
            IngredientConfig.IngredientEntry config = IngredientConfig.getEntry(stack);
            boolean burnt = config != null && Math.max(entry.getMaxStirTime(data), entry.timeSinceStir(data, 0)) > config.stirTime;
            float light = burnt ? 0.4F : stage == Stage.OVERCOOKED ? 0.7F : 1;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX * light, lightY * light);
            double angle = index * Math.PI * (3 - Math.sqrt(5)) + stir * Math.PI * 2;
            double radius = Math.min(0.12, 0.07 * Math.sqrt(index));
            GlStateManager.pushMatrix();
            GlStateManager.translate(radius * Math.cos(angle), 0.018 + index * 0.002 + lift, radius * Math.sin(angle));
            GlStateManager.rotate((entry.seed() % 360) + stir * 360, 0, 1, 0);
            GlStateManager.rotate(stir * 360, 0, 0, 1);
            GlStateManager.rotate(90, 1, 0, 0);
            GlStateManager.scale(0.45, 0.45, 0.45);
            renderer.renderItem(display, renderer.getItemModelWithOverrides(display, mc.world, null));
            GlStateManager.popMatrix();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
            index++;
        }
        if (liquids > 0) renderFluid(mc, red / liquids, green / liquids, blue / liquids);
        GlStateManager.disableRescaleNormal();
    }

    private static void renderFluid(Minecraft mc, int red, int green, int blue) {
        TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/water_still");
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(-0.33, 0.01, -0.33).tex(sprite.getMinU(), sprite.getMinV()).color(red, green, blue, 190).endVertex();
        buffer.pos(-0.33, 0.01, 0.33).tex(sprite.getMinU(), sprite.getMaxV()).color(red, green, blue, 190).endVertex();
        buffer.pos(0.33, 0.01, 0.33).tex(sprite.getMaxU(), sprite.getMaxV()).color(red, green, blue, 190).endVertex();
        buffer.pos(0.33, 0.01, -0.33).tex(sprite.getMaxU(), sprite.getMinV()).color(red, green, blue, 190).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
    }
}

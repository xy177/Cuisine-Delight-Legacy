package xy177.cuisinedelightlegacy.content.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Locale;

import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.loc;

final class PieRenderer {
    private final int x;
    private final int y;

    PieRenderer(int x, int y) {
        this.x = x;
        this.y = y;
    }

    enum Texture {
        PIE_GREEN, PIE_YELLOW, PIE_RED, NEEDLE_RED, NEEDLE_BLACK, COOK, FLIP;

        private final ResourceLocation id = loc("textures/gui/overlay/" + name().toLowerCase(Locale.ROOT) + ".png");

        void bind() {
            Minecraft.getMinecraft().getTextureManager().bindTexture(id);
            GlStateManager.color(1, 1, 1, 1);
        }
    }

    void fillPie(float start, float end, Texture texture) {
        if (start >= end) return;
        start += 0.75F;
        end += 0.75F;
        texture.bind();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y, -1).tex(0.5, 0.5).endVertex();
        for (int step = MathHelper.ceil(end * 4 + 0.5F); step >= MathHelper.floor(start * 4 + 0.5F); step--) {
            double theta = MathHelper.clamp(step * 0.25F - 0.125F, start, end) * Math.PI * 2;
            double u = Math.cos(theta);
            double v = Math.sin(theta);
            double radius = 0.5 / Math.max(Math.abs(u), Math.abs(v));
            buffer.pos(x + u * radius * 16, y + v * radius * 16, -1).tex(0.5 + u * radius, 0.5 + v * radius).endVertex();
        }
        tessellator.draw();
    }

    void drawNeedle(Texture texture, float position) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.rotate(position * 360, 0, 0, 1);
        drawTexture(texture, -8, -8);
        GlStateManager.popMatrix();
    }

    void drawIcon(Texture texture) {
        drawTexture(texture, x - 8, y - 8);
    }

    private void drawTexture(Texture texture, int left, int top) {
        texture.bind();
        Gui.drawModalRectWithCustomSizedTexture(left, top, 0, 0, 16, 16, 16, 16);
    }
}

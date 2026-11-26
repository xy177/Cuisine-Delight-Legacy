package xy177.cuisinedelightlegacy.content.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.apache.commons.lang3.tuple.Pair;
import xy177.cuisinedelightlegacy.content.item.CuisineSkilletItem;
import xy177.cuisinedelightlegacy.content.logic.CookingData;

import javax.vecmath.Matrix4f;

public final class SkilletBEWLR extends TileEntityItemStackRenderer {
    private final IBakedModel model;

    public SkilletBEWLR(IBakedModel model) {
        this.model = model;
    }

    @Override
    public void renderByItem(ItemStack stack) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);
        mc.getRenderItem().renderItem(stack, model);
        CookingData data = CuisineSkilletItem.getData(stack);
        if (mc.world != null && data != null && !data.contents.isEmpty()) {
            data.update(mc.world.getTotalWorldTime());
            float stir = 0;
            if (mc.player != null && (mc.player.getHeldItemMainhand() == stack || mc.player.getHeldItemOffhand() == stack)) {
                stir = mc.player.getCooldownTracker().getCooldown(stack.getItem(), mc.getRenderPartialTicks());
            }
            GlStateManager.translate(0, -0.405, 0);
            CuisineSkilletRenderer.renderItem(data, stir);
        }
        GlStateManager.popMatrix();
    }

    public static final class Model extends BakedModelWrapper<IBakedModel> {
        public Model(IBakedModel original) {
            super(original);
        }

        @Override
        public boolean isBuiltInRenderer() {
            return true;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
            return Pair.of(this, originalModel.handlePerspective(type).getRight());
        }
    }
}

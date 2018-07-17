package zed.mopm.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GuiUtils {

    private GuiUtils() {}

    public static void drawVerticalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor, float zLevel)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)top, (double)right, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)top, (double)left, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)bottom, (double)left, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)bottom, (double)right, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor, int zLevel) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawTexturedRect(double left, double top, double right, double bottom, double z, int r, int g, int b, int a, int tint, ResourceLocation rl, Minecraft mc)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        mc.getTextureManager().bindTexture(rl);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder
                .pos(left, bottom, z)
                .tex(0.0D, (bottom / 128.0F + (float)tint))
                .color(r, g, b, a)
                .endVertex();
        bufferbuilder
                .pos(right, bottom, z)
                .tex(right / 32.0F, (bottom / 128.0F + (float)tint))
                .color(r, g, b, a)
                .endVertex();
        bufferbuilder
                .pos(right, top, z)
                .tex(right / 32.0F, (double)tint)
                .color(r, g, b, a)
                .endVertex();
        bufferbuilder
                .pos(left, top, z)
                .tex(0.0D, (double)tint)
                .color(r, g, b, a)
                .endVertex();
        tessellator.draw();
    }

    public static void drawToolTip(FontRenderer renderer, List<String> textLines, int x, int y, int width, int height) {

        x += 5;
        width += 10;
        int zLevel = 50;
        int baseColor = -267386864;
        int boarderColorStart = 1347420415;
        int boarderColorEnd = 1344798847;

        drawGradientRect(x, y, x + width, y + height, baseColor, baseColor, zLevel); //Horizontal Base
        drawGradientRect(x + 1, y - 1, x + width - 1, y + height + 1, -267386864, -267386864, zLevel); // Vertical base

        drawGradientRect(x + 1, y, x + width - 1, y + 1, boarderColorStart, boarderColorEnd, zLevel); // purple boarder top
        drawGradientRect(x + 1, y + height - 1, x + width - 1, y + height, boarderColorEnd, boarderColorStart, zLevel); // purple boarder bottom
        drawGradientRect(x + width - 2, y + 1, x + width - 1, y + height - 1, boarderColorStart, boarderColorEnd, zLevel); // purple boarder right
        drawGradientRect(x + 1, y + 1, x + 2, y + height - 1, boarderColorEnd, boarderColorStart, zLevel);// purple boarder left

        for (int i = 0; i < textLines.size(); i++) {
            renderer.drawStringWithShadow(textLines.get(i), (float)(x + 5.0), (float)(y + (height / 4.0) + (i * 5)), -1);
        }
    }

    public static void drawToolTip(FontRenderer renderer, String text, int x, int y, int width, int height) {
        List<String> toolTip = new ArrayList<>();
        toolTip.add(text);
        drawToolTip(renderer, toolTip, x, y, width, height);
    }
}

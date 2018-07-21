package zed.mopm.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector4d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class GuiUtils {

    private GuiUtils() { }

    public static void drawVerticalGradientRect(final int left, final int top, final int right, final int bottom, final int startColor, final int endColor, final float zLevel) {
        final float f = (float) (startColor >> 24 & 255) / 255.0F;
        final float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        final float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        final float f3 = (float) (startColor & 255) / 255.0F;
        final float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        final float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        final float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        final float f7 = (float) (endColor & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) top, (double) right, (double) zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) top, (double) left, (double) zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) bottom, (double) left, (double) zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double) bottom, (double) right, (double) zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawGradientRect(final int left, final int top, final int right, final int bottom, final int startColor, final int endColor, final int zLevel) {
    	  final float f = (float) (startColor >> 24 & 255) / 255.0F;
    	  final float f1 = (float) (startColor >> 16 & 255) / 255.0F;
    	  final float f2 = (float) (startColor >> 8 & 255) / 255.0F;
    	  final float f3 = (float) (startColor & 255) / 255.0F;
    	  final float f4 = (float) (endColor >> 24 & 255) / 255.0F;
    	  final float f5 = (float) (endColor >> 16 & 255) / 255.0F;
    	  final float f6 = (float) (endColor >> 8 & 255) / 255.0F;
    	  final float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) right, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) left, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) left, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double) right, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawTexturedRect(final double left, final double top, final double right, final double bottom, final double z, final int r, final int g, final int b, final int a, final int tint, final ResourceLocation rl, final Minecraft mc) {
    {
        List<Vector4d> datums = Arrays.asList(
                new Vector4d(left,bottom,0.0D,bottom/128.0F),
                new Vector4d(right,bottom,right/32.0F,bottom/128.0F),
                new Vector4d(right,top,right/32.0F,0.0D),
                new Vector4d(left,top,0.0D,0.0D));
      
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        mc.getTextureManager().bindTexture(rl);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        datums.stream().forEach( datum -> bufferbuilder.pos(datum.getX(), datum.getY(), z)
                .tex(datum.getZ(), tint+datum.getW())
                .color(r, g, b, a)
                .endVertex() );

        tessellator.draw();
    }

    public static void drawToolTip(final FontRenderer renderer, final List<String> textLines, int x, final int y, int width, final int height) {

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
        drawGradientRect(x + 1, y + 1, x + 2, y + height - 1, boarderColorEnd, boarderColorStart, zLevel); // purple boarder left

        for (int i = 0; i < textLines.size(); i++) {
            renderer.drawStringWithShadow(textLines.get(i), (float) (x + 5.0), (float) (y + (height / 4.0) + (i * 5)), -1);
        }
    }

    public static void drawToolTip(final FontRenderer renderer, final String text, final int x, final int y, final int width, final int height) {
        List<String> toolTip = new ArrayList<>();
        toolTip.add(text);
        drawToolTip(renderer, toolTip, x, y, width, height);
    }
}

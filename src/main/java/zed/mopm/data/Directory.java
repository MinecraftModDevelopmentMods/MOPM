package zed.mopm.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import zed.mopm.util.ColorUtils;
import zed.mopm.util.References;

public class Directory implements GuiListExtended.IGuiListEntry {
    protected String dirName;
    protected String uniqueDirName;

    private static final ResourceLocation ICON_TRASH = new ResourceLocation(References.MODID,"textures/gui/trash_5.png");
    private int width;
    private int x;
    private int y;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public Directory(String name, String uuid) {
        this.dirName = name;
        this.uniqueDirName = uuid;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Interfaces:--------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: GuiListExtended.IGuiListEntry
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
        //:: Does not have any functional use.
        // - This might be used to re organize folders in the future.
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        this.width = listWidth;
        this.x = x;
        this.y = y;
        int color = ColorUtils.getARGB(255, 255, 255, 255);
        FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
        String display = dirName;

        if (renderer.getStringWidth(display) > listWidth) {
            display = renderer.listFormattedStringToWidth(dirName, listWidth).get(0);
            display = display.substring(0, display.length() - 4) + ". . .";
        }
        renderer.drawString(display, x + 5, y + 5, color);
        int j = mouseX - x;
        int i = j < 16 ? 16 : 0;

        if (isSelected) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(ICON_TRASH);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawModalRectWithCustomSizedTexture(listWidth - 20, y, 32.0F, (float)i, 16, 16, 16F, 16F);
        }
    }

    @Override
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        return mouseX >= this.width - 20 && mouseX <= this.width;
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        //:: Not in use
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public String dirUUID() {
        return this.uniqueDirName;
    }

    public String dirName() {
        return this.dirName;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
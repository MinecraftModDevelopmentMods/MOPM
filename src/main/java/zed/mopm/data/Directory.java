package zed.mopm.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListExtended;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.util.ColorUtils;

public class Directory implements GuiListExtended.IGuiListEntry/*, IFolderPath*/ {
    private String dirName;
    private String uniqueDirName;
    private String pathToDir;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public Directory(String name, String UUID) {
        this.dirName = name;
        this.uniqueDirName = UUID;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Interfaces:--------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: GuiListExtended.IGuiListEntry
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        int color = ColorUtils.getARGB(255, 255, 255, 255);
        FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
        String display = dirName;

        if (renderer.getStringWidth(display) > listWidth) {
            display = renderer.listFormattedStringToWidth(dirName, listWidth).get(0);
            display = display.substring(0, display.length() - 4) + ". . .";
        }
        renderer.drawString(display, x + 5, y, color);
    }

    @Override
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        return false;
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

    }

    //:: IFolderPath
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /*@Override
    public void setPath(String path) {
        this.pathToDir = path;
    }

    @Override
    public String getPathToDir() {
        return this.pathToDir;
    }*/

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public String dirUUID() {
        return this.uniqueDirName;
    }

    public String dirName() {
        return this.dirName;
    }
}
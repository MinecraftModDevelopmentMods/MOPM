package zed.mopm.gui.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.client.gui.GuiWorldSelection;
import zed.mopm.gui.SinglePlayerMenu;

import java.util.ArrayList;
import java.util.List;

public class WorldList extends GuiListWorldSelection {
    private List<GuiListWorldSelectionEntry> relevantWorlds;
    private SinglePlayerMenu worldMenu;
    private int i = 0;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public WorldList(GuiWorldSelection worldSelection, Minecraft clientIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(worldSelection, clientIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        worldMenu = (SinglePlayerMenu) worldSelection;
        relevantWorlds = new ArrayList<>();
    }

    public WorldList(GuiWorldSelection worldSelection, Minecraft clientIn, int slotHeightIn) {
        super(worldSelection, clientIn, 0, 0, 0, 0, slotHeightIn);
        relevantWorlds = new ArrayList<>();
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void refreshList() {
        super.refreshList();
    }

    @Override
    public GuiListWorldSelectionEntry getListEntry(int index) {
        return this.relevantWorlds.get(index);
    }

    @Override
    protected int getSize() {
        return this.relevantWorlds.size();
    }


    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public GuiListWorldSelectionEntry getFullListEntry(int index) {
        return super.getListEntry(index);
    }

    protected int getFullSize() {
        return super.getSize();
    }

    public void remove() {
        i++;
        if (i > this.getFullSize() - 1) {
            i = 0;
        }
    }

    public void displayWorlds(List<GuiListWorldSelectionEntry> worlds) {
        relevantWorlds.clear();
        relevantWorlds.addAll(worlds);
    }
}

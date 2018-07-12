package zed.mopm.gui.lists;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.SinglePlayerMenu;
import zed.mopm.util.References;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldList extends GuiListWorldSelection {
    private List<WorldEntry> worldEntryList;
    private List<WorldEntry> relevantWorlds;
    private SinglePlayerMenu worldMenu;
    private int i = 0;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public WorldList(GuiWorldSelection worldSelection, Minecraft clientIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(worldSelection, clientIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        worldMenu = (SinglePlayerMenu) worldSelection;
        relevantWorlds = new ArrayList<>();
        worldEntryList = new ArrayList<>();
        refreshList();
    }

    public WorldList(GuiWorldSelection worldSelection, Minecraft clientIn, int slotHeightIn) {
        super(worldSelection, clientIn, 0, 0, 0, 0, slotHeightIn);
        worldMenu = (SinglePlayerMenu) worldSelection;
        relevantWorlds = new ArrayList<>();
        worldEntryList = new ArrayList<>();
        refreshList();
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void refreshList() {
        if (this.worldEntryList != null && this.worldMenu != null) {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            List<WorldSummary> list;

            try {
                list = isaveformat.getSaveList();
            }
            catch (AnvilConverterException anvilconverterexception) {
                References.LOG.error("Couldn't load level list", anvilconverterexception);
                this.mc.displayGuiScreen(new GuiErrorScreen(I18n.format("selectWorld.unable_to_load"), anvilconverterexception.getMessage()));
                return;
            }

            Collections.sort(list);

            for (WorldSummary worldsummary : list) {
                this.worldEntryList.add(new WorldEntry(this, worldsummary, this.mc.getSaveLoader()));
            }

            worldMenu.getDirectoryList().populateDirectoryList(worldEntryList);
        }
    }

    @Override
    public WorldEntry getListEntry(int index) {
        return this.relevantWorlds.get(index);
    }

    @Override
    protected int getSize() {
        return this.relevantWorlds.size();
    }


    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public WorldEntry getFullListEntry(int index) {
        return this.worldEntryList.get(index);
    }

    protected int getFullSize() {
        return this.worldEntryList.size();
    }

    public void displayWorlds(List<WorldEntry> worlds) {
        Collections.sort(worlds);
        relevantWorlds.clear();
        relevantWorlds.addAll(worlds);
    }
}

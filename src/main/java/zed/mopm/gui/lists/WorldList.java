package zed.mopm.gui.lists;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import zed.mopm.api.data.IModifiableList;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.SinglePlayerMenu;
import zed.mopm.gui.mutators.DirectorySelectionMenu;
import zed.mopm.gui.mutators.EditDirectory;
import zed.mopm.util.References;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldList extends GuiListWorldSelection implements IModifiableList {
    private List<WorldEntry> worldEntryList;
    private List<WorldEntry> relevantWorlds;
    private SinglePlayerMenu worldMenu;

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

    //:: GuiListWorldSelection
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void refreshList() {
        if (this.worldEntryList != null && this.worldMenu != null) {
            this.worldEntryList.clear();
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
    public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
        if (mouseEvent == 1 && this.getSlotIndexFromScreenCoords(mouseX, mouseY) != -1) {
            this.mc.displayGuiScreen(new EditDirectory<>(this.worldMenu, mouseX, mouseY, false,this));
            return true;
        }
        else {
            return super.mouseClicked(mouseX, mouseY, mouseEvent);
        }
    }

    @Nullable
    @Override
    public WorldEntry getSelectedWorld() {
        return this.selectedElement >= 0 && this.selectedElement < this.getSize() ? this.getListEntry(this.selectedElement) : null;
    }

    @Override
    public WorldEntry getListEntry(int index) {
        return this.relevantWorlds.get(index);
    }

    @Override
    protected int getSize() {
        return this.relevantWorlds.size();
    }

    //:: IModifiableList
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void rename(int entryIndex, String name) {
        this.getListEntry(entryIndex).rename(name);
    }

    @Override
    public void delete(int entryIndex) {
        this.selectedElement = entryIndex;
        this.getListEntry(entryIndex).deleteWorld(this.worldMenu.getDirectoryList());
    }

    @Override
    public void changeDir(int entryIndex) {
        this.selectedElement = entryIndex;
        WorldEntry entry = this.getListEntry(entryIndex);
        entry.removeWorld(this.worldMenu.getDirectoryList());
        this.mc.displayGuiScreen(new DirectorySelectionMenu(this.worldMenu, entry, new FolderList(this.worldMenu.getDirectoryList())));
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

    public int getSelectedIndex() {
        return this.selectedElement;
    }

    public void displayWorlds(List<WorldEntry> worlds) {
        Collections.sort(worlds);
        relevantWorlds.clear();
        relevantWorlds.addAll(worlds);
    }
}

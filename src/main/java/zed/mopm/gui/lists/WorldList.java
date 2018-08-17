package zed.mopm.gui.lists;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.api.gui.lists.IModifiableList;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.ModifiableMenu;
import zed.mopm.gui.SinglePlayerMenu;
import zed.mopm.gui.mutators.DirectorySelectionMenu;
import zed.mopm.gui.mutators.EditDirectory;
import zed.mopm.util.References;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldList extends GuiListWorldSelection implements IModifiableList, IListType<WorldEntry> {

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constants:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Fields:------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private List<WorldEntry> entryList;
    private List<WorldEntry> relevantEntries;
    private ModifiableMenu<SinglePlayerMenu, WorldEntry, WorldList> worldMenu;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public WorldList(final ModifiableMenu<SinglePlayerMenu, WorldEntry, WorldList> worldSelection, final Minecraft clientIn, final int slotHeightIn) {
        super(worldSelection.getInvokeScreen(), clientIn, 0, 0, 0, 0, slotHeightIn);
        worldMenu = worldSelection;
        relevantEntries = new ArrayList<>();
        entryList = new ArrayList<>();
        refreshList();
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: GuiListWorldSelection
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void refreshList() {
        if (this.entryList != null && this.worldMenu != null) {
            this.entryList.clear();
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            List<WorldSummary> list;

            try {
                list = isaveformat.getSaveList();
            } catch (AnvilConverterException anvilconverterexception) {
                References.LOG.error("Couldn't load level list", anvilconverterexception);
                this.mc.displayGuiScreen(new GuiErrorScreen(I18n.format("selectWorld.unable_to_load"), anvilconverterexception.getMessage()));
                return;
            }

            Collections.sort(list);

            for (WorldSummary worldsummary : list) {
                this.entryList.add(new WorldEntry(this, worldsummary, this.mc.getSaveLoader()));
            }

            worldMenu.getDirectoryList().populateDirectoryList(entryList);
        }
    }

    @Override
    public boolean mouseClicked(final int mouseX, final int mouseY, final int mouseEvent) {
        if (mouseEvent == 1 && this.getSlotIndexFromScreenCoords(mouseX, mouseY) != -1) {
            this.mc.displayGuiScreen(new EditDirectory<>(this.worldMenu, mouseX, mouseY, false, this));
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, mouseEvent);
        }
    }

    @Nullable
    @Override
    public WorldEntry getSelectedWorld() {
        return this.selectedElement >= 0 && this.selectedElement < this.getSize() ? this.getListEntry(this.selectedElement) : null;
    }

    @Override
    public WorldEntry getListEntry(final int index) {
        return this.relevantEntries.get(index);
    }

    @Override
    protected int getSize() {
        return this.relevantEntries.size();
    }

    //:: IModifiableList
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void rename(final int entryIndex, final String name) {
        this.getListEntry(entryIndex).rename(name);
    }

    @Override
    public void delete(final int entryIndex) {
        this.selectedElement = entryIndex;
        this.getListEntry(entryIndex).deleteWorld(this.worldMenu.getDirectoryList());
    }

    @Override
    public void changeDir(final int entryIndex) {
        this.selectedElement = entryIndex;
        WorldEntry entry = this.getListEntry(entryIndex);
        entry.removeWorld(this.worldMenu.getDirectoryList());
        this.mc.displayGuiScreen(new DirectorySelectionMenu(this.worldMenu, entry, new FolderList(this.worldMenu.getDirectoryList())));
    }

    //:: IListType
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void refresh() {
        this.refreshList();
    }

    @Override
    public void display(final List<WorldEntry> entries) {
        Collections.sort(entries);
        relevantEntries.clear();
        relevantEntries.addAll(entries);
    }


    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public WorldEntry getFullListEntry(final int index) {
        return this.entryList.get(index);
    }

    protected int getFullSize() {
        return this.entryList.size();
    }

    public int getSelectedIndex() {
        return this.selectedElement;
    }
}

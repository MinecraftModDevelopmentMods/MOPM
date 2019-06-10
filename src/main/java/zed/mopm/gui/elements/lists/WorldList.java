package zed.mopm.gui.elements.lists;

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
import zed.mopm.gui.menus.base.SelectMenuBase;
import zed.mopm.gui.menus.base.WorldSelectMenu;
import zed.mopm.gui.menus.mutators.directory.SelectDirectoryMenu;
import zed.mopm.gui.menus.mutators.directory.EditDirectoryMenu;
import zed.mopm.util.References;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldList
        extends GuiListWorldSelection
        implements IModifiableList,
        IListType<WorldEntry> {

    //-----Consts:--------------------------------------//

    /**
     * The error message if the world list could not be loaded.
     */
    private static final String LEVEL_LOAD_ERR = "Couldn't load level list";
    /**
     * The lang key for the vanilla error message for not being able to load
     * a world.
     */
    private static final String ERR_LANG = "selectWorld.unable_to_load";

    //-----Fields:--------------------------------------//

    /**
     * The containing world menu.
     */
    private SelectMenuBase<
            WorldSelectMenu,
            WorldEntry,
            WorldList
            > worldMenu;
    /**
     * The full list of world entries.
     */
    private List<WorldEntry> entryList = new ArrayList<>();
    /**
     * The list of world entries currently being displayed.
     */
    private List<WorldEntry> relevantEntries = new ArrayList<>();

    //-----Constructors:--------------------------------//

    /**
     * Creates a new world list.
     * @param worldSelection The containing selection menu.
     * @param clientIn The minecraft client.
     * @param slotHeightIn The slot height of world entries.
     */
    public WorldList(
            final SelectMenuBase<
                    WorldSelectMenu,
                    WorldEntry,
                    WorldList
                    > worldSelection,
            final Minecraft clientIn,
            final int slotHeightIn
    ) {
        super(
                worldSelection.getInvokeScreen(),
                clientIn,
                0,
                0,
                0,
                0,
                slotHeightIn
        );
        this.worldMenu = worldSelection;
        refreshList();
    }

    //-----Overridden Methods:--------------------------//

    //:: GuiListWorldSelection
    //:::::::::::::::::::::::::::::://

    /**
     * Refreshes the world list and populates the full world entry
     * list.
     */
    @Override
    public final void refreshList() {
        if (this.entryList != null && this.worldMenu != null) {
            this.entryList.clear();
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            List<WorldSummary> list;

            try {
                list = isaveformat.getSaveList();
            } catch (AnvilConverterException anvilconverterexception) {
                References.LOG.error(
                        LEVEL_LOAD_ERR,
                        anvilconverterexception
                );
                this.mc.displayGuiScreen(
                        new GuiErrorScreen(
                                I18n.format(ERR_LANG),
                                anvilconverterexception.getMessage()
                        )
                );
                return;
            }

            Collections.sort(list);

            for (WorldSummary worldsummary : list) {
                this.entryList.add(
                        new WorldEntry(
                                this,
                                worldsummary,
                                this.mc.getSaveLoader()
                        )
                );
            }

            worldMenu.getDirectoryList().populateDirectoryList(entryList);
        }
    }

    /**
     * If a world entry was right clicked, this will open up the
     * entry edit menu.
     * @param mouseX The x position of the mouse click.
     * @param mouseY The y position of the mouse click.
     * @param mouseEvent The mouse event of the click.
     * @return Returns true, or super.false.
     */
    @Override
    public final boolean mouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseEvent
    ) {
        if (
                mouseEvent == 1
                        && this.getSlotIndexFromScreenCoords(
                        mouseX,
                        mouseY
                ) != -1
        ) {
            this.mc.displayGuiScreen(
                    new EditDirectoryMenu<>(
                            this.worldMenu,
                            mouseX,
                            mouseY,
                            false,
                            this)
            );
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, mouseEvent);
        }
    }

    /**
     * @return Returns the world entry at the currently selected index.
     */
    @Nullable
    @Override
    public final WorldEntry getSelectedWorld() {
        return this.selectedElement >= 0
                && this.selectedElement < this.getSize()
                ? this.getListEntry(this.selectedElement)
                : null;
    }

    /**
     * @param index The index of the world entry.
     * @return Returns the world entry from the relevant display list.
     */
    @Override
    public final WorldEntry getListEntry(final int index) {
        return this.relevantEntries.get(index);
    }

    /**
     * @return Returns the count of currently displayed entries.
     */
    @Override
    protected final int getSize() {
        return this.relevantEntries.size();
    }

    //:: IModifiableList
    //:::::::::::::::::::::::::::::://

    /**
     * Renames a world entry.
     * @param entryIndex The index of the world entry to rename.
     * @param name The new name of the world entry.
     */
    @Override
    public final void rename(final int entryIndex, final String name) {
        this.getListEntry(entryIndex).rename(name);
    }

    /**
     * Deletes a world entry.
     * @param entryIndex The index of a world entry to delete.
     */
    @Override
    public final void delete(final int entryIndex) {
        this.selectedElement = entryIndex;
        this.getListEntry(entryIndex)
                .deleteWorld(this.worldMenu.getDirectoryList());
    }

    /**
     * Moves a world entry to a different directory.
     * @param entryIndex The index of the world entry to move.
     */
    @Override
    public final void changeDir(final int entryIndex) {
        this.selectedElement = entryIndex;
        WorldEntry entry = this.getListEntry(entryIndex);
        entry.removeWorld(this.worldMenu.getDirectoryList());
        this.mc.displayGuiScreen(
                new SelectDirectoryMenu(
                        this.worldMenu,
                        entry,
                        new DirectoryList(this.worldMenu.getDirectoryList())
                )
        );
    }

    //:: IListType
    //:::::::::::::::::::::::::::::://

    /**
     * Refreshes the list of all worlds.
     */
    @Override
    public final void refresh() {
        this.refreshList();
    }

    /**
     * Sets the list of currently displayed world entry.
     * @param entries The list of entries to display.
     */
    @Override
    public final void display(final List<WorldEntry> entries) {
        Collections.sort(entries);
        relevantEntries.clear();
        relevantEntries.addAll(entries);
    }


    //-----This:----------------------------------------//

    /**
     * @return Returns the index of the currently selected entry.
     */
    public final int getSelectedIndex() {
        return this.selectedElement;
    }

    /**
     * @return Returns the base selection menu.
     */
    public final SelectMenuBase<
            WorldSelectMenu,
            WorldEntry,
            WorldList
            > getWorldMenu() {
        return this.worldMenu;
    }
}

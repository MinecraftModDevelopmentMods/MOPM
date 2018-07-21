package zed.mopm.gui.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerSelectionList;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.api.gui.lists.IModifiableList;
import zed.mopm.data.ServerEntry;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.ModifiableMenu;
import zed.mopm.gui.SinglePlayerMenu;

import java.util.List;

public class ServerList extends ServerSelectionList implements IModifiableList, IListType<ServerEntry> {

    List<ServerEntry> serverEntryList;
    List<ServerEntry> relevantServers;
    private ModifiableMenu<SinglePlayerMenu, WorldEntry, WorldList> serverMenu;

    public ServerList(final GuiMultiplayer ownerIn, final Minecraft mcIn, final int widthIn, final int heightIn, final int topIn, final int bottomIn, final int slotHeightIn) {
        super(ownerIn, mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
    }

    public ServerList(final GuiMultiplayer worldSelection, final Minecraft clientIn, final int slotHeightIn) {
        super(worldSelection, clientIn, 0, 0, 0, 0, slotHeightIn);
    }

    @Override
    public void rename(final int entryIndex, final String name) {
    	//
    }

    @Override
    public void delete(final int entryIndex) {
    	//
    }

    @Override
    public void changeDir(final int entryIndex) {
    	//
    }

    @Override
    public void display(final List<ServerEntry> entries) {
    	//
    }
}

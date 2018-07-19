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

    public ServerList(GuiMultiplayer ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(ownerIn, mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
    }

    public ServerList(GuiMultiplayer worldSelection, Minecraft clientIn, int slotHeightIn) {
        super(worldSelection, clientIn, 0, 0 ,0 ,0, slotHeightIn);
    }

    @Override
    public void rename(int entryIndex, String name) {

    }

    @Override
    public void delete(int entryIndex) {

    }

    @Override
    public void changeDir(int entryIndex) {

    }

    @Override
    public void display(List<ServerEntry> entries) {

    }
}

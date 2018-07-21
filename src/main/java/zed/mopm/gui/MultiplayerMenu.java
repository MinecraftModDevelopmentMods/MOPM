package zed.mopm.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import zed.mopm.api.gui.IMenuType;
import zed.mopm.data.ServerEntry;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.gui.lists.ServerList;
import zed.mopm.gui.mutators.CreateServerEntryMenu;

import java.io.IOException;
import java.util.List;

public class MultiplayerMenu extends GuiMultiplayer implements IMenuType {

    private ServerData saveData;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public MultiplayerMenu(final GuiScreen parentScreen) {
        super(parentScreen);
        this.saveData = new ServerData(I18n.format("selectServer.defaultName"), "", false);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    protected void actionPerformed(final GuiButton button, final FolderList<ServerEntry> directories, final ServerList selectionList) throws IOException {
        super.actionPerformed(button);
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int mouseButton) {
        //:: do nothing
        // - Override this so the wrapper gui handles all mouse input
    }

    @Override
    public void invokeEntryCreation(final ModifiableMenu menu) {
        this.saveData.copyFrom(new ServerData(I18n.format("selectServer.defaultName"), "", false));
        this.mc.displayGuiScreen(new CreateServerEntryMenu(this, this.saveData, new FolderList(menu.getDirectoryList())));
    }

    @Override
    public List<GuiButton> getButtonList() {
        return this.buttonList;
    }
}

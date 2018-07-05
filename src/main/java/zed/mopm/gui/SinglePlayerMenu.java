package zed.mopm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import zed.mopm.api.data.IFolderMenu;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.gui.lists.WorldList;
import zed.mopm.gui.mutators.CreateFolderEntryMenu;
import zed.mopm.gui.mutators.CreateWorldEntryMenu;
import zed.mopm.util.GuiUtils;
import zed.mopm.util.References;

import java.io.IOException;

import static zed.mopm.gui.SinglePlayerMenu.SelectedList.BUTTONS;
import static zed.mopm.gui.SinglePlayerMenu.SelectedList.FOLDER_LIST;
import static zed.mopm.gui.SinglePlayerMenu.SelectedList.WORLD_LIST;

public class SinglePlayerMenu extends GuiWorldSelection implements IFolderMenu {

    enum SelectedList {
        WORLD_LIST,
        FOLDER_LIST,
        BUTTONS
    }

    private FolderList<WorldEntry> folders;
    private WorldList worldSelectionList;
    private SelectedList scrollList;

    private GuiButtonExt createFolderEntryButton;
    private GuiButtonExt back;
    private GuiButtonExt print;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public SinglePlayerMenu(GuiScreen screenIn) {
        super(screenIn);
        scrollList = WORLD_LIST;

        createFolderEntryButton = new GuiButtonExt(99, 30, 10, 15, 15, "+");
        back = new GuiButtonExt(101, 10, 10, 20, 15, "<<");
        print = new GuiButtonExt(102, 45, 10, 15, 15, "/");
        folders = new FolderList<>(this, 100,0, 32, 20, Minecraft.getMinecraft().mcDataDir);
        //TODO:: Potentially fix what ever issue may arise if the world list only gets refreshed once
        worldSelectionList = new WorldList(this, Minecraft.getMinecraft(), 36);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void initGui() {
        this.addButton(createFolderEntryButton);
        this.addButton(back);
        this.addButton(print);
        this.folders.setHeight(this.height);

        worldSelectionList.setDimensions(this.width + folders.width, this.height - 100, 32, this.height - 64);

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        worldSelectionList.drawScreen(mouseX, mouseY, partialTicks);
        folders.drawScreen(mouseX, mouseY, partialTicks);
        GuiUtils.drawTexturedRect(0.0D, (double)(this.height - 64), (double)(this.width), (double) this.height, (double)this.zLevel + 1, 64, 64, 64, 255, 0, OPTIONS_BACKGROUND, this.mc);

        for (GuiButton button : this.buttonList) {
            button.drawButton(this.mc, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        try {
            switch (button.id) {
                case 3: {
                    this.mc.displayGuiScreen(new CreateWorldEntryMenu(this, new FolderList(this.folders)));
                }
                break;

                case 99: {
                    CreateFolderEntryMenu mkDir = new CreateFolderEntryMenu(this);
                    Minecraft.getMinecraft().displayGuiScreen(mkDir);
                }
                break;

                case 101: {
                    folders.back();
                }
                break;

                case 102: {
                    folders.print();
                    //Todo:: Remove
                    References.LOG.info("CLONED: ");
                    new FolderList(this.folders).print();
                    this.worldSelectionList.remove();
                }
                break;

                default: {
                    super.actionPerformed(button);
                }
            }
        }
        catch (IOException e) {
            References.LOG.error("", e);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        switch (this.scrollList) {
            case FOLDER_LIST:
                this.folders.handleMouseInput();
                break;
            case WORLD_LIST:
                this.worldSelectionList.handleMouseInput();
                break;
            case BUTTONS:
                //Nothing to handle here
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseY >= 32 && mouseY <= this.height - 64) {
            if (mouseX >= 0 && mouseX <= this.folders.width) {
                this.scrollList = FOLDER_LIST;
                this.folders.mouseClicked(mouseX, mouseY, mouseButton);
            }
            else {
                this.scrollList = WORLD_LIST;
                this.worldSelectionList.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
        else {
            this.scrollList = BUTTONS;
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        switch (this.scrollList) {
            case FOLDER_LIST:
                this.folders.mouseReleased(mouseX, mouseY, state);
                break;
            case WORLD_LIST:
                this.worldSelectionList.mouseReleased(mouseX, mouseY, state);
                break;
            case BUTTONS:
                super.mouseReleased(mouseX, mouseY, state);
                break;
        }
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        folders.setHeight(h);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void addFolder(String name) {
        folders.addFolder(name);
    }

    public FolderList getFolders() {
        return this.folders;
    }
}

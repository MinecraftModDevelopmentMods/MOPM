package zed.mopm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.IFolderMenu;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.buttons.ToolTipButton;
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

    private FolderList<WorldEntry> directories;
    private WorldList worldSelectionList;
    private SelectedList listFocus;

    private GuiButtonExt createFolderEntryButton;
    private GuiButtonExt back;
    private GuiButtonExt print;
    private GuiButtonExt hidePath;

    private GuiTextField directoryDisplay;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public SinglePlayerMenu(GuiScreen screenIn) {
        super(screenIn);
        listFocus = WORLD_LIST;

        createFolderEntryButton = new ToolTipButton(99, 30, 10, 15, 15, "+", "New Folder");
        back = new ToolTipButton(101, 10, 10, 20, 15, "<<", "Back");
        print = new ToolTipButton(102, 45, 10, 15, 15, "/", "Save");
        hidePath = new ToolTipButton(103, 65, 2, 5, 5, "", "Hide");

        directories = new FolderList<>(this, 100,0, 32, 20, Minecraft.getMinecraft().gameDir);
        worldSelectionList = new WorldList(this, Minecraft.getMinecraft(), 36);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.directoryDisplay = new GuiTextField(1, this.fontRenderer, 65, 10, (this.directoryDisplay == null) ? this.width - 70 : this.directoryDisplay.width, 15);
        this.directoryDisplay.setMaxStringLength(Integer.MAX_VALUE);
        this.directoryDisplay.setText(this.directories.currentPath());

        this.addButton(createFolderEntryButton);
        this.addButton(back);
        this.addButton(print);
        this.addButton(hidePath);
        this.directories.setHeight(this.height);

        this.directories.save();
        this.worldSelectionList.refreshList();
        this.worldSelectionList.setDimensions(this.width + directories.width, this.height - 100, 32, this.height - 64);
        this.refreshDirectoryEntryList();

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        worldSelectionList.drawScreen(mouseX, mouseY, partialTicks);
        directories.drawScreen(mouseX, mouseY, partialTicks);
        GuiUtils.drawTexturedRect(0.0D, (double)(this.height - 64), (double)(this.width), (double) this.height, (double)this.zLevel + 1, 64, 64, 64, 255, 0, OPTIONS_BACKGROUND, this.mc);
        this.directoryDisplay.drawTextBox();

        int mouseOver = -1;
        for (GuiButton button : this.buttonList) {
            button.drawButton(this.mc, mouseX, mouseY, partialTicks);
            if (button.isMouseOver()) {
                mouseOver = this.buttonList.indexOf(button);
            }
        }

        if (mouseOver != -1 && this.buttonList.get(mouseOver) instanceof ToolTipButton) {
            ((ToolTipButton)this.buttonList.get(mouseOver)).drawHoverState(this.mc, mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        try {
            WorldEntry selectedWorld = this.worldSelectionList.getSelectedWorld();
            switch (button.id) {
                case 0: {
                    this.mc.displayGuiScreen(this.prevScreen);
                }
                break;

                case 1: {
                    if (selectedWorld != null) {
                        selectedWorld.joinWorld();
                    }
                }
                break;

                case 2: {
                    if (selectedWorld != null) {
                        selectedWorld.deleteWorld(this.directories);
                    }
                }
                break;

                case 3: {
                    this.mc.displayGuiScreen(new CreateWorldEntryMenu(this, new FolderList(this.directories)));
                }
                break;

                case 4: {
                    if (selectedWorld != null) {
                        selectedWorld.editWorld();
                    }
                }
                break;

                case 5: {
                    if (selectedWorld != null) {
                        selectedWorld.recreateWorld();
                    }
                }
                break;

                case 99: {
                    CreateFolderEntryMenu mkDir = new CreateFolderEntryMenu(this);
                    Minecraft.getMinecraft().displayGuiScreen(mkDir);
                }
                break;

                case 101: {
                    directories.back();
                    this.directoryDisplay.setText(this.directories.currentPath());
                }
                break;

                case 102: {
                    References.LOG.info("Directory Tree:");
                    this.directories.print();
                    this.directories.save();
                }
                break;

                case 103: {
                    if (this.directoryDisplay.width > 5) {
                        ((ToolTipButton) this.hidePath).setToolTip("Unhide");
                        this.directoryDisplay.width = 5;
                        this.directoryDisplay.setText("");
                    }
                    else {
                        ((ToolTipButton) this.hidePath).setToolTip("Hide");
                        this.directoryDisplay.width = this.width - 70;
                        this.directoryDisplay.setText(this.directories.currentPath());
                    }
                }
                break;

                default: {
                    /// This should not be reached.
                    super.actionPerformed(button);
                }
            }
        }
        catch (IOException e) {
            References.LOG.error("", e);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (this.directoryDisplay.isFocused() && keyCode == 203 || keyCode == 205 || GuiScreen.isKeyComboCtrlA(keyCode) || GuiScreen.isKeyComboCtrlC(keyCode)) {
            this.directoryDisplay.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        switch (this.listFocus) {
            case FOLDER_LIST:
                this.directories.handleMouseInput();
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
            if (mouseX >= 0 && mouseX <= this.directories.width) {
                this.listFocus = FOLDER_LIST;
                this.directories.mouseClicked(mouseX, mouseY, mouseButton);
            }
            else {
                this.listFocus = WORLD_LIST;
                this.worldSelectionList.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
        else {
            this.listFocus = BUTTONS;
            super.mouseClicked(mouseX, mouseY, mouseButton);
            this.directoryDisplay.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        switch (this.listFocus) {
            case FOLDER_LIST:
                this.directories.mouseReleased(mouseX, mouseY, state);
                this.directoryDisplay.setText(this.directories.currentPath());
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
        directories.setHeight(h);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public FolderList<WorldEntry> getDirectoryList() {
        return this.directories;
    }

    @Override
    public void refreshDirectoryEntryList() {
        this.worldSelectionList.displayWorlds(this.directories.getFolder().getEntries());
    }
}

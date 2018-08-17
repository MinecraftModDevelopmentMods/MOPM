package zed.mopm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.*;
import zed.mopm.api.gui.*;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.api.gui.lists.IModifiableList;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.buttons.ToolTipButton;
import zed.mopm.gui.lists.FolderList;
import zed.mopm.gui.lists.ServerEntryList;
import zed.mopm.gui.lists.WorldList;
import zed.mopm.gui.mutators.CreateFolderEntryMenu;
import zed.mopm.util.GuiUtils;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.IOException;

public class ModifiableMenu<K extends GuiScreen & IMenuType, J extends GuiListExtended.IGuiListEntry & IFolderPath, L extends GuiListExtended & IModifiableList & IListType<J>> extends GuiScreen implements IFolderMenu {

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constants:---------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private static final int CREATE_ENTRY_ID = 3;
    private static final int SUBDIR_BTN_ID = 99;
    private static final int BACK_BTN_ID = 101;
    private static final int SAVE_BTN_ID = 102;
    private static final int HIDE_BTN_ID = 103;

    private static final int BACK_BTN_WIDTH = MOPMLiterals.SQUARE_BUTTON_DIM + MOPMLiterals.BASE_FIVE;
    private static final int DIR_LIST_WIDTH = 100;
    private static final int DIR_DISPLAY_PADDING = 70;

    private static final int DIR_LIST_SLOT_HEIGHT = 20;
    private static final int DIR_DISPLAY_HEIGHT = 15;

    private static final int BACK_BTN_X = 10;
    private static final int SAVE_BTN_X = 45;
    private static final int HIDE_BTN_X = 65;
    private static final int DIR_LIST_X = 32;
    private static final int DIR_DISPLAY_X = 65;

    private static final int HEADER_BUTTON_Y = 10;
    private static final int HIDE_BTN_Y = 2;
    private static final int DIR_DISPLAY_Y = 10;

    private static final int BASE_64 = 64;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Fields:------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private K invokeScreen;

    private FolderList<J> directories;
    private L entrySelectionList;
    private SelectedList listFocus;

    private GuiButtonExt createDir;
    private GuiButtonExt back;
    private GuiButtonExt save;
    private GuiButtonExt hidePath;

    private GuiTextField directoryDisplay;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public ModifiableMenu(final K wrappedScreenIn, final Minecraft clientIn) {
        this.invokeScreen = wrappedScreenIn;
        this.listFocus = SelectedList.ENTRY_LIST;

        this.back = new ToolTipButton(BACK_BTN_ID, BACK_BTN_X, HEADER_BUTTON_Y, BACK_BTN_WIDTH, MOPMLiterals.SQUARE_BUTTON_DIM, "<<", "Back");
        this.createDir = new ToolTipButton(SUBDIR_BTN_ID, BACK_BTN_X + BACK_BTN_WIDTH, HEADER_BUTTON_Y, MOPMLiterals.SQUARE_BUTTON_DIM, MOPMLiterals.SQUARE_BUTTON_DIM, "+", "New Folder");
        this.save = new ToolTipButton(SAVE_BTN_ID, SAVE_BTN_X, HEADER_BUTTON_Y, MOPMLiterals.SQUARE_BUTTON_DIM, MOPMLiterals.SQUARE_BUTTON_DIM, "/", "Save");
        this.hidePath = new ToolTipButton(HIDE_BTN_ID, HIDE_BTN_X, HIDE_BTN_Y, MOPMLiterals.BASE_FIVE, MOPMLiterals.BASE_FIVE, "", "Hide");

        this.directories = new FolderList<>(this, DIR_LIST_WIDTH, 0, DIR_LIST_X, DIR_LIST_SLOT_HEIGHT, clientIn.gameDir);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: GuiScreen
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        invokeScreen.setWorldAndResolution(this.mc, this.width, this.height);
        invokeScreen.initGui();
        invokeScreen.listInit(entrySelectionList);

        this.directoryDisplay = new GuiTextField(1, this.fontRenderer, DIR_DISPLAY_X, DIR_DISPLAY_Y, (this.directoryDisplay == null) ? this.width - DIR_DISPLAY_PADDING : this.directoryDisplay.width, DIR_DISPLAY_HEIGHT);
        this.directoryDisplay.setMaxStringLength(Integer.MAX_VALUE);
        this.directoryDisplay.setText(this.directories.currentPath());

        this.buttonList.clear();
        this.addButton(createDir);
        this.addButton(back);
        this.addButton(save);
        this.addButton(hidePath);

        for (GuiButton button : invokeScreen.getButtonList()) {
            this.addButton(button);
        }

        this.directories.setHeight(this.height);

        this.directories.save();
        this.entrySelectionList.setDimensions(this.width + directories.width, this.height - DIR_LIST_WIDTH, DIR_LIST_X, this.height - BASE_64);
        this.refreshDirectoryEntryList();

        super.initGui();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        entrySelectionList.drawScreen(mouseX, mouseY, partialTicks);
        directories.drawScreen(mouseX, mouseY, partialTicks);
        GuiUtils.drawTexturedRect(0.0D, (double)(this.height - BASE_64), (double)(this.width), (double) this.height, (double)this.zLevel + 1, BASE_64, BASE_64, BASE_64, MOPMLiterals.COLOR_MAX, 0, OPTIONS_BACKGROUND, this.mc);
        this.directoryDisplay.drawTextBox();

        int mouseOver = -1;
        for (GuiButton button : this.buttonList) {
            button.drawButton(this.mc, mouseX, mouseY, partialTicks);
            if (button.isMouseOver()) {
                mouseOver = this.buttonList.indexOf(button);
            }
        }

        if (mouseOver != -1 && this.buttonList.get(mouseOver) instanceof ToolTipButton) {
            ((ToolTipButton) this.buttonList.get(mouseOver)).drawHoverState(this.mc, mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        switch (button.id) {
            //:: Create new entry
            case CREATE_ENTRY_ID:
                this.invokeScreen.invokeEntryCreation(this);
                break;

            //:: Create new subdirectory
            case SUBDIR_BTN_ID:
                this.mc.displayGuiScreen(new CreateFolderEntryMenu(this));
                break;

            //:: Jump go back down in the directory path
            case BACK_BTN_ID:
                directories.back();
                this.directoryDisplay.setText(this.directories.currentPath());
                break;

            //:: Save directory tree to a file
            case SAVE_BTN_ID:
                References.LOG.info("Directory Tree:");
                this.directories.print();
                this.directories.save();
                break;

            //:: Hide or Unhide directory display
            case HIDE_BTN_ID:
                this.toggleDirectoryDisplay();
                break;

            default:
                if (this.invokeScreen instanceof SinglePlayerMenu) {
                    ((SinglePlayerMenu) this.invokeScreen).actionPerformed(button, (FolderList<WorldEntry>) this.directories, (WorldList) this.entrySelectionList);
                } else if (this.entrySelectionList instanceof ServerEntryList) {
                    ((MultiplayerMenu) this.invokeScreen).actionPerformed(button, this);
                }
                break;
        }
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        References.LOG.info("KEY CODE: " + keyCode);
        final boolean navKeyPressed = keyCode == MOPMLiterals.LEFT_ARROW_KEY || keyCode == MOPMLiterals.RIGHT_ARROW_KEY || GuiScreen.isKeyComboCtrlA(keyCode) || GuiScreen.isKeyComboCtrlC(keyCode);
        if (this.directoryDisplay.isFocused() && navKeyPressed) {
            this.directoryDisplay.textboxKeyTyped(typedChar, keyCode);
        } else if (keyCode == MOPMLiterals.ESC_KEY) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        invokeScreen.handleMouseInput();
        switch (this.listFocus) {

            //:: The directory list is in focus
            // - Handle the mouse input of the directory list and only the directory list
            case FOLDER_LIST:
                this.directories.handleMouseInput();
                break;

            //:: The world list is in focus
            // - Handle the mouse input of the world list and only the world list
            case ENTRY_LIST:
                this.entrySelectionList.handleMouseInput();
                break;

            //:: Neither the directory list or the world list is in focus
            // - Do nothing because there is nothing to handle
            default:
                //:: This should not be reached
                break;
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseY >= DIR_LIST_X && mouseY <= this.height - BASE_64) {
            if (mouseX >= 0 && mouseX <= this.directories.width) {
                this.listFocus = SelectedList.FOLDER_LIST;
                this.directories.mouseClicked(mouseX, mouseY, mouseButton);
            } else {
                this.listFocus = SelectedList.ENTRY_LIST;
                this.entrySelectionList.mouseClicked(mouseX, mouseY, mouseButton);
            }
        } else {
            this.listFocus = SelectedList.BUTTONS;
            this.directoryDisplay.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        super.mouseReleased(mouseX, mouseY, state);
        switch (this.listFocus) {

            //:: The folder list has been put into focus
            case FOLDER_LIST:
                this.directories.mouseReleased(mouseX, mouseY, state);
                this.directoryDisplay.setText(this.directories.currentPath());
                break;

            //:: The world list has been put into focus
            case ENTRY_LIST:
                this.entrySelectionList.mouseReleased(mouseX, mouseY, state);
                break;

            //:: The world list and folder list is not in focus
            case BUTTONS:
                //:: There is nothing to handle when buttons are in focus
                break;

            default:
                //:: This should not be reached
                break;

        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        this.invokeScreen.confirmClicked(result, id);
        this.mc.displayGuiScreen(this);
    }

    @Override
    public void onResize(final Minecraft mcIn, final int w, final int h) {
        super.onResize(mcIn, w, h);
        directories.setHeight(h);
        this.directoryDisplay.width = (this.directoryDisplay.width > MOPMLiterals.BASE_FIVE) ? this.width - DIR_DISPLAY_PADDING : MOPMLiterals.BASE_FIVE;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    //:: IFolderMenu
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    public FolderList<J> getDirectoryList() {
        return this.directories;
    }

    @Override
    public void refreshDirectoryEntryList() {
        this.entrySelectionList.refresh();
        this.entrySelectionList.display(this.directories.getFolder().getEntries());
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    private void toggleDirectoryDisplay() {
        if (this.directoryDisplay.width > MOPMLiterals.BASE_FIVE) {
            ((ToolTipButton) this.hidePath).setToolTip("Unhide");
            this.directoryDisplay.width = MOPMLiterals.BASE_FIVE;
            this.directoryDisplay.setText("");
        } else {
            ((ToolTipButton) this.hidePath).setToolTip("Hide");
            this.directoryDisplay.width = this.width - DIR_DISPLAY_PADDING;
            this.directoryDisplay.setText(this.directories.currentPath());
        }
    }

    public K getInvokeScreen() {
        return this.invokeScreen;
    }

    public void setContainingList(final L containingList) {
        this.entrySelectionList = containingList;
    }
}

package zed.mopm.gui.menus.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.*;
import zed.mopm.api.gui.*;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.api.gui.lists.IModifiableList;
import zed.mopm.data.WorldEntry;
import zed.mopm.gui.elements.base.NavigationDisplay;
import zed.mopm.gui.elements.buttons.ButtonFactory;
import zed.mopm.gui.elements.lists.DirectoryList;
import zed.mopm.gui.elements.lists.ServerList;
import zed.mopm.gui.elements.lists.WorldList;
import zed.mopm.gui.menus.mutators.directory.CreateDirectoryMenu;
import zed.mopm.gui.utils.GuiUtils;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static zed.mopm.gui.utils.constants.MenuConsts.*;
import static zed.mopm.gui.utils.constants.ColorConsts.COLOR_MAX;
import static zed.mopm.gui.utils.constants.ButtonConsts.SPACE_WIDTH;
import static zed.mopm.gui.utils.constants.KeyConsts.*;

public class SelectMenuBase<
        K extends GuiScreen & IMenuType,
        J extends GuiListExtended.IGuiListEntry & IFolderPath,
        L extends GuiListExtended & IModifiableList & IListType<J>
        >
        extends GuiScreen
        implements IFolderMenu {

    //-----Fields:--------------------------------------//

    /**
     * This is the menu type that {@link SelectMenuBase}
     * bases its controls off of.
     * {@link #invokeScreen} can be one of two types of menus.
     * <br><br>
     * {@link ServerSelectMenu}:    The selection menu for multi-player servers.
     * <br>
     * {@link WorldSelectMenu}:     The selection menu for single player worlds.
     * <br><br>
     * Most/all controls of the specific menu are handled within their
     * respective classes. Reference those classes for more information
     * on how they work.
     */
    private K invokeScreen;

    /**
     * This is the tree-like directory structure that is used to store
     * worlds or servers. It's primary role is to act as a reference for
     * directory traversing.
     * <br><br>
     * <b>Reference</b> {@link DirectoryList} for details.
     */
    private DirectoryList<J> directoryList;

    /**
     * This is a GUI component that lists out the entries of the particular
     * menu type.
     * <br><br>
     * <b>For instance:</b> <br>
     * {@link ServerSelectMenu}:    The list type will be {@link ServerList}.
     * <br>
     * {@link WorldSelectMenu}:     The list type will be {@link WorldList}.
     */
    private L entrySelectionList;

    /**
     * This enum keeps track of what GUI component is focused.
     * <br><br>
     * {@link SelectedList#ENTRY_LIST}: The entry list is focused. If the entry
     * list is focused, worlds / servers have the focus. All clicking or typing
     * actions will be invoked on the entry list, and no other GUI component.
     * <br><br>
     * {@link SelectedList#DIRECTORY_LIST}: The directory list is focused. If
     * the directory list is focused, all clicking or typing actions will be
     * invoked on the directory list, and no other GUI component.
     * <br><br>
     * {@link SelectedList#BUTTONS}: The menu buttons have primary focus.
     * This focus is a fuzzy focus. I.e. the buttons can be clicked on
     * disregarding focus, but if there is no specific focus, then the
     * GUI buttons have main priority for clicking actions.
     */
    private SelectedList listFocus;

    /**
     * The navigation bar element of the selection menu.
     */
    private NavigationDisplay navBar;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new selection menu.
     * @param wrappedScreenIn The type of menu that will be
     *                        controlled by this menu.
     * @param clientIn The minecraft client in.
     */
    public SelectMenuBase(final K wrappedScreenIn, final Minecraft clientIn) {
        this.invokeScreen = wrappedScreenIn;
        this.listFocus = SelectedList.ENTRY_LIST;
        this.navBar = new NavigationDisplay(clientIn);

        this.directoryList = new DirectoryList<>(
                this,
                DIR_LIST_WIDTH,
                0,
                DIR_LIST_X,
                DIR_LIST_SLOT_HEIGHT,
                clientIn.gameDir
        );
    }

    //-----Overridden Methods:--------------------------//

    //:: GuiScreen
    //:::::::::::::::::::::::::::::://

    /**
     * Initiates the selection gui with all of its elements
     * along with initiating the menu the selection menu is in
     * control of.
     */
    @Override
    public final void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.directoryList.gotoBase();
        this.navBar.isPreviousButtonEnabled(false);
        this.invokeScreen.setWorldAndResolution(
                this.mc,
                this.width,
                this.height
        );
        this.invokeScreen.initGui();
        this.invokeScreen.listInit(entrySelectionList);
        this.navBar.updateNavDisplay(
                this.width,
                this.directoryList.currentPath()
        );

        //:: - Add buttons to the menu
        final List<GuiButton> menuButtons = new ArrayList<>();
        this.buttonList.clear();
        menuButtons.addAll(this.navBar.getNavButtons());
        menuButtons.addAll(invokeScreen.getButtonList());

        for (final GuiButton button : menuButtons) {
            this.addButton(button);
        }
        //--------------------------//

        this.directoryList.setHeight(this.height);
        this.directoryList.save();
        this.entrySelectionList.setDimensions(
                this.width + this.directoryList.width,
                this.height - DIR_LIST_WIDTH,
                DIR_LIST_X,
                this.height - BASE_64
        );

        this.refreshDirectoryEntryList();
        super.initGui();
    }

    /**
     * Draws the crucial elements of the selection screen.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param partialTicks The partial game ticks.
     */
    @Override
    public final void drawScreen(
            final int mouseX,
            final int mouseY,
            final float partialTicks
    ) {
        this.listFocus = this.getListFocus(mouseX, mouseY);

        this.entrySelectionList.drawScreen(mouseX, mouseY, partialTicks);
        this.directoryList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawOptionsBackground();
        this.navBar.draw();
        ButtonFactory.drawButtonToolTips(
                this.buttonList,
                this.mc,
                mouseX,
                mouseY,
                partialTicks
        );
    }

    /**
     * Handles the action of the activated button.
     * @param button The button that was activated.
     */
    @Override
    protected final void actionPerformed(final GuiButton button) {
        switch (button.id) {
            //:: Create new entry
            case CREATE_ENTRY_ID:
                this.invokeScreen.invokeEntryCreation(this);
                break;

            //:: Create new subdirectory
            case SUBDIR_BTN_ID:
                this.mc.displayGuiScreen(new CreateDirectoryMenu(this));
                break;

            //:: Jump go previousDirectoryBtn down in the directory path
            case BACK_BTN_ID:
                directoryList.back();
                this.navBar.getPathNavDisplay()
                        .setText(this.directoryList.currentPath());
                break;

            //:: Save directory tree to a file
            case SAVE_BTN_ID:
                References.LOG.info("Directory Tree:");
                this.directoryList.print();
                this.directoryList.save();
                break;

            //:: Hide or Unhide directory display
            case HIDE_BTN_ID:
                this.navBar.toggleNavDisplay(
                        this.width,
                        this.directoryList.currentPath()
                );
                break;

            default:
                if (this.invokeScreen instanceof WorldSelectMenu) {
                    ((WorldSelectMenu) this.invokeScreen).actionPerformed(
                            button,
                            (DirectoryList<WorldEntry>) this.directoryList,
                            (WorldList) this.entrySelectionList
                    );
                } else if (this.entrySelectionList instanceof ServerList) {
                    ((ServerSelectMenu) this.invokeScreen).actionPerformed(
                            button,
                            this
                    );
                }
                break;
        }
    }

    /**
     * Handles keyboard input and dictates what menu element gets
     * the keyboard input.
     * @param typedChar The character of the key typed.
     * @param keyCode The code of the key typed.
     * @throws IOException Thrown if there was a wrong input.
     */
    @Override
    protected final void keyTyped(
            final char typedChar,
            final int keyCode
    ) throws IOException {
        final GuiTextField navTextField = this.navBar.getPathNavDisplay();
        final boolean navKeyPressed = keyCode == LEFT_ARROW
                || keyCode == RIGHT_ARROW
                || GuiScreen.isKeyComboCtrlA(keyCode)
                || GuiScreen.isKeyComboCtrlC(keyCode);

        if (navTextField.isFocused() && navKeyPressed) {
            navTextField.textboxKeyTyped(typedChar, keyCode);
        } else if (keyCode == ESC) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Handles mouse input and dictates what element of the menu
     * gets that mouse input.
     * @throws IOException Thrown if there was a wrong input.
     */
    @Override
    public final void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.invokeScreen.handleMouseInput();
        switch (this.listFocus) {

            //:: The directory list is in focus
            // - Handle the mouse input of the directory list
            // - and only the directory list
            case DIRECTORY_LIST:
                this.directoryList.handleMouseInput();
                break;

            //:: The world list is in focus
            // - Handle the mouse input of the world list and
            // - only the world list
            case ENTRY_LIST:
                this.entrySelectionList.handleMouseInput();
                break;

            //:: Neither the directory list or the world list is in focus
            // - Do nothing because there is nothing to handle
            case BUTTONS:
                //:: This should not be reached
                break;
            default:
                //:: Do nothing because there is nothing to do
        }
    }

    /**
     * Handles the click of the mouse and determines which
     * menu element gets to handle the click.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param mouseButton The mouse button clicked.
     * @throws IOException Thrown if there is incorrect input.
     */
    @Override
    protected final void mouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseButton
    ) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        switch (this.listFocus) {
            case DIRECTORY_LIST:
                this.directoryList.mouseClicked(
                        mouseX,
                        mouseY,
                        mouseButton
                );
                break;
            case ENTRY_LIST:
                this.entrySelectionList.mouseClicked(
                        mouseX,
                        mouseY,
                        mouseButton
                );
                break;
            case BUTTONS:
                this.navBar.getPathNavDisplay()
                        .mouseClicked(mouseX, mouseY, mouseButton);
                break;
            default:
                //:: Do nothing because there is nothing to do
        }
    }

    /**
     * Handles mouse release input and dictates which element
     * of the menu gets the mouse release input.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param state The state of the mouse.
     */
    @Override
    protected final void mouseReleased(
            final int mouseX,
            final int mouseY,
            final int state
    ) {
        super.mouseReleased(mouseX, mouseY, state);

        if (this.directoryList.uniquePath().equals(MOPMLiterals.BASE_DIR)) {
            this.navBar.isPreviousButtonEnabled(false);
        } else {
            this.navBar.isPreviousButtonEnabled(true);
        }

        switch (this.listFocus) {

            //:: The folder list has been put into focus
            case DIRECTORY_LIST:
                this.directoryList.mouseReleased(mouseX, mouseY, state);
                this.navBar.getPathNavDisplay()
                        .setText(this.directoryList.currentPath());
                break;

            //:: The entry list has been put into focus
            case ENTRY_LIST:
                this.entrySelectionList.mouseReleased(mouseX, mouseY, state);
                break;

            //:: The entry list and folder list is not in focus
            case BUTTONS:
                //- There is nothing to handle when buttons are in focus
                break;

            default:
                //- This should not be reached
                break;
        }
    }

    /**
     * Confirms a click within the menu this selection menu controls.
     * @param result If the click was successful.
     * @param id The id of the element clicked.
     */
    @Override
    public final void confirmClicked(final boolean result, final int id) {
        this.invokeScreen.confirmClicked(result, id);
        this.mc.displayGuiScreen(this);
    }

    /**
     * Adjusts the size of the menu elements on screen resize.
     * @param mcIn The minecraft client.
     * @param w The new screen width.
     * @param h The new screen height.
     */
    @Override
    public final void onResize(final Minecraft mcIn, final int w, final int h) {
        final GuiTextField navTextField = this.navBar.getPathNavDisplay();

        super.onResize(mcIn, w, h);
        directoryList.setHeight(h);
        navTextField.width = (navTextField.width > SPACE_WIDTH)
                ? this.width - DIR_DISPLAY_PADDING
                : SPACE_WIDTH;
    }

    /**
     * Ready's the menu when the selection menu is closed.
     */
    @Override
    public final void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    //:: IFolderMenu
    //:::::::::::::::::::::::::::::://

    /**
     * @return Returns the directory list of this selection menu.
     */
    @Override
    public final DirectoryList<J> getDirectoryList() {
        return this.directoryList;
    }

    /**
     * Refreshes the entries to be displayed in the entry list.
     */
    @Override
    public final void refreshDirectoryEntryList() {
        this.entrySelectionList.refresh();
        this.entrySelectionList.display(
                this.directoryList
                        .getFolder()
                        .getEntries()
        );
    }

    //-----This:----------------------------------------//

    /**
     * @return Returns the menu the selection menu has control over.
     */
    public final K getInvokeScreen() {
        return this.invokeScreen;
    }

    /**
     * Sets the containing entry list.
     * @param containingList The entry list that this menu will supply.
     */
    public final void setContainingList(final L containingList) {
        this.entrySelectionList = containingList;
    }

    /**
     * Draws the default selection menu background.
     */
    private void drawOptionsBackground() {
        GuiUtils.drawTexturedRect(
                0.0D,
                (double) (this.height - BASE_64),
                (double) (this.width),
                (double) (this.height),
                this.zLevel + 1,
                BASE_64,
                BASE_64,
                BASE_64,
                COLOR_MAX,
                0,
                OPTIONS_BACKGROUND,
                this.mc
        );
    }

    /**
     * Gets the focus of the current menu element.
     * The focus dictates how input is handled.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @return Returns the menu focus.
     */
    private SelectedList getListFocus(final int mouseX, final int mouseY) {
        if (mouseY >= DIR_LIST_X && mouseY <= this.height - BASE_64) {
            if (mouseX >= 0 && mouseX <= this.directoryList.width) {
                return SelectedList.DIRECTORY_LIST;
            } else {
                return SelectedList.ENTRY_LIST;
            }
        }
        return SelectedList.BUTTONS;
    }
}

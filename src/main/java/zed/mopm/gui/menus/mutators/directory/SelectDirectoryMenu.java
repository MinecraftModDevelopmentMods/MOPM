package zed.mopm.gui.menus.mutators.directory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.gui.elements.base.GradientOverlay;
import zed.mopm.gui.elements.buttons.ButtonFactory;
import zed.mopm.gui.elements.buttons.ToolTipButton;
import zed.mopm.gui.elements.lists.DirectoryList;
import zed.mopm.util.MOPMLiterals;

import java.io.IOException;

import static zed.mopm.gui.utils.constants.ColorConsts.BACKGROUND_COLOR;
import static zed.mopm.gui.utils.constants.ButtonConsts.*;
import static zed.mopm.gui.utils.constants.KeyConsts.*;

public class SelectDirectoryMenu extends GuiScreen {

    //-----Consts:--------------------------------------//

    /**
     * The button id of the back button.
     */
    private static final int BACK_ID = 3;

    /**
     * The back button Y alignment.
     */
    private static final int BACK_Y_ALIGN = 3 + Y_ALIGN;
    /**
     * The back button x alignment.
     */
    private static final int BACK_X_ALIGN = 10;

    /**
     * The select button x location.
     */
    private static final int SELECT_X = 2 * SPACE_WIDTH;

    /**
     * The path display x location.
     */
    private static final int PATH_DISPLAY_X = 35;

    /**
     * The top location of the list.
     */
    private static final int GRADIENT_TOP = 32;
    /**
     * The bottom location of the list.
     */
    private static final int GRADIENT_BOTTOM = 64;

    /**
     * The left and right location of the list.
     */
    private static final int LIST_LEFT_RIGHT = 100;

    //-----Fields:--------------------------------------//

    /**
     * The menu accessing this menu.
     */
    private GuiScreen parentIn;
    /**
     * The entry that will have the directory selection applied to.
     */
    private IFolderPath applySelectionTo;
    /**
     * The list of available directories.
     */
    private DirectoryList directoryListIn;

    /**
     * This button takes you back a level in the directory path.
     */
    private ToolTipButton backBtn;
    /**
     * This button exits out of this menu.
     */
    private ToolTipButton exitBtn;
    /**
     * This button will confirm the directory selection.
     */
    private ToolTipButton confirmBtn;
    /**
     * This field displays the current directory location.
     */
    private GuiTextField pathDisplay;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new select directory menu.
     * @param parent The menu accessing this menu.
     * @param directoryList The list of available directories.
     */
    public SelectDirectoryMenu(
            final GuiScreen parent,
            final DirectoryList directoryList
    ) {
        this(parent, (IFolderPath) parent, directoryList);
    }

    /**
     * Creates a new select directory menu.
     * @param parent The menu accessing this menu.
     * @param modifyEntry The entry being modified by the directory selection.
     * @param directoryList The list of available directories.
     */
    public SelectDirectoryMenu(
            final GuiScreen parent,
            final IFolderPath modifyEntry,
            final DirectoryList directoryList
    ) {
        this.parentIn = parent;
        this.applySelectionTo = modifyEntry;
        this.directoryListIn = directoryList;
    }

    //-----Overridden Methods:--------------------------//

    //:: GuiScreen
    //:::::::::::::::::::::::::::::://

    /**
     * Initializes all of the GUI elements.
     */
    @Override
    public final void initGui() {
        Keyboard.enableRepeatEvents(true);

        final int selectX = this.width - SELECT_X;
        this.backBtn = new ButtonFactory(
                BACK_ID,
                BACK_X_ALIGN,
                (this.height - Y_OFFSET + BACK_Y_ALIGN)
        ).backButton();
        this.exitBtn = new ButtonFactory(
                EXIT_ID,
                (this.width - BUTTON_WIDTH - SPACE_WIDTH),
                (this.height - Y_OFFSET + Y_ALIGN)
        ).defaultButton(CANCEL, CANCEL);
        this.confirmBtn = new ButtonFactory(
                CREATE_ID,
                selectX,
                (this.height - Y_OFFSET + Y_ALIGN)
        ).defaultButton("Select", "Select");

        this.addButton(backBtn);
        this.addButton(exitBtn);
        this.addButton(confirmBtn);

        this.pathDisplay = new GuiTextField(
                1,
                this.fontRenderer,
                PATH_DISPLAY_X,
                (this.height - Y_OFFSET + Y_ALIGN),
                (selectX - PATH_DISPLAY_X - SPACE_WIDTH),
                BUTTON_HEIGHT
        );

        this.pathDisplay.setMaxStringLength(Integer.MAX_VALUE);
        this.pathDisplay.setText(this.directoryListIn.currentPath());
        this.backBtn.enabled =
                !this.directoryListIn.uniquePath()
                        .equals(MOPMLiterals.BASE_DIR);

        this.setDimensions();
    }

    /**
     * Handles the action of a button clicked on.
     * @param button The button that was clicked.
     */
    @Override
    protected final void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case EXIT_ID:
                this.mc.displayGuiScreen(this.parentIn);
                break;

            case BACK_ID:
                directoryListIn.back();
                break;

            case CREATE_ID:
                applySelectionTo.setPath(this.directoryListIn.currentPath());
                applySelectionTo.setUniquePath(
                        this.directoryListIn.uniquePath()
                );
                this.mc.displayGuiScreen(this.parentIn);
                break;

            default:
                // This should never be reached
                break;
        }
    }

    /**
     * Handles keyboard input.
     * @param typedChar The character of the typed key.
     * @param keyCode The code of the typed key.
     */
    @Override
    protected final void keyTyped(final char typedChar, final int keyCode) {
        if (keyCode == ESC) {
            actionPerformed(this.exitBtn);
        }
        if (keyCode == ENTER) {
            actionPerformed(confirmBtn);
        }
        if (
                this.pathDisplay.isFocused()
                        && keyCode == LEFT_ARROW
                        || keyCode == RIGHT_ARROW
                        || GuiScreen.isKeyComboCtrlA(keyCode)
                        || GuiScreen.isKeyComboCtrlC(keyCode)
        ) {
            this.pathDisplay.textboxKeyTyped(typedChar, keyCode);
        }
    }

    /**
     * Handles mouse input.
     * @throws IOException Thrown if there is improper input.
     */
    @Override
    public final void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.directoryListIn.handleMouseInput();
    }

    /**
     * Handles the click of the mouse.
     * @param mouseX The x location of the mouse click.
     * @param mouseY The y location of the mouse click.
     * @param mouseButton The mouse button pressed.
     * @throws IOException Thrown if there is invalid input.
     */
    @Override
    protected final void mouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseButton
    ) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.pathDisplay.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.pathDisplay.isFocused()) {
            this.directoryListIn.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    /**
     * Handles the release of a mouse button.
     * @param mouseX The x location of the mouse release.
     * @param mouseY The y location of the mouse release.
     * @param state The state of the mouse.
     */
    @Override
    protected final void mouseReleased(
            final int mouseX,
            final int mouseY,
            final int state
    ) {
        super.mouseReleased(mouseX, mouseY, state);
        this.directoryListIn.mouseReleased(mouseX, mouseY, state);
        this.pathDisplay.setText(this.directoryListIn.currentPath());
        this.backBtn.enabled =
                !this.directoryListIn.uniquePath()
                        .equals(MOPMLiterals.BASE_DIR);
    }

    /**
     * Draws all of the menu's screen elements.
     * @param mouseX The x location of the mouse.
     * @param mouseY The y location of the mouse.
     * @param partialTicks The partial game ticks.
     */
    @Override
    public final void drawScreen(
            final int mouseX,
            final int mouseY,
            final float partialTicks
    ) {
        super.drawDefaultBackground();
        drawGradientRect(
                0,
                GRADIENT_TOP,
                this.width,
                (this.height - GRADIENT_BOTTOM),
                BACKGROUND_COLOR,
                BACKGROUND_COLOR
        );
        this.directoryListIn.drawScreen(mouseX, mouseY, partialTicks);
        new GradientOverlay(this.width, this.height).draw(this);
        this.pathDisplay.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
        ButtonFactory.drawButtonToolTips(
                this.buttonList,
                this.mc,
                mouseX,
                mouseY,
                partialTicks
        );
    }

    /**
     * Rescales all of the menu elements.
     * @param mcIn The minecraft client.
     * @param w The new width of the screen.
     * @param h The new height of the screen.
     */
    @Override
    public final void onResize(final Minecraft mcIn, final int w, final int h) {
        this.parentIn.onResize(mcIn, w, h);
        this.width = w;
        this.height = h;

        this.setDimensions();
        this.setButtonDimensions();
    }

    /**
     * Ready's the menu for when the menu closes.
     */
    @Override
    public final void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Updates the counter of the cursor.
     */
    @Override
    public final void updateScreen() {
        this.pathDisplay.updateCursorCounter();
    }

    /**
     * Sets the dimensions of the directory list.
     */
    private void setDimensions() {
        this.directoryListIn.height = this.height;
        this.directoryListIn.top = GRADIENT_TOP;
        this.directoryListIn.bottom = this.height - GRADIENT_BOTTOM;
        this.directoryListIn.left = (this.width / 2) - LIST_LEFT_RIGHT;
        this.directoryListIn.right = (this.width / 2) + LIST_LEFT_RIGHT;
        this.directoryListIn.width = this.width
                - (this.width - this.directoryListIn.right);
    }

    /**
     * Sets the dimensions of all of the menu buttons.
     */
    private void setButtonDimensions() {
        final int selectX = this.width - SELECT_X;

        this.pathDisplay.width = (selectX - PATH_DISPLAY_X - SPACE_WIDTH);
        this.confirmBtn.x = selectX;
        this.exitBtn.x = (this.width - BUTTON_WIDTH - SPACE_WIDTH);

        final int yOffset = (this.height - Y_OFFSET + Y_ALIGN);
        this.confirmBtn.y = yOffset;
        this.exitBtn.y = yOffset;
        this.backBtn.y = this.height - Y_OFFSET + BACK_Y_ALIGN;
        this.pathDisplay.y = yOffset;
    }
}

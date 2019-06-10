package zed.mopm.gui.menus.mutators.directory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import zed.mopm.api.data.Editor;
import zed.mopm.api.data.IDrawableListEntry;
import zed.mopm.api.gui.lists.IModifiableList;
import zed.mopm.gui.elements.buttons.ContextButton;

import java.io.IOException;

import static zed.mopm.gui.utils.constants.KeyConsts.ENTER;

public class EditDirectoryMenu<
        T extends GuiListExtended
                & IModifiableList
        > extends GuiScreen {

    //-----Consts:--------------------------------------//

    /**
     * The text label for the delete context button.
     */
    private static final String DELETE_LABEL = "Delete";
    /**
     * The text label for the rename context button.
     */
    private static final String RENAME_LABEL = "Rename";
    /**
     * The text label for the move context button.
     */
    private static final String MOVE_LABEL = "Move";

    /**
     * The Y offset of the rename context button.
     */
    private static final int BTN_Y_1 = 10;
    /**
     * The Y offset of teh move context button.
     */
    private static final int BTN_Y_2 = 20;

    //-----Fields:--------------------------------------//

    /**
     * The parent screen that is accessing this menu.
     */
    private GuiScreen parentIn;
    /**
     * The clicked on entry index.
     */
    private int entryIndex;
    /**
     * Determines if this menu can close.
     */
    private boolean canClose;
    /**
     * The selection list to get entries from.
     */
    private T modifiableList;

    /**
     * Deletes the selected entry when clicked.
     */
    private ContextButton delete;
    /**
     * Renames the selected entry when clicked.
     */
    private ContextButton rename;
    /**
     * Moves the selected entry to a different directory when clicked.
     */
    private ContextButton move;

    /**
     * The text filed that appears when the rename context button is clicked.
     */
    private GuiTextField changeName;

    //-----Constructors:--------------------------------//

    /**
     * Creates an overlay menu for the context buttons when clicking on
     * an entry that is apart of a some modifiable list.
     * @param parent The accessing menu.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param doClose Determines if the menu will close.
     * @param list The list that entries will be accessed from.
     */
    public EditDirectoryMenu(
            final GuiScreen parent,
            final int mouseX,
            final int mouseY,
            final boolean doClose,
            final T list
    ) {
        this.parentIn = parent;
        this.modifiableList = list;
        this.entryIndex =
                list.getSlotIndexFromScreenCoords(
                        mouseX,
                        mouseY
                );
        this.canClose = doClose;

        delete = new ContextButton(
                Editor.DELETE,
                mouseX,
                mouseY,
                DELETE_LABEL
        );
        rename = new ContextButton(
                Editor.RENAME,
                mouseX,
                mouseY + BTN_Y_1,
                RENAME_LABEL
        );
        move = new ContextButton(
                Editor.CHANGE_DIRECTORY,
                mouseX,
                mouseY + BTN_Y_2,
                MOVE_LABEL
        );

        IDrawableListEntry temp =
                (IDrawableListEntry) list.getListEntry(entryIndex);
        changeName = new GuiTextField(
                0,
                Minecraft.getMinecraft().fontRenderer,
                temp.getX(),
                temp.getY(),
                list.getListWidth(),
                list.getSlotHeight()
        );
        changeName.setMaxStringLength(Integer.MAX_VALUE);
        changeName.setText(temp.getEntryText());
        changeName.setVisible(false);
    }

    /**
     * A shortened version of the main constructor.
     * @param parent The accessing parent menu.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param list The list that entries will be accessed from.
     */
    public EditDirectoryMenu(
            final GuiScreen parent,
            final int mouseX,
            final int mouseY,
            final T list
    ) {
        this(parent, mouseX, mouseY, true, list);
    }

    //-----Overridden Methods:--------------------------//

    //:: GuiScreen
    //:::::::::::::::::::::::::::::://

    /**
     * Adds all of the buttons to the menu.
     */
    @Override
    public final void initGui() {
        this.addButton(this.delete);
        this.addButton(this.rename);
        this.addButton(this.move);
    }

    /**
     * Draws the edit directory menu.
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
        this.parentIn.drawScreen(mouseX, mouseY, partialTicks);

        if (this.changeName.getVisible()) {
            this.changeName.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Handles the action of a clicked button.
     * @param button The button that was clicked.
     */
    @Override
    protected final void actionPerformed(final GuiButton button) {
        switch (((ContextButton) button).getFunction()) {
            case DELETE:
                this.modifiableList.delete(this.entryIndex);
                if (canClose) {
                    closeGui();
                }
                break;

            case RENAME:
                this.changeName.setVisible(true);
                this.changeName.setFocused(true);
                break;

            case CHANGE_DIRECTORY:
                this.modifiableList.changeDir(this.entryIndex);
                break;
            default:
                // Do nothing because there is nothing to do.
        }

        this.delete.visible = false;
        this.rename.visible = false;
        this.move.visible = false;
    }

    /**
     * Handles keyboard input.
     * Pressing enter will close the menu and confirm the new name
     * of an entry if there are any valid changes to the name.
     * @param typedChar The character of the key typed.
     * @param keyCode The code of the key typed.
     */
    @Override
    protected final void keyTyped(final char typedChar, final int keyCode) {
        if (keyCode == ENTER) {
            closeGuiOnTextbox();
        }
        if (this.changeName.isFocused()) {
            this.changeName.textboxKeyTyped(typedChar, keyCode);
        }
    }

    /**
     * Handles mouse clicking inputs.
     * @param mouseX The x position of the mouse click.
     * @param mouseY The y position of the mouse click.
     * @param mouseButton The mouse button that was clicked.
     * @throws IOException Thrown if there is invalid input.
     */
    @Override
    protected final void mouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseButton
    ) throws IOException {
        if (
                !(this.delete.mousePressed(this.mc, mouseX, mouseY)
                        || this.rename.mousePressed(this.mc, mouseX, mouseY)
                        || this.move.mousePressed(this.mc, mouseX, mouseY)
                )
        ) {
            closeGuiOnTextbox();
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    /**
     * Does nothing because this menu does not have any background.
     */
    @Override
    public void drawDefaultBackground() {
        // Do not draw any background
    }

    /**
     * Realizes the parent menu.
     * @param mcIn The minecraft client.
     * @param w The new width of the screen.
     * @param h The new height of the screen.
     */
    @Override
    public final void onResize(final Minecraft mcIn, final int w, final int h) {
        this.parentIn.onResize(mcIn, w, h);
    }

    /**
     * Updates screen elements: <br>
     * Screen elements include the cursor of the text field.
     */
    @Override
    public final void updateScreen() {
        this.changeName.updateCursorCounter();
    }

    //-----This:----------------------------------------//

    /**
     * Closes the GUI and commits any changes to entry names
     * if the text field has a proper input.
     */
    private void closeGuiOnTextbox() {
        if (!this.changeName.getText().isEmpty()) {
            this.modifiableList.rename(
                    this.entryIndex,
                    this.changeName.getText()
            );
        }
        closeGui();
    }

    /**
     * Closes this menu.
     */
    private void closeGui() {
        this.mc.displayGuiScreen(parentIn);
    }

}

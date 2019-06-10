package zed.mopm.gui.menus.mutators.entries;

import jline.internal.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.api.gui.mutators.CreatorMenu;
import zed.mopm.api.gui.mutators.ICreatorMenu;
import zed.mopm.gui.elements.lists.DirectoryList;
import zed.mopm.gui.menus.mutators.directory.SelectDirectoryMenu;

import java.io.File;
import java.io.IOException;

import static zed.mopm.gui.utils.constants.KeyConsts.*;

public class CreateEntryMenu<
        K extends GuiScreen & ICreatorMenu,
        L extends IFolderPath
        >
        extends GuiScreen
        implements IFolderPath {

    //-----Constants:-----------------------------------//

    /**
     * The id of the select button.
     */
    protected static final int SELECTION_ID = 100;
    /**
     * The select button x location.
     */
    protected static final int SELECT_X = 100;
    /**
     * The select button width.
     */
    protected static final int SELECT_WIDTH = 45;
    /**
     * The path display width.
     */
    protected static final int PATH_WIDTH = 150;
    /**
     * The width of the path display.
     */
    private static final int PATH_DISPLAY_WIDTH = 50;

    //-----Fields:--------------------------------------//

    /**
     * The menu type that is being invoked.
     * This can either be CreateServerMenu or
     * CreateWorldMenu. This class contains common
     * code that is shared between these two classes.
     */
    private K invokeCreator;
    /**
     * The selection menu that will be used to select a directory.
     */
    private SelectDirectoryMenu selectionMenu;

    /**
     * The path display that diaplays the selected path
     * to store the world or server in.
     */
    private GuiTextField pathDisplay;
    /**
     * The button that goes to the SelectDirectoryMenu.
     */
    private GuiButtonExt selectBtn;
    /**
     * The directory path.
     */
    private String savePath;
    /**
     * The save file for the directory path.
     */
    private File mopmSaveFile;

    //-----Constructors:--------------------------------//

    /**
     * Creates the CreateEntryMenu that invokes the its
     * invoker menu.
     * @param invokerIn The menu this menu has control over.
     * @param directoryList The list of available directories.
     */
    public CreateEntryMenu(
            final K invokerIn,
            final DirectoryList<L> directoryList
    ) {
        this.invokeCreator = invokerIn;
        this.selectionMenu = new SelectDirectoryMenu(this, directoryList);
        this.pathDisplay = this.invokeCreator.getPathDisplay();
        this.pathDisplay.setMaxStringLength(Integer.MAX_VALUE);
        this.mopmSaveFile = null;

        this.setPath(directoryList.currentPath());
        this.setUniquePath(directoryList.uniquePath());
    }

    //-----Overridden Methods:--------------------------//

    //:: GuiScreen
    //:::::::::::::::::::::::::::::://

    /**
     * Initializes all of the menu elements and invoker menu elements.
     */
    @Override
    public final void initGui() {
        this.buttonList.clear();
        this.invokeCreator.setWorldAndResolution(
                this.mc,
                this.width,
                this.height
        );
        this.invokeCreator.initGui();
        Keyboard.enableRepeatEvents(true);

        this.pathDisplay.x = this.width / 2 - PATH_DISPLAY_WIDTH;
        this.selectBtn = this.invokeCreator.getPathSelectButton(this.width);
        this.addButton(selectBtn);
        for (final GuiButton btn : this.invokeCreator.getButtons()) {
            this.addButton(btn);
        }
    }

    /**
     * Draws the menu elements and the invoker menu elements.
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
        this.invokeCreator.drawScreen(mouseX, mouseY, partialTicks);
        this.pathDisplay.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Handles the action of the the button clicked.
     * If the button clicked is the select button, then
     * the SelectDirectoryMenu is open.
     * @param btn The button clicked.
     * @throws IOException Thrown if there is invalid input.
     */
    @Override
    protected final void actionPerformed(
            final GuiButton btn
    ) throws IOException {
        if (btn.id == SELECTION_ID) {
            this.mc.displayGuiScreen(this.selectionMenu);
        } else {
            this.invokeCreator.handleActionPerformed(btn, this);
        }
    }

    /**
     * Handles keyboard input.
     * ENTER: Creates the server or world.
     * RIGHT ARROW: moves the cursor through the path display.
     * LEFT ARROW: moves the cursor through the path display.
     * @param typedChar The character of the key typed.
     * @param keyCode The code of the key typed.
     * @throws IOException Thrown if there is invalid input.
     */
    @Override
    protected final void keyTyped(
            final char typedChar,
            final int keyCode
    ) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.invokeCreator.doKeyTyped(typedChar, keyCode);
        if (keyCode == ENTER || keyCode == NUMPAD_ENTER) {
            this.invokeCreator.handleActionPerformed(
                    this.buttonList
                            .stream()
                            .filter(btn -> btn.id == CreatorMenu.CREATION_ID)
                            .findFirst()
                            .get(),
                    this
            );
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
     * Handles mouse click inputs.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param mouseButton The mouse button pressed.
     * @throws IOException thrown if there is invalid input.
     */
    @Override
    protected final void mouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseButton
    ) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.invokeCreator.doMouseClicked(mouseX, mouseY, mouseButton);
        this.pathDisplay.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Realizes menu components.
     * @param mcIn The minecraft client.
     * @param w The new screen width.
     * @param h The new screen height.
     */
    @Override
    public final void onResize(final Minecraft mcIn, final int w, final int h) {
        super.onResize(mcIn, w, h);
        this.invokeCreator.onResize(mcIn, w, h);
        this.width = w;
        this.height = h;
    }

    //:: IFolderPath
    //:::::::::::::::::::::::::::::://

    /**
     * Sets the path of the path display.
     * @param path The path to be displayed.
     */
    @Override
    public final void setPath(final String path) {
        this.pathDisplay.setText("Dir: " + path);
    }

    /**
     * Sets the unique save path.
     * @param path The path to save the created entry to.
     */
    @Override
    public final void setUniquePath(final String path) {
        this.savePath = path;
    }

    /**
     * @return Returns the path to the directory the entry will be saved in.
     */
    @Override
    public final String getPathToDir() {
        return this.pathDisplay.getText().substring("Dir: ".length());
    }

    /**
     * @return Returns the save file where the path is saved to.
     */
    @Override
    @Nullable
    public final File getMopmSaveFile() {
        return this.mopmSaveFile;
    }

    //-----This:----------------------------------------//

    /**
     * Sets the save file.
     * @param file The file to save the entry directory path to.
     */
    public final void setMopmSaveFile(final File file) {
        this.mopmSaveFile = file;
    }

    /**
     * @return Returns the unique save path to the created entry.
     */
    public final String getSavePath() {
        return this.savePath;
    }

    /**
     * Toggles the display of the selected path.
     */
    public final void toggleDisplay() {
        this.pathDisplay.setVisible(!this.pathDisplay.getVisible());
        this.selectBtn.visible = !this.selectBtn.visible;
    }
}

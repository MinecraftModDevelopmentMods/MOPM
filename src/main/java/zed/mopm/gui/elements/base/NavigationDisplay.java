package zed.mopm.gui.elements.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import zed.mopm.gui.elements.buttons.ButtonFactory;
import zed.mopm.gui.elements.buttons.ToolTipButton;

import java.util.ArrayList;
import java.util.List;

import static zed.mopm.gui.utils.constants.MenuConsts.*;
import static zed.mopm.gui.utils.constants.ButtonConsts.SPACE_WIDTH;

public class NavigationDisplay {

    //-----Fields:--------------------------------------//

    /**
     * The list of navigation buttons in the nav bar.
     */
    private List<GuiButton> navButtons = new ArrayList<>();
    /**
     * Nav Button: used to create a directory.
     */
    private GuiButtonExt createDirectoryBtn;
    /**
     * Nav Button: used to traverse backwards in the
     * directory tree.
     */
    private GuiButtonExt previousDirectoryBtn;
    /**
     * Nav Button: used to force save the directory list.
     */
    private GuiButtonExt saveDirectoryListBtn;
    /**
     * Nav Button: used to hide the path display.
     */
    private GuiButtonExt hideDirectoryPathBtn;
    /**
     * Nav text field: used to display the current directory path.
     */
    private GuiTextField pathNavDisplay;

    //-----Constructors:----------------------------------//

    /**
     * Creates a new navigation display.
     * @param clientIn Used to get the font renderer
     *                 and display width.
     */
    public NavigationDisplay(final Minecraft clientIn) {
        this.createDirectoryBtn = new ButtonFactory(
                SUBDIR_BTN_ID,
                (BACK_BTN_X + BACK_BTN_WIDTH),
                HEADER_BUTTON_Y
        ).createDirectoryButton();
        this.previousDirectoryBtn = new ButtonFactory(
                BACK_BTN_ID,
                BACK_BTN_X,
                HEADER_BUTTON_Y
        ).backButton();
        this.saveDirectoryListBtn = new ButtonFactory(
                SAVE_BTN_ID,
                SAVE_BTN_X,
                HEADER_BUTTON_Y
        ).saveButton();
        this.hideDirectoryPathBtn = new ButtonFactory(
                HIDE_BTN_ID,
                HIDE_BTN_X,
                HIDE_BTN_Y
        ).hidePathButton();

        this.pathNavDisplay = new GuiTextField(
                1,
                clientIn.fontRenderer,
                DIR_DISPLAY_X,
                DIR_DISPLAY_Y,
                clientIn.displayWidth - DIR_DISPLAY_PADDING,
                DIR_DISPLAY_HEIGHT
        );
        this.pathNavDisplay.setMaxStringLength(Integer.MAX_VALUE);

        this.navButtons.add(this.createDirectoryBtn);
        this.navButtons.add(this.previousDirectoryBtn);
        this.navButtons.add(this.saveDirectoryListBtn);
        this.navButtons.add(this.hideDirectoryPathBtn);
    }

    //-----This:----------------------------------------//

    /**
     * Draws the path navigation text field.
     */
    public void draw() {
        this.pathNavDisplay.drawTextBox();
    }

    /**
     * Updates teh path navigation display.
     * @param width The new width of the display.
     * @param displayText The new text to display.
     */
    public void updateNavDisplay(final int width, final String displayText) {
        this.pathNavDisplay.width = width - DIR_DISPLAY_PADDING;
        this.pathNavDisplay.setText(displayText);
    }

    /**
     * Toggles the path navigation display.
     * @param width The new width of the text display.
     * @param displayText The new text to display.
     */
    public void toggleNavDisplay(final int width, final String displayText) {
        if (this.pathNavDisplay.width > SPACE_WIDTH) {
            ((ToolTipButton) this.hideDirectoryPathBtn).setToolTip("Unhide");
            this.pathNavDisplay.width = SPACE_WIDTH;
            this.pathNavDisplay.setText("");
        } else {
            ((ToolTipButton) this.hideDirectoryPathBtn).setToolTip("Hide");
            this.pathNavDisplay.width = width - DIR_DISPLAY_PADDING;
            this.pathNavDisplay.setText(displayText);
        }
    }

    /**
     * Sets whether or not the previous button is enabled.
     * @param isEnabled True to enable : False to disable.
     */
    public void isPreviousButtonEnabled(final boolean isEnabled) {
        this.previousDirectoryBtn.enabled = isEnabled;
    }

    /**
     * @return Returns the text field to get appropriate data.
     */
    public GuiTextField getPathNavDisplay() {
        return this.pathNavDisplay;
    }

    /**
     * @return Returns the list of navigation buttons.
     */
    public List<GuiButton> getNavButtons() {
        return this.navButtons;
    }
}

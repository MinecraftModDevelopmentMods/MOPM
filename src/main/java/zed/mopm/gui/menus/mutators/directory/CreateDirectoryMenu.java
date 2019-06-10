package zed.mopm.gui.menus.mutators.directory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import zed.mopm.api.gui.IFolderMenu;
import zed.mopm.gui.elements.base.GradientOverlay;
import zed.mopm.gui.elements.buttons.ButtonFactory;
import zed.mopm.gui.elements.buttons.ToolTipButton;
import zed.mopm.gui.utils.constants.ColorConsts;
import zed.mopm.gui.utils.GuiUtils;

import java.io.IOException;

import static zed.mopm.gui.utils.constants.ColorConsts.BACKGROUND_COLOR;
import static zed.mopm.gui.utils.constants.ColorConsts.COLOR_MAX;
import static zed.mopm.gui.utils.constants.ButtonConsts.*;
import static zed.mopm.gui.utils.constants.KeyConsts.ENTER;
import static zed.mopm.gui.utils.constants.KeyConsts.ESC;

public class CreateDirectoryMenu extends GuiScreen {

    //-----Consts:--------------------------------------//

    /**
     * The label of the text field.
     */
    private static final String TEXT_FIELD_LABEL = "Directory Name:";
    /**
     * The max length of the string the text field can hold.
     */
    private static final int MAX_STRING = 100;
    /**
     * The color of the background.
     */
    private static final int RGB = 64;
    /**
     * The offset height of the background.
     */
    private static final int BACKGROUND_OFFSET = 64;

    //-----Fields:--------------------------------------//

    /**
     * The parent screen accessing this menu.
     */
    private GuiScreen parentIn;

    /**
     * The text field that gets the directory name from the player.
     */
    private GuiTextField directoryNameInquiry;
    /**
     * The button that exits out of this menu.
     */
    private ToolTipButton cancel;
    /**
     * The button that creates the new folder.
     */
    private ToolTipButton createFolder;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new directory creator menu.
     * @param parent The screen accessing this menu.
     */
    public CreateDirectoryMenu(final GuiScreen parent) {
        this.parentIn = parent;
    }

    //-----Overridden Methods:--------------------------//

    //:: GuiScreen
    //:::::::::::::::::::::::::::::://

    /**
     * Initializes the menu elements.
     */
    @Override
    public final void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.cancel = new ButtonFactory(
                EXIT_ID,
                EXIT_X,
                (this.height - Y_OFFSET + Y_ALIGN)
        ).defaultButton(CANCEL, CANCEL);
        this.createFolder = new ButtonFactory(
                CREATE_ID,
                SELECT_X,
                (this.height - Y_OFFSET + Y_ALIGN)
        ).defaultButton(CREATE, CREATE);
        this.createFolder.enabled = false;
        this.addButton(cancel);
        this.addButton(createFolder);

        this.directoryNameInquiry = new GuiTextField(
                1,
                this.fontRenderer,
                STARTING_X,
                (this.height - Y_OFFSET + Y_ALIGN),
                TEXT_FIELD_WIDTH,
                BUTTON_HEIGHT
        );
        this.directoryNameInquiry.setFocused(true);
        this.directoryNameInquiry.setText("");
        this.directoryNameInquiry.setMaxStringLength(MAX_STRING);
        super.initGui();
    }

    /**
     * Handles the action of the button that was pressed.
     * @param button The button that was pressed.
     */
    @Override
    protected final void actionPerformed(final GuiButton button) {
        if (button.id == CREATE_ID) {
            ((IFolderMenu) parentIn)
                    .getDirectoryList()
                    .addFolder(this.directoryNameInquiry.getText());
        }

        this.mc.displayGuiScreen(this.parentIn);
    }

    /**
     * Handles keys typed. If the key is enter, try and create a folder.
     * If the key is escape, try and cancel the menu.
     * Otherwise, just type in the text box.
     * @param typedChar The character of the key typed.
     * @param keyCode The code of the typed key.
     */
    @Override
    protected final void keyTyped(
            final char typedChar,
            final int keyCode
    ) {
        switch (keyCode) {
            case ESC:
                this.mc.displayGuiScreen(this.parentIn);
                break;
            case ENTER:
                if (this.createFolder.enabled) {
                    this.actionPerformed(this.createFolder);
                }
                break;
            default:
                if (this.directoryNameInquiry.isFocused()) {
                    this.directoryNameInquiry.textboxKeyTyped(
                            typedChar,
                            keyCode
                    );
                    this.createFolder.enabled =
                            !this.directoryNameInquiry.getText().isEmpty();
                }
        }
    }

    /**
     * Handles mouse input dependant of what screen element
     * was clicked.
     * @param mouseX The x location of the mouse click.
     * @param mouseY The y location of the mouse click.
     * @param mouseButton The mouse button clicked.
     * @throws IOException Thrown if there was improper input.
     */
    @Override
    protected final void mouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseButton
    ) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.directoryNameInquiry.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Draws the menu elements.
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
        parentIn.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();
        this.drawString(
                this.fontRenderer,
                TEXT_FIELD_LABEL,
                STARTING_X,
                (this.height - Y_OFFSET + TEXT_OFFSET),
                ColorConsts.PURE_WHITE
        );

        this.directoryNameInquiry.drawTextBox();
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
     * Draws the background of the menu.
     */
    @Override
    public final void drawDefaultBackground() {
        //:: Background
        this.drawGradientRect(
                0,
                0,
                this.width,
                this.height,
                BACKGROUND_COLOR,
                BACKGROUND_COLOR
        );
        GuiUtils.drawTexturedRect(
                0.0D,
                (double) (this.height - BACKGROUND_OFFSET),
                (double) (this.width),
                (double) this.height,
                ((double) this.zLevel - 1),
                RGB,
                RGB,
                RGB,
                COLOR_MAX,
                0,
                OPTIONS_BACKGROUND,
                this.mc
        );

        new GradientOverlay(this.width, this.height).draw(this);
    }

    /**
     * Scales element items based on the new size of the window.
     * @param mcIn The minecraft client.
     * @param w The new screen width.
     * @param h The new screen height.
     */
    @Override
    public final void onResize(final Minecraft mcIn, final int w, final int h) {
        this.parentIn.onResize(mcIn, w, h);
        this.width = w;
        this.height = h;

        int yAlign = this.height - Y_OFFSET + Y_ALIGN;
        this.cancel.y = yAlign;
        this.createFolder.y = yAlign;
        this.directoryNameInquiry.y = yAlign;
    }

    @Override
    public final void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public final void updateScreen() {
        this.directoryNameInquiry.updateCursorCounter();
    }
}

package zed.mopm.gui.menus.mutators.entries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import zed.mopm.api.data.ServerDataStatus;
import zed.mopm.api.gui.mutators.CreatorMenu;
import zed.mopm.api.gui.mutators.ICreatorMenu;
import zed.mopm.data.ServerSaveData;

import java.io.IOException;
import java.net.IDN;
import java.util.List;

import static zed.mopm.gui.menus.mutators.entries.CreateEntryMenu.*;
import static zed.mopm.gui.utils.constants.ButtonConsts.BUTTON_HEIGHT;
import static zed.mopm.gui.utils.constants.ButtonConsts.TEXT_FIELD_WIDTH;
import static zed.mopm.gui.utils.constants.KeyConsts.TAB;

public class CreateServerMenu
        extends GuiScreenAddServer
        implements ICreatorMenu {

    //-----Constants:-----------------------------------//

    /**
     * The resource button id.
     * This is not used other than to prevent the button
     * action from doing anything in the super class.
     */
    private static final int RESOURCE_BTN_ID = 2;
    /**
     * The y position of this menu's elements.
     */
    private static final int Y_POS = 30;
    /**
     * The y position of the name field.
     */
    private static final int NAME_Y = 66;
    /**
     * The y position of the ip field.
     */
    private static final int IP_Y = 106;
    /**
     * The max character length of the IP field.
     */
    private static final int IP_LENGTH = 128;

    //-----Fields:--------------------------------------//

    /**
     * The menu accessing this menu.
     */
    private GuiScreen parentIn;

    /**
     * The save data that is being created.
     */
    private ServerSaveData saveData;
    /**
     * The ip of the save data.
     */
    private GuiTextField ipField;
    /**
     * The name of the save data.
     */
    private GuiTextField nameField;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new create server menu that allows players
     * to create server entries.
     * @param parentScreenIn The menu accessing this menu.
     * @param serverDataIn The server data being created.
     */
    public CreateServerMenu(
            final GuiScreen parentScreenIn,
            final ServerSaveData serverDataIn
    ) {
        super(parentScreenIn, serverDataIn.getServerData());
        this.parentIn = parentScreenIn;

        this.saveData = serverDataIn;
        this.mc = parentScreenIn.mc;
        this.fontRenderer = this.mc.fontRenderer;
    }

    //-----Overridden Methods:--------------------------//

    //:: GuiScreenAddServer
    //:::::::::::::::::::::::::::::://

    /**
     * Initializes all of the screen elements.
     */
    @Override
    public final void initGui() {
        super.initGui();
        this.nameField = new GuiTextField(
                0,
                this.fontRenderer,
                (this.width / 2 - SELECT_X),
                NAME_Y,
                TEXT_FIELD_WIDTH,
                BUTTON_HEIGHT
        );
        this.nameField.setFocused(true);
        this.nameField.setText(this.saveData.getServerData().serverName);

        this.ipField = new GuiTextField(
                1,
                this.fontRenderer,
                (this.width / 2 - SELECT_X),
                IP_Y,
                TEXT_FIELD_WIDTH,
                BUTTON_HEIGHT
        );
        this.ipField.setMaxStringLength(IP_LENGTH);
        this.ipField.setText(this.saveData.getServerData().serverIP);

        //:: This validator code has been taken from the
        // - validator predicate in the GuiScreenAddServer class.
        this.ipField.setValidator(validator -> {
            if (StringUtils.isNullOrEmpty(validator)) {
                return true;
            } else {
                String[] astring = validator.split(":");

                if (astring.length == 0) {
                    return true;
                } else {
                    try {
                        IDN.toASCII(astring[0]);
                        return true;
                    } catch (IllegalArgumentException var4) {
                        return false;
                    }
                }
            }
        });
    }

    /**
     * Draws all of the screen elements.
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
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.nameField.drawTextBox();
        this.ipField.drawTextBox();
    }

    //:: ICreatorMenu
    //:::::::::::::::::::::::::::::://

    /**
     * @return Returns the directory display path that will be drawn in the
     * CreateEntryMenu.
     */
    @Override
    public final GuiTextField getPathDisplay() {
        return new GuiTextField(
                1,
                Minecraft.getMinecraft().fontRenderer,
                0,
                Y_POS,
                PATH_WIDTH,
                BUTTON_HEIGHT
        );
    }

    /**
     * @param screenWidth The width of the screen.
     * @return Returns the button that will be drawn in the
     * CreateEntryMenu. This button opens the SelectDirectoryMenu.
     */
    @Override
    public final GuiButtonExt getPathSelectButton(final int screenWidth) {
        return new GuiButtonExt(
                SELECTION_ID,
                (screenWidth / 2 - SELECT_X),
                Y_POS,
                SELECT_WIDTH,
                BUTTON_HEIGHT,
                "Select"
        );
    }

    /**
     * @return Returns the list of buttons that this menu owns.
     */
    @Override
    public final List<GuiButton> getButtons() {
        return this.buttonList;
    }

    /**
     * Replaces {@link #actionPerformed(GuiButton)}.
     * Handles the action of the button pressed.
     * @param btn The button clicked on.
     * @param entryMenu The create entry menu that invoked this menu.
     * @throws IOException Thrown if there is invalid input.
     */
    @Override
    public final void handleActionPerformed(
            final GuiButton btn,
            final CreateEntryMenu entryMenu
    ) throws IOException {
        final ServerData server = this.saveData.getServerData();
        final ServerDataStatus status = this.saveData.getStatus();
        this.saveData.changeStatus(ServerDataStatus.NONE);
        super.actionPerformed(btn);
        this.saveData.changeStatus(status);

        switch (btn.id) {
            case CreatorMenu.CREATION_ID:
                server.serverName = this.nameField.getText();
                server.serverIP = this.ipField.getText();
                this.saveData.setSavePath(entryMenu.getSavePath());
                this.parentIn.confirmClicked(true, 0);
                break;

            case RESOURCE_BTN_ID:
                //:: Do nothing! We dont want to execute the default case.
                // - ID 2 is handled by the super actionPerformed call.
                break;

            default:
                this.mc.displayGuiScreen(this.parentIn);
                break;
        }
    }

    /**
     * Replaces {@link #keyTyped(char, int)}.
     * Handles the keyboard input.
     * @param typedChar The character of the key typed.
     * @param keyCode The code of the key typed.
     */
    @Override
    public void doKeyTyped(final char typedChar, final int keyCode) {
        if (keyCode == TAB) {
            final boolean oldFocus = this.nameField.isFocused();
            final boolean newFocus = oldFocus;
            this.nameField.setFocused(!oldFocus);
            this.ipField.setFocused(newFocus);
        } else {
            this.nameField.textboxKeyTyped(typedChar, keyCode);
            this.ipField.textboxKeyTyped(typedChar, keyCode);
        }

        this.buttonList.get(0).enabled =
                !this.ipField.getText().isEmpty()
                        && this.ipField.getText().split(":").length > 0
                        && !this.nameField.getText().isEmpty();
    }

    /**
     * Replaces {@link #mouseClicked(int, int, int)}.
     * Handles the click of the mouse.
     * @param mouseX The x location of the mouse click.
     * @param mouseY The y location of the mouse click.
     * @param mouseButton The mouse button clicked.
     */
    @Override
    public void doMouseClicked(
            final int mouseX,
            final int mouseY,
            final int mouseButton
    ) {
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.ipField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Replaces {@link #mouseReleased(int, int, int)}.
     * Handles the release of the mouse.
     * @param mouseX The x location of the mouse release.
     * @param mouseY The y location of the mouse release.
     * @param state The state of the mouse.
     */
    @Override
    public void doMouseReleased(
            final int mouseX,
            final int mouseY,
            final int state
    ) {
        this.mouseReleased(mouseX, mouseY, state);
    }
}

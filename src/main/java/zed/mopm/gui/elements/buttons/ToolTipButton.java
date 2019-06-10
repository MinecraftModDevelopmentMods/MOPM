package zed.mopm.gui.elements.buttons;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import zed.mopm.gui.utils.GuiUtils;

public class ToolTipButton extends GuiButtonExt {

    //-----Consts:--------------------------------------//

    /**
     * Locates the tool tip x y 3 units away from the cursor.
     */
    private static final int TOOL_TIP_OFFSET = 3;
    /**
     * Adds an addition height of 5 to the tool tip height.
     */
    private static final int TOOL_TIP_HEIGHT = 5;

    //-----Fields:--------------------------------------//

    /**
     * The tool tip text.
     */
    private String toolTipText;

    //-----Constructors:--------------------------------//

    /**
     * Creates a Tool tip button.
     * @param id The id of the tool tip button.
     * @param xPos The x location of the tool tip button.
     * @param yPos The y location of the tool tip button.
     * @param width The width of the tool tip button.
     * @param height The height of the tool tip button.
     * @param displayString The tool tip button label.
     * @param hoverTextIn The tool tip text.
     */
    public ToolTipButton(
            final int id,
            final int xPos,
            final int yPos,
            final int width,
            final int height,
            final String displayString,
            final String hoverTextIn
    ) {
        super(
                id,
                xPos,
                yPos,
                width,
                height,
                displayString
        );
        this.toolTipText = hoverTextIn;
    }

    //-----This:----------------------------------------//

    /**
     * Draws the tool tip of this button when the button is
     * being hovered over.
     * @param mc The minecraft client.
     * @param mouseX The x location of the mouse.
     * @param mouseY The y location of the mouse.
     */
    public final void drawHoverState(
            final Minecraft mc,
            final int mouseX,
            final int mouseY
    ) {
        if (this.visible) {
            GuiUtils.drawToolTip(
                    mc.fontRenderer,
                    toolTipText,
                    mouseX + TOOL_TIP_OFFSET,
                    mouseY - TOOL_TIP_OFFSET,
                    mc.fontRenderer.getStringWidth(toolTipText),
                    mc.fontRenderer.FONT_HEIGHT + TOOL_TIP_HEIGHT
            );
        }
    }

    /**
     * Sets the tool tip of the button.
     * @param toolTip The new tool tip text.
     */
    public final void setToolTip(final String toolTip) {
        this.toolTipText = toolTip;
    }
}

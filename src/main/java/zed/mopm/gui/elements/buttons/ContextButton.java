package zed.mopm.gui.elements.buttons;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import zed.mopm.api.data.Editor;

import static zed.mopm.gui.utils.constants.ColorConsts.*;

public class ContextButton extends GuiButtonExt {

    //-----Consts:--------------------------------------//

    /**
     * The default width of the button.
     */
    private static final int DEFAULT_WIDTH = 55;
    /**
     * The default height of the button.
     */
    private static final int DEFAULT_HEIGHT = 10;

    /**
     * The button text y modifier.
     */
    private static final float TEXT_Y_MOD = 2f;
    /**
     * The button text position modifier.
     */
    private static final float TEXT_POS_MOD = 3f;

    //-----Fields:--------------------------------------//

    /**
     * The function of the button.
     */
    private Editor function;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new button with a functional context.
     * @param e The editor function of this button.
     * @param xPos The x position of this button.
     * @param yPos The y position of this button.
     * @param displayString The button text.
     */
    public ContextButton(
            final Editor e,
            final int xPos,
            final int yPos,
            final String displayString
    ) {
        super(
                e.ordinal(),
                xPos,
                yPos,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                displayString
        );
        function = e;
    }

    //-----GuiButtonExt:--------------------------------//

    /**
     * Draws the context button.
     * @param mc The minecraft client.
     * @param mouseX The x location of the mouse.
     * @param mouseY The y location of the mouse.
     * @param partial The partial game ticks.
     */
    @Override
    public final void drawButton(
            final Minecraft mc,
            final int mouseX,
            final int mouseY,
            final float partial
    ) {
        if (this.visible) {
            this.hovered = mouseX >= this.x
                            && mouseY >= this.y
                            && mouseX < this.x + this.width
                            && mouseY < this.y + this.height;
            int white = (this.hovered) ? OFF_WHITE_COLOR : TRUE_WHITE;

            this.drawGradientRect(
                    this.x,
                    this.y,
                    this.x + this.width,
                    this.y + this.height,
                    white,
                    white
            );
            this.drawGradientRect(
                    this.x,
                    this.y,
                    this.x + this.width,
                    this.y + 1,
                    OFF_BLACK_COLOR,
                    OFF_BLACK_COLOR
            );
            this.drawGradientRect(
                    this.x,
                    this.y + this.height,
                    this.x + this.width,
                    this.y + this.height + 1,
                    OFF_BLACK_COLOR,
                    OFF_BLACK_COLOR
            );
            this.drawGradientRect(
                    this.x + this.width,
                    this.y,
                    this.x + this.width + 1,
                    this.y + this.height + 1,
                    OFF_BLACK_COLOR,
                    OFF_BLACK_COLOR
            );
            this.drawGradientRect(
                    this.x,
                    this.y,
                    this.x + 1,
                    this.y + this.height,
                    OFF_BLACK_COLOR,
                    OFF_BLACK_COLOR
            );

            Minecraft.getMinecraft().fontRenderer.drawString(
                    this.displayString,
                    this.x + TEXT_POS_MOD,
                    this.y + (this.height / TEXT_Y_MOD) - TEXT_POS_MOD,
                    OFF_BLACK_COLOR,
                    false
            );
        }
    }

    //-----This:----------------------------------------//

    /**
     * @return Returns the function of this context button.
     */
    public final Editor getFunction() {
        return this.function;
    }
}

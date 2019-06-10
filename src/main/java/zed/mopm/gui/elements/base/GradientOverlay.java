package zed.mopm.gui.elements.base;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import static zed.mopm.gui.utils.constants.ColorConsts.PURE_BLACK;
import static zed.mopm.gui.utils.constants.ColorConsts.TRANSPARENT;

public class GradientOverlay extends Gui {

    //-----Consts:--------------------------------------//

    /**
     * The Thickness of the gradient.
     */
    private static final int THICKNESS = 20;

    //-----Fields:--------------------------------------//

    /**
     * The width of the gradient overlay.
     */
    private int width;
    /**
     * The height of the gradient overlay.
     */
    private int height;

    //-----Constructors:--------------------------------//

    /**
     * Creates a new gradient overlay.
     * @param w The width of the overlay.
     * @param h The height of the overlay.
     */
    public GradientOverlay(final int w, final int h) {
        this.width = w;
        this.height = h;
    }

    //-----This:-----------------------------------------//

    /**
     * Draws the gradient overlay.
     * @param parentIn The screen the overlay will be drawn on.
     */
    public final void draw(final GuiScreen parentIn) {
        this.drawGradientRect(
                0,
                0,
                this.width,
                THICKNESS,
                PURE_BLACK,
                TRANSPARENT
        );
        this.drawGradientRect(
                this.width,
                this.height,
                0,
                this.height - THICKNESS,
                PURE_BLACK,
                TRANSPARENT
        );
        MinecraftForge.EVENT_BUS.post(
                new GuiScreenEvent.BackgroundDrawnEvent(parentIn)
        );
    }
}
